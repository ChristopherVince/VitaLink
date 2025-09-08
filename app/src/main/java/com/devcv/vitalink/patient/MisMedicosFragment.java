package com.devcv.vitalink.patient;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devcv.vitalink.R;
import com.devcv.vitalink.models.Medico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class MisMedicosFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicoAdapter adapter;
    private List<Medico> medicoList;
    private Button buttonVincular;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    public MisMedicosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_medicos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialización
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        buttonVincular = view.findViewById(R.id.buttonVincularMedico);
        recyclerView = view.findViewById(R.id.recyclerViewMedicos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medicoList = new ArrayList<>();
        adapter = new MedicoAdapter(medicoList);
        recyclerView.setAdapter(adapter);

        loadMedicosVinculados();

        buttonVincular.setOnClickListener(v -> showVincularDialog());
    }

    private void loadMedicosVinculados() {
        String patientId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(patientId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("medicosVinculados")) {
                List<String> medicoIds = (List<String>) documentSnapshot.get("medicosVinculados");
                if (medicoIds != null && !medicoIds.isEmpty()) {
                    medicoList.clear(); // Limpiamos la lista antes de llenarla
                    for (String medicoId : medicoIds) {
                        // Por cada ID de médico, obtenemos su información
                        db.collection("users").document(medicoId).get().addOnSuccessListener(medicoDoc -> {
                            if (medicoDoc.exists()) {
                                String name = medicoDoc.getString("fullName");
                                String specialty = medicoDoc.getString("specialty"); // Leemos la especialidad
                                medicoList.add(new Medico(name, specialty));
                                adapter.notifyDataSetChanged(); // Notificamos al adaptador
                            }
                        });
                    }
                }
            }
        });
    }

    private void showVincularDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Vincular con Médico");
        builder.setMessage("Pide a tu médico el código de vinculación e ingrésalo aquí.");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Vincular", (dialog, which) -> {
            String code = input.getText().toString().trim().toUpperCase(); // Convertimos a mayúsculas
            if (!code.isEmpty()) {
                validateCodeAndLink(code); // Llamamos al nuevo método de validación
            } else {
                Toast.makeText(getContext(), "Por favor, ingresa un código.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // NUEVO MÉTODO PARA VALIDAR EL CÓDIGO Y REALIZAR LA VINCULACIÓN
    private void validateCodeAndLink(final String code) {
        // 1. Buscamos en la colección de códigos
        db.collection("linkingCodes")
                .whereEqualTo("code", code)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Código encontrado, ahora validamos
                        DocumentSnapshot codeDocument = task.getResult().getDocuments().get(0);
                        Timestamp expiresAt = codeDocument.getTimestamp("expiresAt");

                        // 2. Verificamos que no haya expirado
                        if (expiresAt != null && Timestamp.now().compareTo(expiresAt) < 0) {
                            // Código válido y no expirado
                            String doctorId = codeDocument.getString("doctorId");
                            String patientId = mAuth.getCurrentUser().getUid();

                            // 3. Actualizamos ambos documentos
                            // Añadimos el ID del médico al array del paciente
                            db.collection("users").document(patientId)
                                    .update("medicosVinculados", FieldValue.arrayUnion(doctorId));

                            // Añadimos el ID del paciente al array del médico
                            db.collection("users").document(doctorId)
                                    .update("pacientesVinculados", FieldValue.arrayUnion(patientId));

                            // 4. Eliminamos el código para que no se reutilice
                            codeDocument.getReference().delete();

                            Toast.makeText(getContext(), "¡Vinculación exitosa!", Toast.LENGTH_SHORT).show();
                            loadMedicosVinculados(); // Recargamos la lista para mostrar al nuevo médico
                        } else {
                            // El código ha expirado
                            Toast.makeText(getContext(), "El código ha expirado. Pide a tu médico que genere uno nuevo.", Toast.LENGTH_LONG).show();
                            codeDocument.getReference().delete(); // Borramos el código expirado
                        }
                    } else {
                        // El código no se encontró
                        Toast.makeText(getContext(), "Código de vinculación no válido.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
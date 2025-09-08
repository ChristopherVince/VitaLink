package com.devcv.vitalink.doctor;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devcv.vitalink.R;
import com.devcv.vitalink.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DoctorPacientesFragment extends Fragment {

    private FloatingActionButton fabAddPatient;
    private RecyclerView recyclerView;
    private PacienteAdapter adapter;
    private List<User> pacienteList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public DoctorPacientesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_pacientes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialización
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pacienteList = new ArrayList<>();
        adapter = new PacienteAdapter(pacienteList);
        recyclerView = view.findViewById(R.id.recyclerViewPacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        fabAddPatient = view.findViewById(R.id.fabAddPatient);

        fabAddPatient.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DoctorAddPatientActivity.class));
        });

        loadPacientes();
    }

    private void loadPacientes() {
        String doctorId = mAuth.getCurrentUser().getUid();

        // Escuchamos cambios en el documento del doctor en tiempo real
        db.collection("users").document(doctorId).addSnapshotListener((doctorDocument, error) -> {
            if (error != null) {
                Log.e("DoctorPacientes", "Error al escuchar cambios del doctor", error);
                return;
            }

            if (doctorDocument != null && doctorDocument.exists() && doctorDocument.contains("pacientesVinculados")) {
                List<String> patientIds = (List<String>) doctorDocument.get("pacientesVinculados");
                pacienteList.clear(); // Limpiamos la lista para refrescarla

                if (patientIds != null && !patientIds.isEmpty()) {
                    // Usamos la misma consulta 'whereIn' para obtener los detalles
                    db.collection("users").whereIn(FieldPath.documentId(), patientIds).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot patientDocument : task.getResult()) {
                                        User paciente = patientDocument.toObject(User.class);
                                        pacienteList.add(paciente);
                                    }
                                    adapter.notifyDataSetChanged(); // Notificamos al adaptador
                                } else {
                                    Log.d("DoctorPacientes", "Error al obtener documentos de pacientes: ", task.getException());
                                }
                            });
                } else {
                    adapter.notifyDataSetChanged(); // Notificamos por si la lista quedó vacía
                }
            }
        });
    }
}
package com.devcv.vitalink.patient;

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
import com.devcv.vitalink.models.Tratamiento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.widget.Toast;

public class TratamientosFragment extends Fragment implements TratamientoAdapter.OnItemLongClickListener {


    // 1. Declaramos las variables
    private RecyclerView recyclerView;
    private TratamientoAdapter adapter;
    private List<Tratamiento> tratamientoList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public TratamientosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tratamientos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Inicialización
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewTratamientos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tratamientoList = new ArrayList<>();
        adapter = new TratamientoAdapter(tratamientoList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddTratamiento);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddTratamientoActivity.class));
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        // 3. Cargamos los datos desde Firestore CADA VEZ que el fragmento es visible
        loadTratamientos();
    }

    private void loadTratamientos() {
        // Verificamos si el usuario está logueado antes de hacer la consulta
        if (mAuth.getCurrentUser() == null) {
            Log.e("DEBUG", "Error: Usuario no autenticado.");
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        Log.d("DEBUG", "Iniciando carga de tratamientos para el usuario: " + userId);

        db.collection("users").document(userId).collection("treatments")
                .orderBy("horaInicio", Query.Direction.ASCENDING) // Asegúrate que el campo se llama 'horaInicio' en Firestore
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("DEBUG", "Error al escuchar cambios en Firestore: ", error);
                        return;
                    }

                    if (value == null) {
                        Log.d("DEBUG", "El snapshot de Firestore es nulo.");
                        return;
                    }

                    tratamientoList.clear(); // Limpiamos la lista para no duplicar datos
                    Log.d("DEBUG", "Snapshot recibido. Número de documentos: " + value.size());

                    for (QueryDocumentSnapshot doc : value) {
                        Tratamiento tratamiento = doc.toObject(Tratamiento.class);
                        tratamiento.setIdDocumento(doc.getId());

                        // Imprimimos los datos de cada tratamiento recuperado
                        Log.d("DEBUG", "Tratamiento recuperado: " + tratamiento.getNombreMedicamento() + " a las " + tratamiento.getHoraInicio());

                        tratamientoList.add(tratamiento);
                    }

                    Log.d("DEBUG", "Lista actualizada. Tamaño final: " + tratamientoList.size());

                    // Notificamos al adaptador que los datos han cambiado
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", "Adaptador notificado.");
                });
    }

    @Override
    public void onItemLongClick(Tratamiento tratamiento) {
        // Creamos un diálogo de confirmación
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Tratamiento")
                .setMessage("¿Estás seguro de que quieres eliminar " + tratamiento.getNombreMedicamento() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Si el usuario confirma, procedemos a borrar
                    deleteTratamiento(tratamiento.getIdDocumento());
                })
                .setNegativeButton("Cancelar", null) // No hace nada al cancelar
                .show();
    }

    // En el método deleteTratamiento dentro de TratamientosFragment.java

    private void deleteTratamiento(String tratamientoId) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("treatments").document(tratamientoId)
                .delete()
                .addOnSuccessListener(aVoid -> { // <-- Añadir llave de apertura
                    Toast.makeText(getContext(), "Tratamiento eliminado", Toast.LENGTH_SHORT).show();
                }) // <-- Añadir llave de cierre
                .addOnFailureListener(e -> { // <-- Añadir llave de apertura
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }); // <-- Añadir llave de cierre y punto y coma final
    }
}
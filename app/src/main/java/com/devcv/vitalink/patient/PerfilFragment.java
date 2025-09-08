package com.devcv.vitalink.patient;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devcv.vitalink.R;
import com.devcv.vitalink.auth.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilFragment extends Fragment {

    // 1. Declaramos los elementos de la UI y de Firebase
    private TextView textViewProfileName, textViewProfileEmail;
    private Button buttonLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    // Usamos onViewCreated para asegurarnos que la vista ya fue creada antes de manipularla
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Inicializamos Firebase y enlazamos los elementos de la UI
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        textViewProfileName = view.findViewById(R.id.textViewProfileName);
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        // 3. Cargamos los datos del usuario
        loadUserProfile();

        // 4. Configuramos el botón de Cerrar Sesión
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();

                // Llevamos al usuario de vuelta a la pantalla de Login
                Intent intent = new Intent(getActivity(), MainActivity.class);
                // Estas 'flags' limpian el historial de pantallas, para que no pueda volver atrás
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Creamos una referencia al documento del usuario en Firestore
            DocumentReference docRef = db.collection("users").document(userId);

            // Obtenemos el documento
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Si el documento existe, obtenemos los datos y los mostramos
                            Log.d("PerfilFragment", "DocumentSnapshot data: " + document.getData());
                            String name = document.getString("fullName");
                            String email = document.getString("email");

                            textViewProfileName.setText(name);
                            textViewProfileEmail.setText(email);
                        } else {
                            Log.d("PerfilFragment", "No such document");
                        }
                    } else {
                        Log.d("PerfilFragment", "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}
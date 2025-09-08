package com.devcv.vitalink.doctor;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.devcv.vitalink.R;
import com.devcv.vitalink.auth.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorPerfilFragment extends Fragment {

    private TextView textViewName, textViewEmail, textViewSpecialty;
    private Button buttonLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public DoctorPerfilFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialización
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        textViewName = view.findViewById(R.id.textViewDoctorName);
        textViewEmail = view.findViewById(R.id.textViewDoctorEmail);
        textViewSpecialty = view.findViewById(R.id.textViewDoctorSpecialty);
        buttonLogout = view.findViewById(R.id.buttonDoctorLogout);

        loadDoctorProfile();

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadDoctorProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String doctorId = currentUser.getUid();
            DocumentReference docRef = db.collection("users").document(doctorId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    textViewName.setText(documentSnapshot.getString("fullName"));
                    textViewEmail.setText(documentSnapshot.getString("email"));
                    textViewSpecialty.setText(documentSnapshot.getString("specialty"));
                }
            });
        }
    }
}
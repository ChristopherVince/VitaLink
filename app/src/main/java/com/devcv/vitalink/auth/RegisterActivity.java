package com.devcv.vitalink.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devcv.vitalink.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// 1. Importamos las clases de Firestore
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private FirebaseAuth mAuth;

    // 2. Declaramos la instancia de Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // 3. Inicializamos Firestore
        db = FirebaseFirestore.getInstance();

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenemos el nombre para pasarlo al método de registro
                final String name = editTextFullName.getText().toString().trim();
                final String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (name.isEmpty()){
                    editTextFullName.setError("El nombre es requerido");
                    return;
                }
                if (email.isEmpty()){
                    editTextEmail.setError("El correo es requerido");
                    return;
                }
                if (password.isEmpty() || password.length() < 6){
                    editTextPassword.setError("La contraseña es requerida (mín. 6 caracteres)");
                    return;
                }

                registrarUsuario(name, email, password);
            }
        });
    }

    private void registrarUsuario(final String name, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Usuario registrado exitosamente.", Toast.LENGTH_SHORT).show();

                            // 4. Si el registro es exitoso, guardamos los datos en Firestore
                            FirebaseUser user = mAuth.getCurrentUser();
                            guardarDatosDeUsuario(user.getUid(), name, email);

                        } else {
                            Log.w("REGISTER_FAIL", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "El registro falló: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // 5. Nuevo método para guardar la información en Firestore
    private void guardarDatosDeUsuario(String userId, String name, String email) {
        // Creamos un Map para guardar los datos
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("email", email);
        userData.put("role", "paciente"); // Asignamos el rol por defecto

        // Accedemos a la colección "users" y creamos un documento con el ID del usuario
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FIRESTORE_SUCCESS", "Datos del usuario guardados exitosamente.");
                        // Cerramos la actividad de registro para volver al login
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FIRESTORE_FAIL", "Error al guardar datos del usuario", e);
                        Toast.makeText(RegisterActivity.this, "Error al guardar información adicional.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.devcv.vitalink.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast; // Importamos Toast para mostrar mensajes

// Importamos las clases de Firebase Auth
import com.devcv.vitalink.R;
import com.devcv.vitalink.patient.HomeActivity;
import com.devcv.vitalink.doctor.DoctorHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;

    // 1. Declaramos la instancia de FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Inicializamos la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // 3. Creamos el evento de clic para el botón de login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenemos el email y la contraseña de los EditText
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validamos que los campos no estén vacíos
                if (email.isEmpty()) {
                    editTextEmail.setError("El correo es requerido");
                    return; // Detenemos la ejecución si el campo está vacío
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("La contraseña es requerida");
                    return; // Detenemos la ejecución si el campo está vacío
                }

                // Si los campos son válidos, iniciamos sesión con Firebase
                iniciarSesion(email, password);
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos un Intent para abrir RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    // 4. Creamos un método para manejar el inicio de sesión
    private void iniciarSesion(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si la autenticación es correcta, ahora verificamos el rol
                            verificarRolUsuario(task.getResult().getUser().getUid());
                        } else {
                            Log.w("LOGIN_FAIL", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Autenticación fallida.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void verificarRolUsuario(String userId) {
        // Obtenemos la instancia de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Creamos una referencia al documento del usuario
        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtenemos el campo "role" del documento
                        String role = document.getString("role");

                        // Comparamos el rol y redirigimos a la actividad correspondiente
                        if ("paciente".equals(role)) {
                            Toast.makeText(MainActivity.this, "¡Bienvenido, Paciente!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
                        } else if ("medico".equals(role)) {
                            Toast.makeText(MainActivity.this, "¡Bienvenido, Doctor!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, DoctorHomeActivity.class));
                            finish();
                        } else {
                            // Rol no definido o nulo
                            Toast.makeText(MainActivity.this, "Rol de usuario no reconocido.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No se encontraron datos de usuario.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al verificar el rol.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
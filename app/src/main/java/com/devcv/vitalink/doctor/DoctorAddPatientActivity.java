package com.devcv.vitalink.doctor;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.devcv.vitalink.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DoctorAddPatientActivity extends AppCompatActivity {

    private TextView textViewGeneratedCode;
    private Button buttonGenerateCode;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_patient);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        textViewGeneratedCode = findViewById(R.id.textViewGeneratedCode);
        buttonGenerateCode = findViewById(R.id.buttonGenerateCode);

        buttonGenerateCode.setOnClickListener(v -> generateAndSaveCode());
    }

    private void generateAndSaveCode() {
        String doctorId = mAuth.getCurrentUser().getUid();
        String code = generateRandomCode();

        // El código expira en 10 minutos (600,000 milisegundos)
        long expirationMillis = System.currentTimeMillis() + 600000;
        Timestamp expirationTimestamp = new Timestamp(new Date(expirationMillis));

        Map<String, Object> codeData = new HashMap<>();
        codeData.put("doctorId", doctorId);
        codeData.put("code", code);
        codeData.put("expiresAt", expirationTimestamp);

        // Guardamos el código en la nueva colección
        db.collection("linkingCodes").add(codeData)
                .addOnSuccessListener(documentReference -> {
                    // Mostramos el código en la pantalla
                    textViewGeneratedCode.setText(code);
                    buttonGenerateCode.setEnabled(false); // Deshabilitamos el botón para no generar más códigos
                    Toast.makeText(this, "Código generado. Compártelo con tu paciente.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al generar el código.", Toast.LENGTH_SHORT).show();
                });
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // 6 caracteres de longitud
            int index = (int) (rnd.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }
        // Formateamos para que sea más legible, ej: "ABC-DEF"
        String code = salt.toString();
        return code.substring(0, 3) + "-" + code.substring(3);
    }
}
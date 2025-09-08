package com.devcv.vitalink.patient;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.devcv.vitalink.R;
import com.devcv.vitalink.models.Tratamiento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Locale;

public class AddTratamientoActivity extends AppCompatActivity {

    private EditText editTextMedicineName, editTextDose;
    private TimePicker timePicker;
    private Button buttonSave;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tratamiento);

        // Inicialización de Firebase y Vistas
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        editTextMedicineName = findViewById(R.id.editTextMedicineName);
        editTextDose = findViewById(R.id.editTextDose);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true); // Usamos formato de 24 horas
        buttonSave = findViewById(R.id.buttonSaveTratamiento);

        buttonSave.setOnClickListener(v -> saveTratamiento());
    }

    private void saveTratamiento() {
        String medicineName = editTextMedicineName.getText().toString().trim();
        String dose = editTextDose.getText().toString().trim();
        EditText editTextFrecuencia = findViewById(R.id.editTextFrecuencia);
        String frecuenciaStr = editTextFrecuencia.getText().toString().trim();
        int frecuencia = 0; // Por defecto es 0 (toma única)
        if (!frecuenciaStr.isEmpty()) {
            frecuencia = Integer.parseInt(frecuenciaStr);
        }
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Validación
        if (medicineName.isEmpty()) {
            editTextMedicineName.setError("El nombre del medicamento es requerido");
            return;
        }

        // Formateamos la hora a un string como "08:05"
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

        // Obtenemos el ID del usuario actual
        String userId = mAuth.getCurrentUser().getUid();

        // Creamos una instancia de nuestro modelo Tratamiento
        Tratamiento tratamiento = new Tratamiento(medicineName, dose, time, frecuencia);

        // Guardamos el tratamiento en la subcolección del usuario
        db.collection("users").document(userId).collection("treatments")
                .add(tratamiento) // .add() crea un documento con ID automático
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tratamiento guardado", Toast.LENGTH_SHORT).show();
                    // Cerramos la actividad para volver a la lista
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el tratamiento", Toast.LENGTH_SHORT).show();
                });
    }
}
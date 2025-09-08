package com.devcv.vitalink.doctor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.devcv.vitalink.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// La clase extiende AppCompatActivity, que es correcto
public class DoctorAddCitaActivity extends AppCompatActivity {

    // Las variables son las mismas
    private Spinner spinnerPatients;
    private CalendarView calendarView;
    private EditText editTextTime, editTextReason;
    private Button buttonSave;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<String> patientNames = new ArrayList<>();
    private List<String> patientIds = new ArrayList<>();
    private String selectedPatientId = null;
    private String selectedPatientName = null;
    private String selectedDate = null;

    // El método principal en una Activity es onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_add_cita); // Se establece el layout

        // Inicialización (se llama directamente a findViewById)
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        spinnerPatients = findViewById(R.id.spinnerPatients);
        calendarView = findViewById(R.id.calendarViewDoctor);
        editTextTime = findViewById(R.id.editTextAppointmentTime);
        editTextReason = findViewById(R.id.editTextAppointmentReason);
        buttonSave = findViewById(R.id.buttonSaveAppointment);

        // La lógica que estaba en onViewCreated ahora va aquí en onCreate
        loadPatients();

        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        spinnerPatients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedPatientId = patientIds.get(position - 1);
                    selectedPatientName = patientNames.get(position);
                } else {
                    selectedPatientId = null;
                    selectedPatientName = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        buttonSave.setOnClickListener(v -> saveAppointment());
    }

    // El resto de los métodos no cambia, solo las llamadas a getContext()
    private void loadPatients() {
        String doctorId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(doctorId).get().addOnSuccessListener(doctorDocument -> {
            if (doctorDocument.exists() && doctorDocument.contains("pacientesVinculados")) {
                List<String> linkedPatientIds = (List<String>) doctorDocument.get("pacientesVinculados");
                patientNames.clear();
                patientIds.clear();
                patientNames.add("Seleccione un paciente...");

                if (linkedPatientIds != null && !linkedPatientIds.isEmpty()) {
                    db.collection("users").whereIn(FieldPath.documentId(), linkedPatientIds).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot patientDocument : task.getResult()) {
                                        patientNames.add(patientDocument.getString("fullName"));
                                        patientIds.add(patientDocument.getId());
                                    }
                                    // Se usa 'this' en lugar de 'getContext()'
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patientNames);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerPatients.setAdapter(adapter);
                                }
                            });
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patientNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPatients.setAdapter(adapter);
                }
            }
        });
    }

    private void saveAppointment() {
        String time = editTextTime.getText().toString().trim();
        String reason = editTextReason.getText().toString().trim();
        String doctorId = mAuth.getCurrentUser().getUid();

        if (selectedPatientId == null) {
            // Se usa 'this' en lugar de 'getContext()'
            Toast.makeText(this, "Por favor, seleccione un paciente.", Toast.LENGTH_SHORT).show();
            return;
        }
        // ... (el resto de la lógica de guardado es igual)
        if (selectedDate == null) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
            selectedDate = dateFormat.format(c.getTime());
        }
        if (time.isEmpty()) {
            editTextTime.setError("La hora es requerida");
            return;
        }

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("patientId", selectedPatientId);
        appointment.put("patientName", selectedPatientName);
        appointment.put("doctorId", doctorId);
        appointment.put("date", selectedDate);
        appointment.put("time", time);
        appointment.put("reason", reason);

        db.collection("appointments").add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Cita guardada exitosamente.", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad al guardar
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar la cita.", Toast.LENGTH_SHORT).show());
    }
}
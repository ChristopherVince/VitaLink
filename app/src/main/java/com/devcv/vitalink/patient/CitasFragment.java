package com.devcv.vitalink.patient;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import com.devcv.vitalink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CitasFragment extends Fragment {

    // 1. Declaramos las variables de UI y Firebase
    private CalendarView calendarView;
    private TextView textViewCitaInfo;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public CitasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_citas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Inicializamos Firebase y enlazamos vistas
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        calendarView = view.findViewById(R.id.calendarView);
        textViewCitaInfo = view.findViewById(R.id.textViewCitaInfo);

        // 3. Listener para cuando cambia la fecha
        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            // Cuando el usuario selecciona una fecha, buscamos sus citas
            fetchAppointmentForDate(selectedDate);
        });

        // Para una mejor experiencia, cargamos las citas del día actual al abrir la pantalla
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        String today = dateFormat.format(c.getTime());
        fetchAppointmentForDate(today);
    }

    // 4. Nuevo método para buscar y mostrar las citas
    private void fetchAppointmentForDate(String date) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        textViewCitaInfo.setText("Buscando citas para el " + date + "...");

        // 5. Creamos la consulta a Firestore
        db.collection("appointments")
                .whereEqualTo("patientId", currentUserId) // Filtro: solo citas de este paciente
                .whereEqualTo("date", date)              // Filtro: y solo para la fecha seleccionada
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Si la consulta no devuelve documentos
                            textViewCitaInfo.setText("No hay citas programadas para el " + date);
                        } else {
                            // Si encuentra citas, las mostramos
                            StringBuilder appointmentsText = new StringBuilder("Citas para el " + date + ":\n\n");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String time = document.getString("time");
                                String reason = document.getString("reason");
                                appointmentsText.append("• Hora: ").append(time).append("\n");
                                appointmentsText.append("  Motivo: ").append(reason).append("\n\n");
                            }
                            textViewCitaInfo.setText(appointmentsText.toString());
                        }
                    } else {
                        textViewCitaInfo.setText("Error al cargar las citas.");
                        Log.w("CitasFragment", "Error getting documents.", task.getException());
                    }
                });
    }
}
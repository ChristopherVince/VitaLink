package com.devcv.vitalink.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.devcv.vitalink.R;
import com.devcv.vitalink.models.Appointment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.devcv.vitalink.doctor.CitaAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DoctorCitasFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView recyclerViewCitas;
    private FloatingActionButton fabAddCita;
    private CitaAdapter adapter;
    private List<Appointment> appointmentList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "DoctorCitasFragment";

    public DoctorCitasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.fragment_doctor_citas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicialización de Firebase y las vistas de la UI
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        calendarView = view.findViewById(R.id.calendarViewDoctor);
        recyclerViewCitas = view.findViewById(R.id.recyclerViewCitasDelDia);
        fabAddCita = view.findViewById(R.id.fabAddCita);
        appointmentList = new ArrayList<>();
        adapter = new CitaAdapter(appointmentList);

        // 2. Configuración del RecyclerView
        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCitas.setAdapter(adapter);

        // 3. Listener para el calendario para cargar citas al seleccionar un día
        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            loadAppointmentsForDate(selectedDate);
        });

        // 4. Listener para el botón flotante para abrir la pantalla de añadir cita
        fabAddCita.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DoctorAddCitaActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 5. Carga las citas para el día actual cada vez que el fragmento se vuelve visible
        loadAppointmentsForToday();
    }

    private void loadAppointmentsForToday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        String today = dateFormat.format(calendar.getTime());
        loadAppointmentsForDate(today);
    }

    private void loadAppointmentsForDate(String date) {
        if (mAuth.getCurrentUser() == null) {
            Log.e(TAG, "No hay un médico autenticado.");
            return;
        }
        String doctorId = mAuth.getCurrentUser().getUid();

        // Limpiamos la lista antes de cada nueva consulta
        appointmentList.clear();

        // 6. Consulta a Firestore para obtener las citas del médico para la fecha seleccionada
        db.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("date", date)
                .orderBy("time", Query.Direction.ASCENDING) // Ordena las citas por hora
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointmentList.add(appointment);
                        }
                        // Notifica al adaptador que los datos han cambiado para que actualice la lista
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Citas cargadas para " + date + ": " + appointmentList.size());
                    } else {
                        Log.w(TAG, "Error al obtener las citas.", task.getException());
                    }
                });
    }
}
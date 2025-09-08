package com.devcv.vitalink.doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devcv.vitalink.R;
import com.devcv.vitalink.models.Appointment;
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private List<Appointment> appointmentList;

    // Constructor que recibe la lista de citas
    public CitaAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    // Este método crea la vista para cada fila (item)
    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout 'item_cita.xml' que define cómo se ve una fila
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    // Este método conecta los datos de una cita específica con la vista
    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.patientName.setText(appointment.getPatientName());
        holder.appointmentTime.setText("Hora: " + appointment.getTime());
        holder.appointmentReason.setText("Motivo: " + appointment.getReason());
    }

    // Devuelve el número total de citas en la lista
    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    // Esta clase interna contiene las referencias a los TextViews de 'item_cita.xml'
    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, appointmentTime, appointmentReason;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.textViewPatientName);
            appointmentTime = itemView.findViewById(R.id.textViewAppointmentTime);
            appointmentReason = itemView.findViewById(R.id.textViewAppointmentReason);
        }
    }
}
package com.devcv.vitalink.doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devcv.vitalink.R;
import com.devcv.vitalink.models.User; // Usaremos un modelo genérico
import java.util.List;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder> {
    private List<User> pacienteList;

    public PacienteAdapter(List<User> pacienteList) {
        this.pacienteList = pacienteList;
    }

    @NonNull
    @Override
    public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paciente, parent, false);
        return new PacienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder holder, int position) {
        User paciente = pacienteList.get(position);
        holder.patientName.setText(paciente.getFullName());
        holder.patientEmail.setText(paciente.getEmail());
    }

    @Override
    public int getItemCount() {
        return pacienteList.size();
    }

    public static class PacienteViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientEmail;
        public PacienteViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.textViewPatientName);
            patientEmail = itemView.findViewById(R.id.textViewPatientEmail);
        }
    }
}
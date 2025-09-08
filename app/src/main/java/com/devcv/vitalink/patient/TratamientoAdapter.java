package com.devcv.vitalink.patient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devcv.vitalink.R;
import com.devcv.vitalink.models.Tratamiento;
import java.util.List;

public class TratamientoAdapter extends RecyclerView.Adapter<TratamientoAdapter.TratamientoViewHolder> {

    private List<Tratamiento> tratamientoList;
    private OnItemLongClickListener listener;

    // 1. Aquí definimos la interfaz DENTRO de la clase del adaptador
    public interface OnItemLongClickListener {
        void onItemLongClick(Tratamiento tratamiento);
    }

    // 2. El constructor usa el nombre correcto de la interfaz
    public TratamientoAdapter(List<Tratamiento> tratamientoList, OnItemLongClickListener itemListener) {
        this.tratamientoList = tratamientoList;
        this.listener = itemListener;
    }

    @NonNull
    @Override
    public TratamientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tratamiento, parent, false);
        return new TratamientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TratamientoViewHolder holder, int position) {
        Tratamiento tratamiento = tratamientoList.get(position);
        holder.medicineName.setText(tratamiento.getNombreMedicamento());
        holder.dose.setText(tratamiento.getDosis());

        if (tratamiento.getFrecuenciaHoras() <= 0) {
            holder.timeInfo.setText("Toma única: " + tratamiento.getHoraInicio());
        } else {
            holder.timeInfo.setText("Desde las " + tratamiento.getHoraInicio() + ", cada " + tratamiento.getFrecuenciaHoras() + " horas");
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(tratamiento);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tratamientoList.size();
    }

    public static class TratamientoViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, dose, timeInfo;

        public TratamientoViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.textViewMedicineName);
            dose = itemView.findViewById(R.id.textViewDose);
            timeInfo = itemView.findViewById(R.id.textViewTimeInfo);
        }
    }
}
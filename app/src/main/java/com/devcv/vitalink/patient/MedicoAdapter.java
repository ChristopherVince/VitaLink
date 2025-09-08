package com.devcv.vitalink.patient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devcv.vitalink.R;
import com.devcv.vitalink.models.Medico;
import java.util.List;

public class MedicoAdapter extends RecyclerView.Adapter<MedicoAdapter.MedicoViewHolder> {

    private List<Medico> medicoList;

    public MedicoAdapter(List<Medico> medicoList) {
        this.medicoList = medicoList;
    }

    @NonNull
    @Override
    public MedicoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medico, parent, false);
        return new MedicoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicoViewHolder holder, int position) {
        Medico medico = medicoList.get(position);
        holder.medicoName.setText(medico.getFullName());
        holder.medicoSpecialty.setText(medico.getSpecialty());
    }

    @Override
    public int getItemCount() {
        return medicoList.size();
    }

    public static class MedicoViewHolder extends RecyclerView.ViewHolder {
        TextView medicoName, medicoSpecialty;

        public MedicoViewHolder(@NonNull View itemView) {
            super(itemView);
            medicoName = itemView.findViewById(R.id.textViewMedicoName);
            medicoSpecialty = itemView.findViewById(R.id.textViewMedicoSpecialty);
        }
    }
}

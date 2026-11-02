package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ProfesorAdapter extends RecyclerView.Adapter<ProfesorAdapter.ProfesorViewHolder> {

    public interface OnProfesorClickListener {
        void onProfesorClick(ProfesorDTO profesor);
    }

    private List<ProfesorDTO> lista = new ArrayList<>();
    private final OnProfesorClickListener listener;

    public ProfesorAdapter(OnProfesorClickListener listener) {
        this.listener = listener;
    }

    public void setProfesores(List<ProfesorDTO> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfesorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profesor, parent, false);
        return new ProfesorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfesorViewHolder holder, int position) {
        ProfesorDTO p = lista.get(position);

        // 1. Iniciales (ej: Laura Méndez -> LM)
        holder.tvIniciales.setText(calcularIniciales(p.getNombre()));

        // 2. Nombre
        holder.tvNombreProfesor.setText(p.getNombre());

        // 3. Materias
        if (p.getMaterias() != null && !p.getMaterias().isEmpty()) {
            holder.tvMateriaProfesor.setText(String.join(", ", p.getMaterias()));
        } else {
            holder.tvMateriaProfesor.setText("Sin materia asignada");
        }

        // 4. Próximo horario libre
        if (p.getProximoEspacio() != null) {
            String hora = p.getProximoEspacio().getHoraInicio();
            if (hora != null && hora.length() >= 5) {
                hora = hora.substring(0, 5);
            }
            holder.tvProximoHorario.setText("● Próximo: " + p.getProximoEspacio().getFecha() + " · " + hora);
            holder.tvProximoHorario.setTextColor(0xFF059669); // Verde
        } else {
            holder.tvProximoHorario.setText("● Sin espacios disponibles");
            holder.tvProximoHorario.setTextColor(0xFF6B7280); // Gris
        }

        // Clic en la tarjeta
        holder.cardProfesor.setOnClickListener(v -> {
            if (listener != null) listener.onProfesorClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private String calcularIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "PR";
        String[] partes = nombre.replace("Dra.", "").replace("Dr.", "").replace("Prof.", "").trim().split("\\s+");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        } else if (partes.length == 1 && !partes[0].isEmpty()) {
            return ("" + partes[0].charAt(0)).toUpperCase();
        }
        return "PR";
    }

    static class ProfesorViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardProfesor;
        TextView tvIniciales;
        TextView tvNombreProfesor;
        TextView tvMateriaProfesor;
        TextView tvProximoHorario;

        public ProfesorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProfesor = itemView.findViewById(R.id.cardProfesor);
            tvIniciales = itemView.findViewById(R.id.tvIniciales);
            tvNombreProfesor = itemView.findViewById(R.id.tvNombreProfesor);
            tvMateriaProfesor = itemView.findViewById(R.id.tvMateriaProfesor);
            tvProximoHorario = itemView.findViewById(R.id.tvProximoHorario);
        }
    }
}

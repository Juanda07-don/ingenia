package com.example.myapplication;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class AgendaProfesorAdapter extends RecyclerView.Adapter<AgendaProfesorAdapter.AgendaViewHolder> {

    public interface OnProfesorAsesoriaListener {
        void onReprogramarClick(AsesoriaDTO asesoria);
        void onItemClick(AsesoriaDTO asesoria);
    }

    private List<AsesoriaDTO> lista = new ArrayList<>();
    private final OnProfesorAsesoriaListener listener;

    public AgendaProfesorAdapter(OnProfesorAsesoriaListener listener) {
        this.listener = listener;
    }

    public void setAsesorias(List<AsesoriaDTO> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asesoria_profesor, parent, false);
        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        AsesoriaDTO a = lista.get(position);

        holder.tvProfMateria.setText(a.getMateria());

        String est = a.getEstudiante() != null ? a.getEstudiante() : "Estudiante UAO";
        holder.tvProfEstudiante.setText("Estudiante: " + est);

        String inicio = a.getHoraInicio() != null && a.getHoraInicio().length() >= 5 ? a.getHoraInicio().substring(0, 5) : a.getHoraInicio();
        String fin = a.getHoraFin() != null && a.getHoraFin().length() >= 5 ? a.getHoraFin().substring(0, 5) : a.getHoraFin();
        holder.tvProfHorario.setText("🕒  " + a.getFecha() + " · " + inicio + "–" + fin);

        String lugar = a.getLugar() != null && !a.getLugar().isEmpty() ? a.getLugar() : "Cubículo docente";
        holder.tvProfLugar.setText("📍  " + lugar);

        // Estilos del Badge
        configurarBadge(holder.tvProfEstadoBadge, a.getEstado());

        // El botón Reprogramar solo aplica si la asesoría está pendiente (AGENDADA o REPROGRAMADA)
        String st = a.getEstado() != null ? a.getEstado().toUpperCase() : "";
        if ("AGENDADA".equals(st) || "REPROGRAMADA".equals(st)) {
            holder.btnReprogramarItem.setVisibility(View.VISIBLE);
            holder.btnReprogramarItem.setOnClickListener(v -> {
                if (listener != null) listener.onReprogramarClick(a);
            });
        } else {
            holder.btnReprogramarItem.setVisibility(View.GONE);
        }

        holder.cardAsesoriaProfesorItem.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(a);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private void configurarBadge(TextView badge, String estado) {
        if (estado == null) estado = "AGENDADA";

        String texto = estado;
        int colorTexto;
        int colorFondo;

        switch (estado.toUpperCase()) {
            case "REPROGRAMADA":
                texto = "Reprogramada";
                colorTexto = Color.parseColor("#B45309"); // Naranja
                colorFondo = Color.parseColor("#FEF3C7");
                break;

            case "FINALIZADA":
                texto = "Finalizada";
                colorTexto = Color.parseColor("#065F46"); // Verde
                colorFondo = Color.parseColor("#D1FAE5");
                break;

            case "CANCELADA":
                texto = "Cancelada";
                colorTexto = Color.parseColor("#991B1B"); // Rojo
                colorFondo = Color.parseColor("#FEE2E2");
                break;

            case "AGENDADA":
            default:
                texto = "Agendada";
                colorTexto = Color.parseColor("#1E40AF"); // Azul
                colorFondo = Color.parseColor("#DBEAFE");
                break;
        }

        badge.setText(texto);
        badge.setTextColor(colorTexto);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16);
        drawable.setColor(colorFondo);
        badge.setBackground(drawable);
    }

    static class AgendaViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAsesoriaProfesorItem;
        TextView tvProfEstadoBadge;
        TextView tvProfModalidad;
        TextView tvProfMateria;
        TextView tvProfEstudiante;
        TextView tvProfHorario;
        TextView tvProfLugar;
        MaterialButton btnReprogramarItem;

        public AgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAsesoriaProfesorItem = itemView.findViewById(R.id.cardAsesoriaProfesorItem);
            tvProfEstadoBadge = itemView.findViewById(R.id.tvProfEstadoBadge);
            tvProfModalidad = itemView.findViewById(R.id.tvProfModalidad);
            tvProfMateria = itemView.findViewById(R.id.tvProfMateria);
            tvProfEstudiante = itemView.findViewById(R.id.tvProfEstudiante);
            tvProfHorario = itemView.findViewById(R.id.tvProfHorario);
            tvProfLugar = itemView.findViewById(R.id.tvProfLugar);
            btnReprogramarItem = itemView.findViewById(R.id.btnReprogramarItem);
        }
    }
}

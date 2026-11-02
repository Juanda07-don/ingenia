package com.example.myapplication;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class AsesoriaAdapter extends RecyclerView.Adapter<AsesoriaAdapter.AsesoriaViewHolder> {

    public interface OnAsesoriaClickListener {
        void onAsesoriaClick(AsesoriaDTO asesoria);
    }

    private List<AsesoriaDTO> lista = new ArrayList<>();
    private final OnAsesoriaClickListener listener;

    public AsesoriaAdapter(OnAsesoriaClickListener listener) {
        this.listener = listener;
    }

    public void setAsesorias(List<AsesoriaDTO> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AsesoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asesoria, parent, false);
        return new AsesoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsesoriaViewHolder holder, int position) {
        AsesoriaDTO a = lista.get(position);

        holder.tvItemMateria.setText(a.getMateria());
        holder.tvItemProfesor.setText(a.getProfesor());

        // Cortar segundos si vienen (ej: "10:00:00" -> "10:00")
        String inicio = a.getHoraInicio() != null && a.getHoraInicio().length() >= 5 ? a.getHoraInicio().substring(0, 5) : a.getHoraInicio();
        String fin = a.getHoraFin() != null && a.getHoraFin().length() >= 5 ? a.getHoraFin().substring(0, 5) : a.getHoraFin();
        holder.tvItemHorario.setText("🕒  " + a.getFecha() + " · " + inicio + "–" + fin);

        String lugar = a.getLugar() != null && !a.getLugar().isEmpty() ? a.getLugar() : "Cubículo docente";
        holder.tvItemLugar.setText("📍  " + lugar);

        // Estilos del Badge según el Estado (Figma)
        configurarBadgeEstado(holder.tvItemEstadoBadge, holder.tvItemNotaExtra, a.getEstado());

        // Clic en la tarjeta
        holder.cardAsesoriaItem.setOnClickListener(v -> {
            if (listener != null) listener.onAsesoriaClick(a);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private void configurarBadgeEstado(TextView badge, TextView notaExtra, String estado) {
        if (estado == null) estado = "AGENDADA";

        String texto = estado;
        int colorTexto;
        int colorFondo;

        switch (estado.toUpperCase()) {
            case "REPROGRAMADA":
                texto = "Reprogramada";
                colorTexto = Color.parseColor("#B45309"); // Naranja
                colorFondo = Color.parseColor("#FEF3C7");
                notaExtra.setText("Nuevo horario por confirmar");
                notaExtra.setTextColor(colorTexto);
                notaExtra.setVisibility(View.VISIBLE);
                break;

            case "FINALIZADA":
                texto = "Finalizada";
                colorTexto = Color.parseColor("#065F46"); // Verde
                colorFondo = Color.parseColor("#D1FAE5");
                notaExtra.setText("Reporte disponible");
                notaExtra.setTextColor(Color.parseColor("#8C233E"));
                notaExtra.setVisibility(View.VISIBLE);
                break;

            case "CANCELADA":
                texto = "Cancelada";
                colorTexto = Color.parseColor("#991B1B"); // Rojo
                colorFondo = Color.parseColor("#FEE2E2");
                notaExtra.setText("Cancelada por el docente");
                notaExtra.setTextColor(colorTexto);
                notaExtra.setVisibility(View.VISIBLE);
                break;

            case "AGENDADA":
            default:
                texto = "Agendada";
                colorTexto = Color.parseColor("#1E40AF"); // Azul
                colorFondo = Color.parseColor("#DBEAFE");
                notaExtra.setVisibility(View.GONE);
                break;
        }

        badge.setText(texto);
        badge.setTextColor(colorTexto);

        // Fondo redondeado con el color dinámico
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16);
        drawable.setColor(colorFondo);
        badge.setBackground(drawable);
    }

    static class AsesoriaViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAsesoriaItem;
        TextView tvItemEstadoBadge;
        TextView tvItemModalidad;
        TextView tvItemMateria;
        TextView tvItemProfesor;
        TextView tvItemHorario;
        TextView tvItemLugar;
        TextView tvItemNotaExtra;

        public AsesoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAsesoriaItem = itemView.findViewById(R.id.cardAsesoriaItem);
            tvItemEstadoBadge = itemView.findViewById(R.id.tvItemEstadoBadge);
            tvItemModalidad = itemView.findViewById(R.id.tvItemModalidad);
            tvItemMateria = itemView.findViewById(R.id.tvItemMateria);
            tvItemProfesor = itemView.findViewById(R.id.tvItemProfesor);
            tvItemHorario = itemView.findViewById(R.id.tvItemHorario);
            tvItemLugar = itemView.findViewById(R.id.tvItemLugar);
            tvItemNotaExtra = itemView.findViewById(R.id.tvItemNotaExtra);
        }
    }
}

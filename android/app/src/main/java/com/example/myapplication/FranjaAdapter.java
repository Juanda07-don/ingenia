package com.example.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class FranjaAdapter extends RecyclerView.Adapter<FranjaAdapter.FranjaViewHolder> {

    public interface OnFranjaSelectedListener {
        void onFranjaSelected(DisponibilidadDTO franja);
    }

    private List<DisponibilidadDTO> lista = new ArrayList<>();
    private int posicionSeleccionada = -1;
    private final OnFranjaSelectedListener listener;

    public FranjaAdapter(OnFranjaSelectedListener listener) {
        this.listener = listener;
    }

    public void setFranjas(List<DisponibilidadDTO> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        this.posicionSeleccionada = -1;
        notifyDataSetChanged();
    }

    public DisponibilidadDTO getSeleccionada() {
        if (posicionSeleccionada >= 0 && posicionSeleccionada < lista.size()) {
            return lista.get(posicionSeleccionada);
        }
        return null;
    }

    @NonNull
    @Override
    public FranjaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_franja_horario, parent, false);
        return new FranjaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FranjaViewHolder holder, int position) {
        DisponibilidadDTO f = lista.get(position);

        String inicio = f.getHoraInicio() != null && f.getHoraInicio().length() >= 5 ? f.getHoraInicio().substring(0, 5) : f.getHoraInicio();
        String fin = f.getHoraFin() != null && f.getHoraFin().length() >= 5 ? f.getHoraFin().substring(0, 5) : f.getHoraFin();
        holder.tvHoraRango.setText("🕒  " + inicio + "–" + fin);

        boolean esLibre = f.isReservable() || "LIBRE".equalsIgnoreCase(f.getEstado());

        if (!esLibre) {
            // OCUPADO: gris, deshabilitado
            holder.cardFranja.setCardBackgroundColor(Color.parseColor("#F9FAFB"));
            holder.cardFranja.setStrokeColor(Color.parseColor("#E5E7EB"));
            holder.cardFranja.setStrokeWidth(2);
            holder.tvHoraRango.setTextColor(Color.parseColor("#9CA3AF"));
            holder.tvEstadoFranja.setText("Ocupado");
            holder.tvEstadoFranja.setTextColor(Color.parseColor("#9CA3AF"));
            holder.cardFranja.setOnClickListener(null);
            holder.cardFranja.setClickable(false);
        } else if (position == posicionSeleccionada) {
            // SELECCIONADO: borde vino, fondo rosa suave
            holder.cardFranja.setCardBackgroundColor(Color.parseColor("#FDF2F4"));
            holder.cardFranja.setStrokeColor(Color.parseColor("#8C233E"));
            holder.cardFranja.setStrokeWidth(4);
            holder.tvHoraRango.setTextColor(Color.parseColor("#8C233E"));
            holder.tvEstadoFranja.setText("Seleccionado");
            holder.tvEstadoFranja.setTextColor(Color.parseColor("#8C233E"));
            holder.cardFranja.setClickable(true);
            holder.cardFranja.setOnClickListener(v -> {});
        } else {
            // LIBRE: blanco, borde gris, texto verde
            holder.cardFranja.setCardBackgroundColor(Color.WHITE);
            holder.cardFranja.setStrokeColor(Color.parseColor("#E5E7EB"));
            holder.cardFranja.setStrokeWidth(2);
            holder.tvHoraRango.setTextColor(Color.parseColor("#111827"));
            holder.tvEstadoFranja.setText("Libre");
            holder.tvEstadoFranja.setTextColor(Color.parseColor("#059669"));
            holder.cardFranja.setClickable(true);
            holder.cardFranja.setOnClickListener(v -> {
                int prev = posicionSeleccionada;
                posicionSeleccionada = holder.getAdapterPosition();
                notifyItemChanged(prev);
                notifyItemChanged(posicionSeleccionada);
                if (listener != null) listener.onFranjaSelected(f);
            });
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class FranjaViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardFranja;
        TextView tvHoraRango;
        TextView tvEstadoFranja;

        public FranjaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFranja = itemView.findViewById(R.id.cardFranja);
            tvHoraRango = itemView.findViewById(R.id.tvHoraRango);
            tvEstadoFranja = itemView.findViewById(R.id.tvEstadoFranja);
        }
    }
}
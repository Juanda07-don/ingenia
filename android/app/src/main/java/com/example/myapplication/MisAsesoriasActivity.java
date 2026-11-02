package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisAsesoriasActivity extends AppCompatActivity {

    private Long idEstudiante;
    private String nombreEstudiante;

    private TextView tabTodas;
    private TextView tabProximas;
    private TextView tabHistorial;
    private TextView tvContadorSesiones;
    private TextView tvSinAsesoriasLista;
    private RecyclerView rvMisAsesorias;

    private AsesoriaAdapter adapter;
    private List<AsesoriaDTO> todasLasAsesorias = new ArrayList<>();
    private int filtroActual = 0; // 0: Todas, 1: Próximas, 2: Historial

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_asesorias);

        // 1. Recibir datos del Intent
        idEstudiante = getIntent().getLongExtra("idUsuario", -1L);
        nombreEstudiante = getIntent().getStringExtra("nombre");

        // 2. Vincular vistas
        tabTodas = findViewById(R.id.tabTodas);
        tabProximas = findViewById(R.id.tabProximas);
        tabHistorial = findViewById(R.id.tabHistorial);
        tvContadorSesiones = findViewById(R.id.tvContadorSesiones);
        tvSinAsesoriasLista = findViewById(R.id.tvSinAsesoriasLista);
        rvMisAsesorias = findViewById(R.id.rvMisAsesorias);

        // 3. Configurar RecyclerView con clic inteligente según el estado
        adapter = new AsesoriaAdapter(asesoria -> {
            String st = asesoria.getEstado() != null ? asesoria.getEstado().toUpperCase() : "";

            if ("FINALIZADA".equals(st)) {
                // Reporte de Asesoría (Página 12)
                Intent intent = new Intent(MisAsesoriasActivity.this, ReporteAsesoriaActivity.class);
                intent.putExtra("idAsesoria", asesoria.getId());
                intent.putExtra("idUsuario", idEstudiante);
                intent.putExtra("materia", asesoria.getMateria());
                intent.putExtra("profesor", asesoria.getProfesor());
                intent.putExtra("fecha", asesoria.getFecha());
                intent.putExtra("horaInicio", asesoria.getHoraInicio());
                intent.putExtra("horaFin", asesoria.getHoraFin());
                intent.putExtra("lugar", asesoria.getLugar());
                intent.putExtra("codigoReserva", asesoria.getCodigoReserva());
                startActivity(intent);
            } else if ("REPROGRAMADA".equals(st)) {
                // Propuesta de Reprogramación (Página 10)
                Intent intent = new Intent(MisAsesoriasActivity.this, DetalleReprogramacionActivity.class);
                intent.putExtra("idAsesoria", asesoria.getId());
                intent.putExtra("idEstudiante", idEstudiante);
                intent.putExtra("materia", asesoria.getMateria());
                intent.putExtra("profesor", asesoria.getProfesor());
                intent.putExtra("fechaOriginal", asesoria.getFecha());
                intent.putExtra("horaInicioOriginal", asesoria.getHoraInicio());
                intent.putExtra("horaFinOriginal", asesoria.getHoraFin());
                intent.putExtra("lugar", asesoria.getLugar());

                if (asesoria.getPropuesta() != null) {
                    intent.putExtra("nuevaFecha", asesoria.getPropuesta().getFecha());
                    intent.putExtra("nuevaHoraInicio", asesoria.getPropuesta().getHoraInicio());
                    intent.putExtra("nuevaHoraFin", asesoria.getPropuesta().getHoraFin());
                    intent.putExtra("motivo", asesoria.getPropuesta().getMotivo());
                }
                startActivity(intent);
            } else {
                Toast.makeText(this, "Asesoría " + asesoria.getMateria() + " (" + asesoria.getEstado() + ")", Toast.LENGTH_SHORT).show();
            }
        });
        rvMisAsesorias.setLayoutManager(new LinearLayoutManager(this));
        rvMisAsesorias.setAdapter(adapter);

        // 4. Configurar clics de las pestañas
        tabTodas.setOnClickListener(v -> cambiarFiltro(0));
        tabProximas.setOnClickListener(v -> cambiarFiltro(1));
        tabHistorial.setOnClickListener(v -> cambiarFiltro(2));

        // 5. Barra inferior de navegación
        findViewById(R.id.navInicio).setOnClickListener(v -> finish());
        findViewById(R.id.navPerfil).setOnClickListener(v ->
                Toast.makeText(this, "Perfil de " + nombreEstudiante, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarAsesorias();
    }

    private void cargarAsesorias() {
        if (idEstudiante == -1L) return;

        tvContadorSesiones.setText("Cargando asesorías...");

        ApiClient.getService().getAsesoriasEstudiante(idEstudiante).enqueue(new Callback<List<AsesoriaDTO>>() {
            @Override
            public void onResponse(Call<List<AsesoriaDTO>> call, Response<List<AsesoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todasLasAsesorias = response.body();
                    tabTodas.setText("Todas (" + todasLasAsesorias.size() + ")");
                    aplicarFiltro();
                } else {
                    tvContadorSesiones.setText("No se pudieron cargar las asesorías");
                    tvSinAsesoriasLista.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AsesoriaDTO>> call, Throwable t) {
                tvContadorSesiones.setText("Error al conectar con el servidor");
                tvSinAsesoriasLista.setVisibility(View.VISIBLE);
            }
        });
    }

    private void cambiarFiltro(int nuevoFiltro) {
        this.filtroActual = nuevoFiltro;

        actualizarEstiloTab(tabTodas, nuevoFiltro == 0);
        actualizarEstiloTab(tabProximas, nuevoFiltro == 1);
        actualizarEstiloTab(tabHistorial, nuevoFiltro == 2);

        aplicarFiltro();
    }

    private void actualizarEstiloTab(TextView tab, boolean seleccionado) {
        if (seleccionado) {
            tab.setBackgroundResource(R.drawable.badge_bg);
            tab.setTextColor(getColor(R.color.ingenia_primary));
        } else {
            tab.setBackgroundColor(Color.TRANSPARENT);
            tab.setTextColor(getColor(R.color.ingenia_text_secondary));
        }
    }

    private void aplicarFiltro() {
        List<AsesoriaDTO> filtradas = new ArrayList<>();

        for (AsesoriaDTO a : todasLasAsesorias) {
            String estado = a.getEstado() != null ? a.getEstado().toUpperCase() : "";

            if (filtroActual == 0) {
                filtradas.add(a);
            } else if (filtroActual == 1) {
                if ("AGENDADA".equals(estado) || "REPROGRAMADA".equals(estado)) {
                    filtradas.add(a);
                }
            } else if (filtroActual == 2) {
                if ("FINALIZADA".equals(estado) || "CANCELADA".equals(estado)) {
                    filtradas.add(a);
                }
            }
        }

        if (filtroActual == 0) {
            tvContadorSesiones.setText("Todas las asesorías (" + filtradas.size() + ")");
        } else if (filtroActual == 1) {
            tvContadorSesiones.setText("Próximas · " + filtradas.size() + (filtradas.size() == 1 ? " sesión" : " sesiones"));
        } else {
            tvContadorSesiones.setText("Historial · " + filtradas.size() + (filtradas.size() == 1 ? " sesión" : " sesiones"));
        }

        if (filtradas.isEmpty()) {
            rvMisAsesorias.setVisibility(View.GONE);
            tvSinAsesoriasLista.setVisibility(View.VISIBLE);
        } else {
            rvMisAsesorias.setVisibility(View.VISIBLE);
            tvSinAsesoriasLista.setVisibility(View.GONE);
            adapter.setAsesorias(filtradas);
        }
    }
}
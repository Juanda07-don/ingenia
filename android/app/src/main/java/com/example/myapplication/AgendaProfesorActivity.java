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

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaProfesorActivity extends AppCompatActivity {

    private Long idProfesor;
    private String nombreProfesor;

    private TextView tvSubtituloAgenda;
    private TextView tabProfProximas;
    private TextView tabProfHistorial;
    private TextView tvContadorAgenda;
    private TextView tvSinAgenda;
    private RecyclerView rvAgendaProfesor;

    private TextView tvProximaAtencionFooter;
    private MaterialButton btnRegistrarAtencion;

    private AgendaProfesorAdapter adapter;
    private List<AsesoriaDTO> todasLasAsesorias = new ArrayList<>();
    private int filtroActual = 0; // 0: Próximas, 1: Historial
    private AsesoriaDTO proximaAtencion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_profesor);

        // 1. Recibir datos del Intent
        idProfesor = getIntent().getLongExtra("idUsuario", -1L);
        nombreProfesor = getIntent().getStringExtra("nombre");

        // 2. Vincular vistas
        tvSubtituloAgenda = findViewById(R.id.tvSubtituloAgenda);
        tabProfProximas = findViewById(R.id.tabProfProximas);
        tabProfHistorial = findViewById(R.id.tabProfHistorial);
        tvContadorAgenda = findViewById(R.id.tvContadorAgenda);
        tvSinAgenda = findViewById(R.id.tvSinAgenda);
        rvAgendaProfesor = findViewById(R.id.rvAgendaProfesor);

        tvProximaAtencionFooter = findViewById(R.id.tvProximaAtencionFooter);
        btnRegistrarAtencion = findViewById(R.id.btnRegistrarAtencion);

        // 3. Saludo personalizado
        if (nombreProfesor != null && !nombreProfesor.isEmpty()) {
            String primerNombre = nombreProfesor.replace("Dra.", "").replace("Dr.", "").replace("Prof.", "").trim().split(" ")[0];
            tvSubtituloAgenda.setText("Hola, " + primerNombre + ". Estas son tus reservas recibidas.");
        }

        // 4. Configurar RecyclerView con clic en "Reprogramar"
        adapter = new AgendaProfesorAdapter(new AgendaProfesorAdapter.OnProfesorAsesoriaListener() {
            @Override
            public void onReprogramarClick(AsesoriaDTO asesoria) {
                Intent intent = new Intent(AgendaProfesorActivity.this, ReprogramarAsesoriaActivity.class);
                intent.putExtra("idAsesoria", asesoria.getId());
                intent.putExtra("idProfesor", idProfesor);
                intent.putExtra("estudianteNombre", asesoria.getEstudiante());
                intent.putExtra("materia", asesoria.getMateria());
                intent.putExtra("fechaActual", asesoria.getFecha());
                intent.putExtra("horaInicioActual", asesoria.getHoraInicio());
                intent.putExtra("horaFinActual", asesoria.getHoraFin());
                intent.putExtra("lugarActual", asesoria.getLugar());
                startActivity(intent);
            }

            @Override
            public void onItemClick(AsesoriaDTO asesoria) {
                Toast.makeText(AgendaProfesorActivity.this, "Asesoría: " + asesoria.getMateria() + " (" + asesoria.getEstado() + ")", Toast.LENGTH_SHORT).show();
            }
        });
        rvAgendaProfesor.setLayoutManager(new LinearLayoutManager(this));
        rvAgendaProfesor.setAdapter(adapter);

        // 5. Clics en pestañas
        tabProfProximas.setOnClickListener(v -> cambiarFiltro(0));
        tabProfHistorial.setOnClickListener(v -> cambiarFiltro(1));

        // 6. Botón inferior "Registrar atención"
        btnRegistrarAtencion.setOnClickListener(v -> {
            if (proximaAtencion != null) {
                Intent intent = new Intent(AgendaProfesorActivity.this, RegistrarAtencionActivity.class);
                intent.putExtra("idAsesoria", proximaAtencion.getId());
                intent.putExtra("idProfesor", idProfesor);
                intent.putExtra("estudianteNombre", proximaAtencion.getEstudiante());
                intent.putExtra("materia", proximaAtencion.getMateria());
                intent.putExtra("fecha", proximaAtencion.getFecha());
                intent.putExtra("horaInicio", proximaAtencion.getHoraInicio());
                intent.putExtra("horaFin", proximaAtencion.getHoraFin());
                startActivity(intent);
            } else {
                Toast.makeText(this, "No hay atenciones pendientes para registrar", Toast.LENGTH_SHORT).show();
            }
        });

        // 7. Barra inferior
        findViewById(R.id.navInicioAgenda).setOnClickListener(v -> finish());
        findViewById(R.id.navPerfilAgenda).setOnClickListener(v ->
                Toast.makeText(this, "Perfil de " + nombreProfesor, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarAgenda();
    }

    private void cargarAgenda() {
        if (idProfesor == -1L) return;

        tvContadorAgenda.setText("Cargando agenda...");

        ApiClient.getService().getAsesoriasProfesor(idProfesor).enqueue(new Callback<List<AsesoriaDTO>>() {
            @Override
            public void onResponse(Call<List<AsesoriaDTO>> call, Response<List<AsesoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todasLasAsesorias = response.body();
                    aplicarFiltro();
                } else {
                    tvContadorAgenda.setText("No se pudo cargar la agenda");
                    tvSinAgenda.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AsesoriaDTO>> call, Throwable t) {
                tvContadorAgenda.setText("Error al conectar con el servidor");
                tvSinAgenda.setVisibility(View.VISIBLE);
            }
        });
    }

    private void cambiarFiltro(int nuevoFiltro) {
        this.filtroActual = nuevoFiltro;

        if (nuevoFiltro == 0) {
            tabProfProximas.setBackgroundResource(R.drawable.badge_bg);
            tabProfProximas.setTextColor(getColor(R.color.ingenia_primary));

            tabProfHistorial.setBackgroundColor(Color.TRANSPARENT);
            tabProfHistorial.setTextColor(getColor(R.color.ingenia_text_secondary));
        } else {
            tabProfHistorial.setBackgroundResource(R.drawable.badge_bg);
            tabProfHistorial.setTextColor(getColor(R.color.ingenia_primary));

            tabProfProximas.setBackgroundColor(Color.TRANSPARENT);
            tabProfProximas.setTextColor(getColor(R.color.ingenia_text_secondary));
        }

        aplicarFiltro();
    }

    private void aplicarFiltro() {
        List<AsesoriaDTO> proximas = new ArrayList<>();
        List<AsesoriaDTO> historial = new ArrayList<>();

        for (AsesoriaDTO a : todasLasAsesorias) {
            String st = a.getEstado() != null ? a.getEstado().toUpperCase() : "";
            if ("AGENDADA".equals(st) || "REPROGRAMADA".equals(st)) {
                proximas.add(a);
            } else {
                historial.add(a);
            }
        }

        tabProfProximas.setText("Próximas (" + proximas.size() + ")");

        List<AsesoriaDTO> listaAMostrar = (filtroActual == 0) ? proximas : historial;

        if (!proximas.isEmpty()) {
            proximaAtencion = proximas.get(0);
            tvProximaAtencionFooter.setText("Próxima atención · " + proximaAtencion.getEstudiante());
            tvProximaAtencionFooter.setVisibility(View.VISIBLE);
            btnRegistrarAtencion.setEnabled(true);
        } else {
            proximaAtencion = null;
            tvProximaAtencionFooter.setVisibility(View.GONE);
            btnRegistrarAtencion.setEnabled(false);
        }

        if (filtroActual == 0) {
            tvContadorAgenda.setText("Esta semana · " + listaAMostrar.size() + (listaAMostrar.size() == 1 ? " sesión" : " sesiones"));
        } else {
            tvContadorAgenda.setText("Historial · " + listaAMostrar.size() + (listaAMostrar.size() == 1 ? " sesión" : " sesiones"));
        }

        if (listaAMostrar.isEmpty()) {
            rvAgendaProfesor.setVisibility(View.GONE);
            tvSinAgenda.setVisibility(View.VISIBLE);
        } else {
            rvAgendaProfesor.setVisibility(View.VISIBLE);
            tvSinAgenda.setVisibility(View.GONE);
            adapter.setAsesorias(listaAMostrar);
        }
    }
}
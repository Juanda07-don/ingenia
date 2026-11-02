package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeProfesorActivity extends AppCompatActivity {

    private Long idProfesor;
    private String nombreProfesor;

    private TextView tvSaludoProfesor;
    private MaterialCardView cardProximaAtencion;
    private TextView tvBadgeEstadoProfesor;
    private TextView tvMateriaProfesorCard;
    private TextView tvEstudianteCard;
    private TextView tvHorarioProfesorCard;
    private TextView tvLugarProfesorCard;
    private TextView tvSinAtenciones;
    private TextView tvContadorSemanaProfesor;

    private MaterialButton btnReprogramarCard;
    private MaterialButton btnVerAsesoriasAgendadas;
    private MaterialButton btnConfigurarDisponibilidad;

    private AsesoriaDTO proximaAtencionActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_profesor);

        // 1. Recibir datos del Intent
        idProfesor = getIntent().getLongExtra("idUsuario", -1L);
        nombreProfesor = getIntent().getStringExtra("nombre");

        // 2. Vincular vistas
        tvSaludoProfesor = findViewById(R.id.tvSaludoProfesor);
        cardProximaAtencion = findViewById(R.id.cardProximaAtencion);
        tvBadgeEstadoProfesor = findViewById(R.id.tvBadgeEstadoProfesor);
        tvMateriaProfesorCard = findViewById(R.id.tvMateriaProfesorCard);
        tvEstudianteCard = findViewById(R.id.tvEstudianteCard);
        tvHorarioProfesorCard = findViewById(R.id.tvHorarioProfesorCard);
        tvLugarProfesorCard = findViewById(R.id.tvLugarProfesorCard);
        tvSinAtenciones = findViewById(R.id.tvSinAtenciones);
        tvContadorSemanaProfesor = findViewById(R.id.tvContadorSemanaProfesor);

        btnReprogramarCard = findViewById(R.id.btnReprogramarCard);
        btnVerAsesoriasAgendadas = findViewById(R.id.btnVerAsesoriasAgendadas);
        btnConfigurarDisponibilidad = findViewById(R.id.btnConfigurarDisponibilidad);

        // 3. Saludo al docente (ej: "Hola, Laura")
        if (nombreProfesor != null && !nombreProfesor.isEmpty()) {
            String primerNombre = nombreProfesor.replace("Dra.", "").replace("Dr.", "").replace("Prof.", "").trim().split(" ")[0];
            tvSaludoProfesor.setText("Hola, " + primerNombre);
        }

        // 4. Botón Reprogramar de la tarjeta
        btnReprogramarCard.setOnClickListener(v -> {
            if (proximaAtencionActual != null) {
                Intent intent = new Intent(HomeProfesorActivity.this, ReprogramarAsesoriaActivity.class);
                intent.putExtra("idAsesoria", proximaAtencionActual.getId());
                intent.putExtra("idProfesor", idProfesor);
                intent.putExtra("estudianteNombre", proximaAtencionActual.getEstudiante());
                intent.putExtra("materia", proximaAtencionActual.getMateria());
                intent.putExtra("fechaActual", proximaAtencionActual.getFecha());
                intent.putExtra("horaInicioActual", proximaAtencionActual.getHoraInicio());
                intent.putExtra("horaFinActual", proximaAtencionActual.getHoraFin());
                intent.putExtra("lugarActual", proximaAtencionActual.getLugar());
                startActivity(intent);
            }
        });

        // 5. Botón "Ver asesorías agendadas" -> Abre AgendaProfesorActivity
        btnVerAsesoriasAgendadas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeProfesorActivity.this, AgendaProfesorActivity.class);
            intent.putExtra("idUsuario", idProfesor);
            intent.putExtra("nombre", nombreProfesor);
            startActivity(intent);
        });

        // 6. Barra inferior: Tab "Agenda" también abre la agenda
        findViewById(R.id.navAgendaProfesor).setOnClickListener(v -> {
            Intent intent = new Intent(HomeProfesorActivity.this, AgendaProfesorActivity.class);
            intent.putExtra("idUsuario", idProfesor);
            intent.putExtra("nombre", nombreProfesor);
            startActivity(intent);
        });

        // 7. Botón "Configurar disponibilidad" -> Abre ConfigurarDisponibilidadActivity (Página 14)
        btnConfigurarDisponibilidad.setOnClickListener(v -> {
            Intent intent = new Intent(HomeProfesorActivity.this, ConfigurarDisponibilidadActivity.class);
            intent.putExtra("idUsuario", idProfesor);
            intent.putExtra("nombre", nombreProfesor);
            startActivity(intent);
        });

        findViewById(R.id.navPerfilProfesor).setOnClickListener(v ->
                Toast.makeText(this, "Perfil de " + nombreProfesor, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarAgendaProfesor();
    }

    private void cargarAgendaProfesor() {
        if (idProfesor == -1L) return;

        ApiClient.getService().getAsesoriasProfesor(idProfesor).enqueue(new Callback<List<AsesoriaDTO>>() {
            @Override
            public void onResponse(Call<List<AsesoriaDTO>> call, Response<List<AsesoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<AsesoriaDTO> agenda = response.body();

                    AsesoriaDTO proxima = null;
                    int totalAgendadas = 0;

                    for (AsesoriaDTO a : agenda) {
                        String st = a.getEstado() != null ? a.getEstado().toUpperCase() : "";
                        if ("AGENDADA".equals(st) || "REPROGRAMADA".equals(st)) {
                            totalAgendadas++;
                            if (proxima == null) {
                                proxima = a;
                            }
                        }
                    }

                    tvContadorSemanaProfesor.setText(totalAgendadas + (totalAgendadas == 1 ? " asesoría agendada" : " asesorías agendadas"));

                    if (proxima != null) {
                        mostrarTarjetaAtencion(proxima);
                    } else {
                        mostrarSinAtenciones();
                    }
                } else {
                    mostrarSinAtenciones();
                }
            }

            @Override
            public void onFailure(Call<List<AsesoriaDTO>> call, Throwable t) {
                mostrarSinAtenciones();
            }
        });
    }

    private void mostrarTarjetaAtencion(AsesoriaDTO a) {
        proximaAtencionActual = a;
        cardProximaAtencion.setVisibility(View.VISIBLE);
        tvSinAtenciones.setVisibility(View.GONE);

        tvBadgeEstadoProfesor.setText(a.getEstado());
        tvMateriaProfesorCard.setText(a.getMateria());

        String nombreEst = a.getEstudiante() != null ? a.getEstudiante() : "Estudiante UAO";
        tvEstudianteCard.setText("Estudiante: " + nombreEst);

        String inicio = a.getHoraInicio() != null && a.getHoraInicio().length() >= 5 ? a.getHoraInicio().substring(0, 5) : a.getHoraInicio();
        String fin = a.getHoraFin() != null && a.getHoraFin().length() >= 5 ? a.getHoraFin().substring(0, 5) : a.getHoraFin();
        tvHorarioProfesorCard.setText("🕒  " + a.getFecha() + " · " + inicio + "–" + fin);

        String lugar = a.getLugar() != null ? a.getLugar() : "Cubículo docente";
        tvLugarProfesorCard.setText("📍  " + lugar);
    }

    private void mostrarSinAtenciones() {
        proximaAtencionActual = null;
        cardProximaAtencion.setVisibility(View.GONE);
        tvSinAtenciones.setVisibility(View.VISIBLE);
        tvContadorSemanaProfesor.setText("0 asesorías agendadas");
    }
}
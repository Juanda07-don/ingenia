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

public class HomeEstudianteActivity extends AppCompatActivity {

    private Long idEstudiante;
    private String nombreEstudiante;

    private TextView tvSaludo;
    private MaterialCardView cardProximaAsesoria;
    private TextView tvBadgeEstado;
    private TextView tvModalidad;
    private TextView tvMateria;
    private TextView tvProfesor;
    private TextView tvHorario;
    private TextView tvLugar;
    private TextView tvSinAsesorias;

    private MaterialButton btnBuscarAsesoria;
    private MaterialButton btnMisAsesorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_estudiante);

        // 1. Recibir datos del Intent
        idEstudiante = getIntent().getLongExtra("idUsuario", -1L);
        nombreEstudiante = getIntent().getStringExtra("nombre");

        // 2. Vincular vistas
        tvSaludo = findViewById(R.id.tvSaludo);
        cardProximaAsesoria = findViewById(R.id.cardProximaAsesoria);
        tvBadgeEstado = findViewById(R.id.tvBadgeEstado);
        tvModalidad = findViewById(R.id.tvModalidad);
        tvMateria = findViewById(R.id.tvMateria);
        tvProfesor = findViewById(R.id.tvProfesor);
        tvHorario = findViewById(R.id.tvHorario);
        tvLugar = findViewById(R.id.tvLugar);
        tvSinAsesorias = findViewById(R.id.tvSinAsesorias);

        btnBuscarAsesoria = findViewById(R.id.btnBuscarAsesoria);
        btnMisAsesorias = findViewById(R.id.btnMisAsesorias);

        // 3. Saludo dinámico
        if (nombreEstudiante != null && !nombreEstudiante.isEmpty()) {
            String primerNombre = nombreEstudiante.split(" ")[0];
            tvSaludo.setText("Hola, " + primerNombre);
        }

        // 4. Botón "Buscar asesoría"
        btnBuscarAsesoria.setOnClickListener(v -> {
            Intent intent = new Intent(HomeEstudianteActivity.this, BuscarAsesoriaActivity.class);
            intent.putExtra("idUsuario", idEstudiante);
            intent.putExtra("nombre", nombreEstudiante);
            startActivity(intent);
        });

        // 5. Botón "Mis asesorías" -> Abre MisAsesoriasActivity
        btnMisAsesorias.setOnClickListener(v -> {
            Intent intent = new Intent(HomeEstudianteActivity.this, MisAsesoriasActivity.class);
            intent.putExtra("idUsuario", idEstudiante);
            intent.putExtra("nombre", nombreEstudiante);
            startActivity(intent);
        });

        // Barra inferior: Tab Asesorías también abre Mis Asesorías
        findViewById(R.id.navAsesorias).setOnClickListener(v -> {
            Intent intent = new Intent(HomeEstudianteActivity.this, MisAsesoriasActivity.class);
            intent.putExtra("idUsuario", idEstudiante);
            intent.putExtra("nombre", nombreEstudiante);
            startActivity(intent);
        });

        findViewById(R.id.navPerfil).setOnClickListener(v ->
                Toast.makeText(this, "Perfil de " + nombreEstudiante, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que vuelve a esta pantalla, recargamos la próxima asesoría
        cargarProximaAsesoria();
    }

    private void cargarProximaAsesoria() {
        if (idEstudiante == -1L) return;

        ApiClient.getService().getAsesoriasEstudiante(idEstudiante).enqueue(new Callback<List<AsesoriaDTO>>() {
            @Override
            public void onResponse(Call<List<AsesoriaDTO>> call, Response<List<AsesoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<AsesoriaDTO> asesorias = response.body();

                    AsesoriaDTO proxima = null;
                    for (AsesoriaDTO a : asesorias) {
                        if ("AGENDADA".equalsIgnoreCase(a.getEstado()) || "REPROGRAMADA".equalsIgnoreCase(a.getEstado())) {
                            proxima = a;
                            break;
                        }
                    }

                    if (proxima != null) {
                        mostrarTarjetaAsesoria(proxima);
                    } else {
                        mostrarSinAsesorias();
                    }
                } else {
                    mostrarSinAsesorias();
                }
            }

            @Override
            public void onFailure(Call<List<AsesoriaDTO>> call, Throwable t) {
                mostrarSinAsesorias();
            }
        });
    }

    private void mostrarTarjetaAsesoria(AsesoriaDTO a) {
        cardProximaAsesoria.setVisibility(View.VISIBLE);
        tvSinAsesorias.setVisibility(View.GONE);

        tvBadgeEstado.setText(a.getEstado());
        tvMateria.setText(a.getMateria());
        tvProfesor.setText(a.getProfesor());

        String inicio = a.getHoraInicio() != null && a.getHoraInicio().length() >= 5 ? a.getHoraInicio().substring(0, 5) : a.getHoraInicio();
        String fin = a.getHoraFin() != null && a.getHoraFin().length() >= 5 ? a.getHoraFin().substring(0, 5) : a.getHoraFin();
        tvHorario.setText("🕒  " + a.getFecha() + " · " + inicio + "–" + fin);

        String lugar = a.getLugar() != null ? a.getLugar() : "Cubículo docente";
        tvLugar.setText("📍  " + lugar);
    }

    private void mostrarSinAsesorias() {
        cardProximaAsesoria.setVisibility(View.GONE);
        tvSinAsesorias.setVisibility(View.VISIBLE);
    }
}
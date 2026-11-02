package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilProfesorActivity extends AppCompatActivity {

    private Long idProfesor;
    private String nombreProfesor;
    private String materiaProfesor;
    private String cubiculoProfesor;
    private Long idEstudiante;
    private String nombreEstudiante;

    private TextView tvInicialesDocente;
    private TextView tvNombreDocente;
    private TextView tvMateriasDocente;
    private TextView tvMesAnio;
    private TextView tvSemanaRango;
    private TextView tvMensajeDisponibilidad;
    private RecyclerView rvFranjas;
    private MaterialButton btnContinuarHorario;

    private LinearLayout[] btnsDias;
    private TextView[] tvsNombresDias;
    private TextView[] tvsNumerosDias;
    private LocalDate[] fechasSemana;
    private int diaSeleccionadoIndex = 0;

    private FranjaAdapter adapter;
    private DisponibilidadDTO franjaSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_profesor);

        // 1. Recibir datos del Intent
        idProfesor = getIntent().getLongExtra("idProfesor", -1L);
        nombreProfesor = getIntent().getStringExtra("nombreProfesor");
        materiaProfesor = getIntent().getStringExtra("materiaProfesor");
        cubiculoProfesor = getIntent().getStringExtra("cubiculoProfesor");
        idEstudiante = getIntent().getLongExtra("idEstudiante", -1L);
        nombreEstudiante = getIntent().getStringExtra("nombreEstudiante");

        // 2. Vincular vistas
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        tvInicialesDocente = findViewById(R.id.tvInicialesDocente);
        tvNombreDocente = findViewById(R.id.tvNombreDocente);
        tvMateriasDocente = findViewById(R.id.tvMateriasDocente);
        tvMesAnio = findViewById(R.id.tvMesAnio);
        tvSemanaRango = findViewById(R.id.tvSemanaRango);
        tvMensajeDisponibilidad = findViewById(R.id.tvMensajeDisponibilidad);
        rvFranjas = findViewById(R.id.rvFranjas);
        btnContinuarHorario = findViewById(R.id.btnContinuarHorario);

        // 3. Mostrar datos del profesor
        tvInicialesDocente.setText(calcularIniciales(nombreProfesor));
        tvNombreDocente.setText(nombreProfesor != null ? nombreProfesor : "Profesor");
        String detalle = (materiaProfesor != null ? materiaProfesor : "") +
                (cubiculoProfesor != null ? " · " + cubiculoProfesor : "");
        tvMateriasDocente.setText(detalle);

        // 4. Configurar RecyclerView de Franjas
        adapter = new FranjaAdapter(franja -> {
            this.franjaSeleccionada = franja;
            btnContinuarHorario.setEnabled(true);
        });
        rvFranjas.setLayoutManager(new LinearLayoutManager(this));
        rvFranjas.setAdapter(adapter);

        // 5. Configurar el calendario semanal
        configurarSemana();

        // 6. Botón continuar -> Abre ConfirmarReservaActivity
        btnContinuarHorario.setOnClickListener(v -> {
            if (franjaSeleccionada != null) {
                Intent intent = new Intent(PerfilProfesorActivity.this, ConfirmarReservaActivity.class);
                intent.putExtra("idDisponibilidad", franjaSeleccionada.getIdDisponibilidad());
                intent.putExtra("fecha", franjaSeleccionada.getFecha());
                intent.putExtra("horaInicio", franjaSeleccionada.getHoraInicio());
                intent.putExtra("horaFin", franjaSeleccionada.getHoraFin());
                intent.putExtra("idProfesor", idProfesor);
                intent.putExtra("nombreProfesor", nombreProfesor);
                intent.putExtra("materiaProfesor", materiaProfesor);
                intent.putExtra("cubiculoProfesor", cubiculoProfesor);
                intent.putExtra("idEstudiante", idEstudiante);
                intent.putExtra("nombreEstudiante", nombreEstudiante);
                startActivity(intent);
            }
        });
    }

    private void configurarSemana() {
        btnsDias = new LinearLayout[]{
                findViewById(R.id.btnDiaLun),
                findViewById(R.id.btnDiaMar),
                findViewById(R.id.btnDiaMie),
                findViewById(R.id.btnDiaJue),
                findViewById(R.id.btnDiaVie)
        };

        tvsNombresDias = new TextView[]{
                findViewById(R.id.tvNombreDiaLun),
                findViewById(R.id.tvNombreDiaMar),
                findViewById(R.id.tvNombreDiaMie),
                findViewById(R.id.tvNombreDiaJue),
                findViewById(R.id.tvNombreDiaVie)
        };

        tvsNumerosDias = new TextView[]{
                findViewById(R.id.tvNumeroDiaLun),
                findViewById(R.id.tvNumeroDiaMar),
                findViewById(R.id.tvNumeroDiaMie),
                findViewById(R.id.tvNumeroDiaJue),
                findViewById(R.id.tvNumeroDiaVie)
        };

        fechasSemana = new LocalDate[5];

        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        DateTimeFormatter fmtMes = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "CO"));
        tvMesAnio.setText(lunes.format(fmtMes).substring(0, 1).toUpperCase() + lunes.format(fmtMes).substring(1));
        tvSemanaRango.setText("Semana del " + lunes.getDayOfMonth() + " al " + lunes.plusDays(4).getDayOfMonth());

        for (int i = 0; i < 5; i++) {
            final int index = i;
            fechasSemana[i] = lunes.plusDays(i);
            tvsNumerosDias[i].setText(String.valueOf(fechasSemana[i].getDayOfMonth()));

            if (fechasSemana[i].equals(hoy)) {
                diaSeleccionadoIndex = i;
            }

            btnsDias[i].setOnClickListener(v -> seleccionarDia(index));
        }

        seleccionarDia(diaSeleccionadoIndex);
    }

    private void seleccionarDia(int index) {
        this.diaSeleccionadoIndex = index;
        this.franjaSeleccionada = null;
        btnContinuarHorario.setEnabled(false);

        for (int i = 0; i < 5; i++) {
            if (i == index) {
                btnsDias[i].setBackgroundResource(R.drawable.badge_bg);
                tvsNombresDias[i].setTextColor(getColor(R.color.ingenia_primary));
                tvsNumerosDias[i].setTextColor(getColor(R.color.ingenia_primary));
            } else {
                btnsDias[i].setBackgroundColor(Color.parseColor("#F3F4F6"));
                tvsNombresDias[i].setTextColor(Color.parseColor("#6B7280"));
                tvsNumerosDias[i].setTextColor(Color.parseColor("#111827"));
            }
        }

        consultarDisponibilidad(fechasSemana[index].toString());
    }

    private void consultarDisponibilidad(String fechaIso) {
        if (idProfesor == -1L) return;

        tvMensajeDisponibilidad.setText("Consultando espacios disponibles...");
        tvMensajeDisponibilidad.setVisibility(View.VISIBLE);
        rvFranjas.setVisibility(View.GONE);

        ApiClient.getService().getDisponibilidad(idProfesor, fechaIso).enqueue(new Callback<List<DisponibilidadDTO>>() {
            @Override
            public void onResponse(Call<List<DisponibilidadDTO>> call, Response<List<DisponibilidadDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    tvMensajeDisponibilidad.setVisibility(View.GONE);
                    rvFranjas.setVisibility(View.VISIBLE);
                    adapter.setFranjas(response.body());
                } else {
                    adapter.setFranjas(null);
                    rvFranjas.setVisibility(View.GONE);
                    tvMensajeDisponibilidad.setText("El docente no tiene espacios programados para este día.");
                    tvMensajeDisponibilidad.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<DisponibilidadDTO>> call, Throwable t) {
                adapter.setFranjas(null);
                rvFranjas.setVisibility(View.GONE);
                tvMensajeDisponibilidad.setText("Error al consultar disponibilidad.");
                tvMensajeDisponibilidad.setVisibility(View.VISIBLE);
            }
        });
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
}
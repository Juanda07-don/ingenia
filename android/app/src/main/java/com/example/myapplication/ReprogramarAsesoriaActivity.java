package com.example.myapplication;

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
import com.google.android.material.textfield.TextInputEditText;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReprogramarAsesoriaActivity extends AppCompatActivity {

    private Long idAsesoria;
    private Long idProfesor;
    private String estudianteNombre;
    private String materia;
    private String fechaActual;
    private String horaInicioActual;
    private String horaFinActual;
    private String lugarActual;

    // Vistas Vista 1: Formulario
    private View layoutFormularioReprogramar;
    private TextView tvMateriaActual;
    private TextView tvEstudianteActual;
    private TextView tvHorarioActual;
    private TextView tvLugarActual;
    private TextView tvHelperMotivo;
    private TextInputEditText etMotivoReprogramar;
    private TextView tvMensajeEspaciosReprogramar;
    private RecyclerView rvFranjasReprogramar;
    private MaterialButton btnEnviarReprogramacion;
    private MaterialButton btnCancelarReprogramacion;

    // Vistas Vista 2: Éxito (Página 18)
    private LinearLayout layoutExitoReprogramar;
    private TextView tvSubtituloExitoRepro;
    private TextView tvMateriaExitoRepro;
    private TextView tvEstudianteExitoRepro;
    private TextView tvHorarioNuevoExitoRepro;
    private TextView tvHorarioAnteriorExitoRepro;
    private TextView tvMensajeAvisoEstudiante;
    private MaterialButton btnVolverAgendaRepro;

    // Calendario semanal
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
        setContentView(R.layout.activity_reprogramar_asesoria);

        // 1. Recibir datos del Intent
        idAsesoria = getIntent().getLongExtra("idAsesoria", -1L);
        idProfesor = getIntent().getLongExtra("idProfesor", -1L);
        estudianteNombre = getIntent().getStringExtra("estudianteNombre");
        materia = getIntent().getStringExtra("materia");
        fechaActual = getIntent().getStringExtra("fechaActual");
        horaInicioActual = getIntent().getStringExtra("horaInicioActual");
        horaFinActual = getIntent().getStringExtra("horaFinActual");
        lugarActual = getIntent().getStringExtra("lugarActual");

        // 2. Vincular vistas
        layoutFormularioReprogramar = findViewById(R.id.layoutFormularioReprogramar);
        layoutExitoReprogramar = findViewById(R.id.layoutExitoReprogramar);

        findViewById(R.id.btnVolverReprogramar).setOnClickListener(v -> finish());
        tvMateriaActual = findViewById(R.id.tvMateriaActual);
        tvEstudianteActual = findViewById(R.id.tvEstudianteActual);
        tvHorarioActual = findViewById(R.id.tvHorarioActual);
        tvLugarActual = findViewById(R.id.tvLugarActual);
        tvHelperMotivo = findViewById(R.id.tvHelperMotivo);
        etMotivoReprogramar = findViewById(R.id.etMotivoReprogramar);
        tvMensajeEspaciosReprogramar = findViewById(R.id.tvMensajeEspaciosReprogramar);
        rvFranjasReprogramar = findViewById(R.id.rvFranjasReprogramar);
        btnEnviarReprogramacion = findViewById(R.id.btnEnviarReprogramacion);
        btnCancelarReprogramacion = findViewById(R.id.btnCancelarReprogramacion);

        tvSubtituloExitoRepro = findViewById(R.id.tvSubtituloExitoRepro);
        tvMateriaExitoRepro = findViewById(R.id.tvMateriaExitoRepro);
        tvEstudianteExitoRepro = findViewById(R.id.tvEstudianteExitoRepro);
        tvHorarioNuevoExitoRepro = findViewById(R.id.tvHorarioNuevoExitoRepro);
        tvHorarioAnteriorExitoRepro = findViewById(R.id.tvHorarioAnteriorExitoRepro);
        tvMensajeAvisoEstudiante = findViewById(R.id.tvMensajeAvisoEstudiante);
        btnVolverAgendaRepro = findViewById(R.id.btnVolverAgendaRepro);

        // 3. Pintar datos de la asesoría actual
        tvMateriaActual.setText(materia != null ? materia : "Asesoría académica");
        tvEstudianteActual.setText("Estudiante: " + (estudianteNombre != null ? estudianteNombre : "Estudiante"));

        String inicio = horaInicioActual != null && horaInicioActual.length() >= 5 ? horaInicioActual.substring(0, 5) : horaInicioActual;
        String fin = horaFinActual != null && horaFinActual.length() >= 5 ? horaFinActual.substring(0, 5) : horaFinActual;
        tvHorarioActual.setText("🕒  " + fechaActual + " · " + inicio + "–" + fin);
        tvLugarActual.setText("📍  " + (lugarActual != null ? lugarActual : "Cubículo docente"));

        String primerNombre = estudianteNombre != null ? estudianteNombre.split(" ")[0] : "el estudiante";
        tvHelperMotivo.setText("El motivo se compartirá con " + primerNombre + ".");

        // 4. Configurar RecyclerView de franjas
        adapter = new FranjaAdapter(franja -> {
            this.franjaSeleccionada = franja;
            btnEnviarReprogramacion.setEnabled(true);
        });
        rvFranjasReprogramar.setLayoutManager(new LinearLayoutManager(this));
        rvFranjasReprogramar.setAdapter(adapter);

        // 5. Configurar selector de días
        configurarSemana();

        // 6. Botones de acción
        btnCancelarReprogramacion.setOnClickListener(v -> finish());
        btnEnviarReprogramacion.setOnClickListener(v -> enviarReprogramacion());
        btnVolverAgendaRepro.setOnClickListener(v -> finish());
    }

    private void configurarSemana() {
        btnsDias = new LinearLayout[]{
                findViewById(R.id.btnReproLun),
                findViewById(R.id.btnReproMar),
                findViewById(R.id.btnReproMie),
                findViewById(R.id.btnReproJue),
                findViewById(R.id.btnReproVie)
        };

        tvsNombresDias = new TextView[]{
                findViewById(R.id.tvNombreReproLun),
                findViewById(R.id.tvNombreReproMar),
                findViewById(R.id.tvNombreReproMie),
                findViewById(R.id.tvNombreReproJue),
                findViewById(R.id.tvNombreReproVie)
        };

        tvsNumerosDias = new TextView[]{
                findViewById(R.id.tvNumReproLun),
                findViewById(R.id.tvNumReproMar),
                findViewById(R.id.tvNumReproMie),
                findViewById(R.id.tvNumReproJue),
                findViewById(R.id.tvNumReproVie)
        };

        fechasSemana = new LocalDate[5];

        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

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
        btnEnviarReprogramacion.setEnabled(false);

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

        consultarEspacios(fechasSemana[index].toString());
    }

    private void consultarEspacios(String fechaIso) {
        if (idProfesor == -1L) return;

        tvMensajeEspaciosReprogramar.setText("Consultando espacios disponibles...");
        tvMensajeEspaciosReprogramar.setVisibility(View.VISIBLE);
        rvFranjasReprogramar.setVisibility(View.GONE);

        ApiClient.getService().getDisponibilidad(idProfesor, fechaIso).enqueue(new Callback<List<DisponibilidadDTO>>() {
            @Override
            public void onResponse(Call<List<DisponibilidadDTO>> call, Response<List<DisponibilidadDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    tvMensajeEspaciosReprogramar.setVisibility(View.GONE);
                    rvFranjasReprogramar.setVisibility(View.VISIBLE);
                    adapter.setFranjas(response.body());
                } else {
                    adapter.setFranjas(null);
                    rvFranjasReprogramar.setVisibility(View.GONE);
                    tvMensajeEspaciosReprogramar.setText("No tienes espacios libres programados para este día.");
                    tvMensajeEspaciosReprogramar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<DisponibilidadDTO>> call, Throwable t) {
                adapter.setFranjas(null);
                rvFranjasReprogramar.setVisibility(View.GONE);
                tvMensajeEspaciosReprogramar.setText("Error al consultar disponibilidad.");
                tvMensajeEspaciosReprogramar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void enviarReprogramacion() {
        if (franjaSeleccionada == null) {
            Toast.makeText(this, "Selecciona un horario disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String motivo = etMotivoReprogramar.getText() != null ? etMotivoReprogramar.getText().toString().trim() : "";
        if (motivo.isEmpty()) {
            motivo = "Ajuste de horario por parte del docente";
        }

        btnEnviarReprogramacion.setEnabled(false);
        btnEnviarReprogramacion.setText("Enviando...");

        ReprogramarRequest request = new ReprogramarRequest(idProfesor, franjaSeleccionada.getId(), motivo);
        ApiClient.getService().reprogramarAsesoria(idAsesoria, request).enqueue(new Callback<AsesoriaDTO>() {
            @Override
            public void onResponse(Call<AsesoriaDTO> call, Response<AsesoriaDTO> response) {
                btnEnviarReprogramacion.setEnabled(true);
                btnEnviarReprogramacion.setText("Enviar reprogramación");

                if (response.isSuccessful() && response.body() != null) {
                    mostrarExitoReprogramacion();
                } else {
                    Toast.makeText(ReprogramarAsesoriaActivity.this, "No se pudo reprogramar (código " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AsesoriaDTO> call, Throwable t) {
                btnEnviarReprogramacion.setEnabled(true);
                btnEnviarReprogramacion.setText("Enviar reprogramación");
                Toast.makeText(ReprogramarAsesoriaActivity.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarExitoReprogramacion() {
        layoutFormularioReprogramar.setVisibility(View.GONE);
        layoutExitoReprogramar.setVisibility(View.VISIBLE);

        String primerNombre = estudianteNombre != null ? estudianteNombre.split(" ")[0] : "el estudiante";
        tvSubtituloExitoRepro.setText("Se notificó a " + (estudianteNombre != null ? estudianteNombre : "el estudiante") + ".");
        tvMateriaExitoRepro.setText(materia);
        tvEstudianteExitoRepro.setText("Estudiante: " + estudianteNombre);

        String nuevoInicio = franjaSeleccionada.getHoraInicio() != null && franjaSeleccionada.getHoraInicio().length() >= 5 ? franjaSeleccionada.getHoraInicio().substring(0, 5) : franjaSeleccionada.getHoraInicio();
        String nuevoFin = franjaSeleccionada.getHoraFin() != null && franjaSeleccionada.getHoraFin().length() >= 5 ? franjaSeleccionada.getHoraFin().substring(0, 5) : franjaSeleccionada.getHoraFin();
        tvHorarioNuevoExitoRepro.setText("🕒  " + franjaSeleccionada.getFecha() + " · " + nuevoInicio + "–" + nuevoFin);

        String antInicio = horaInicioActual != null && horaInicioActual.length() >= 5 ? horaInicioActual.substring(0, 5) : horaInicioActual;
        String antFin = horaFinActual != null && horaFinActual.length() >= 5 ? horaFinActual.substring(0, 5) : horaFinActual;
        tvHorarioAnteriorExitoRepro.setText("Antes: " + fechaActual + ", " + antInicio + "–" + antFin);

        tvMensajeAvisoEstudiante.setText("🔔  " + primerNombre + " recibió la propuesta de nuevo horario. Su aceptación está pendiente.");
    }
}
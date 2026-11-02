package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigurarDisponibilidadActivity extends AppCompatActivity {

    private Long idProfesor;
    private String nombreProfesor;

    private TextView tvMesConfigurar;
    private TextView tvTituloDiaSeleccionado;
    private Spinner spinnerMateriaDocente;
    private TextInputEditText etHoraInicioConfig;
    private TextInputEditText etHoraFinConfig;
    private TextView tvErrorConfigurar;
    private MaterialButton btnGuardarDisponibilidad;

    private LinearLayout[] btnsDias;
    private TextView[] tvsNombresDias;
    private TextView[] tvsNumerosDias;
    private LocalDate[] fechasSemana;
    private int diaSeleccionadoIndex = 0;

    private List<MateriaDTO> listaMaterias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_disponibilidad);

        // 1. Recibir datos del Intent
        idProfesor = getIntent().getLongExtra("idUsuario", -1L);
        nombreProfesor = getIntent().getStringExtra("nombre");

        // 2. Vincular vistas
        findViewById(R.id.btnVolverConfigurar).setOnClickListener(v -> finish());
        tvMesConfigurar = findViewById(R.id.tvMesConfigurar);
        tvTituloDiaSeleccionado = findViewById(R.id.tvTituloDiaSeleccionado);
        spinnerMateriaDocente = findViewById(R.id.spinnerMateriaDocente);
        etHoraInicioConfig = findViewById(R.id.etHoraInicioConfig);
        etHoraFinConfig = findViewById(R.id.etHoraFinConfig);
        tvErrorConfigurar = findViewById(R.id.tvErrorConfigurar);
        btnGuardarDisponibilidad = findViewById(R.id.btnGuardarDisponibilidad);

        // 3. Configurar selector de días (Lunes a Viernes)
        configurarSemana();

        // 4. Cargar materias asignadas a este profesor desde el backend
        cargarMateriasDocente();

        // 5. Botón Guardar disponibilidad
        btnGuardarDisponibilidad.setOnClickListener(v -> guardarDisponibilidad());

        // 6. Barra inferior
        findViewById(R.id.navInicioConfigurar).setOnClickListener(v -> finish());
    }

    private void configurarSemana() {
        btnsDias = new LinearLayout[]{
                findViewById(R.id.btnConfigLun),
                findViewById(R.id.btnConfigMar),
                findViewById(R.id.btnConfigMie),
                findViewById(R.id.btnConfigJue),
                findViewById(R.id.btnConfigVie)
        };

        tvsNombresDias = new TextView[]{
                findViewById(R.id.tvNombreConfigLun),
                findViewById(R.id.tvNombreConfigMar),
                findViewById(R.id.tvNombreConfigMie),
                findViewById(R.id.tvNombreConfigJue),
                findViewById(R.id.tvNombreConfigVie)
        };

        tvsNumerosDias = new TextView[]{
                findViewById(R.id.tvNumConfigLun),
                findViewById(R.id.tvNumConfigMar),
                findViewById(R.id.tvNumConfigMie),
                findViewById(R.id.tvNumConfigJue),
                findViewById(R.id.tvNumConfigVie)
        };

        fechasSemana = new LocalDate[5];

        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        DateTimeFormatter fmtMes = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "CO"));
        tvMesConfigurar.setText(lunes.format(fmtMes).substring(0, 1).toUpperCase() + lunes.format(fmtMes).substring(1));

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

        LocalDate fecha = fechasSemana[index];
        tvTituloDiaSeleccionado.setText("Horario para el " + fecha.getDayOfMonth() + " de " +
                fecha.getMonth().name().toLowerCase());
    }

    private void cargarMateriasDocente() {
        if (idProfesor == -1L) return;

        ApiClient.getService().getMateriasProfesor(idProfesor).enqueue(new Callback<List<MateriaDTO>>() {
            @Override
            public void onResponse(Call<List<MateriaDTO>> call, Response<List<MateriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    listaMaterias = response.body();
                    ArrayAdapter<MateriaDTO> adapter = new ArrayAdapter<>(
                            ConfigurarDisponibilidadActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            listaMaterias
                    );
                    spinnerMateriaDocente.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<MateriaDTO>> call, Throwable t) {
                Toast.makeText(ConfigurarDisponibilidadActivity.this, "Error al cargar materias", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDisponibilidad() {
        tvErrorConfigurar.setVisibility(View.GONE);

        if (listaMaterias.isEmpty()) {
            tvErrorConfigurar.setText("No tienes materias asignadas para abrir asesorías.");
            tvErrorConfigurar.setVisibility(View.VISIBLE);
            return;
        }

        MateriaDTO materiaSeleccionada = (MateriaDTO) spinnerMateriaDocente.getSelectedItem();
        if (materiaSeleccionada == null) {
            tvErrorConfigurar.setText("Selecciona una materia.");
            tvErrorConfigurar.setVisibility(View.VISIBLE);
            return;
        }

        String inicio = etHoraInicioConfig.getText() != null ? etHoraInicioConfig.getText().toString().trim() : "";
        String fin = etHoraFinConfig.getText() != null ? etHoraFinConfig.getText().toString().trim() : "";

        if (inicio.isEmpty() || fin.isEmpty()) {
            tvErrorConfigurar.setText("Completa la hora de inicio y fin.");
            tvErrorConfigurar.setVisibility(View.VISIBLE);
            return;
        }

        if (inicio.length() == 5) inicio += ":00";
        if (fin.length() == 5) fin += ":00";

        LocalDate fecha = fechasSemana[diaSeleccionadoIndex];

        btnGuardarDisponibilidad.setEnabled(false);
        btnGuardarDisponibilidad.setText("Guardando...");

        ConfigurarEspaciosRequest request = new ConfigurarEspaciosRequest(
                materiaSeleccionada.getId(),
                fecha.toString(),
                inicio,
                fin
        );

        ApiClient.getService().crearEspacios(idProfesor, request).enqueue(new Callback<List<EspacioDTO>>() {
            @Override
            public void onResponse(Call<List<EspacioDTO>> call, Response<List<EspacioDTO>> response) {
                btnGuardarDisponibilidad.setEnabled(true);
                btnGuardarDisponibilidad.setText("Guardar disponibilidad");

                if (response.isSuccessful() && response.body() != null) {
                    int creados = response.body().size();
                    Toast.makeText(ConfigurarDisponibilidadActivity.this,
                            "¡Disponibilidad guardada! Se abrieron " + creados + " espacios libres.",
                            Toast.LENGTH_LONG).show();
                    finish(); // Regresa al inicio
                } else {
                    tvErrorConfigurar.setText("No fue posible guardar la disponibilidad. Verifica que el horario no se cruce.");
                    tvErrorConfigurar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<EspacioDTO>> call, Throwable t) {
                btnGuardarDisponibilidad.setEnabled(true);
                btnGuardarDisponibilidad.setText("Guardar disponibilidad");
                tvErrorConfigurar.setText("Error al conectar con el servidor: " + t.getMessage());
                tvErrorConfigurar.setVisibility(View.VISIBLE);
            }
        });
    }
}
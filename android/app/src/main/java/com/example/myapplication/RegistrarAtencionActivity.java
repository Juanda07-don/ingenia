package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarAtencionActivity extends AppCompatActivity {

    private Long idAsesoria;
    private Long idProfesor;
    private String estudianteNombre;
    private String materia;
    private String fecha;
    private String horaInicio;
    private String horaFin;

    private TextView tvEstudianteAtencion;
    private TextView tvMateriaModalidadAtencion;
    private TextView tvHorarioAtencion;
    private TextInputEditText etHoraIngreso;
    private TextInputEditText etHoraSalida;
    private TextInputEditText etTemaTratado;
    private TextInputEditText etCompromisos;
    private TextView tvErrorAtencion;
    private MaterialButton btnGuardarRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_atencion);

        // 1. Recibir datos del Intent
        idAsesoria = getIntent().getLongExtra("idAsesoria", -1L);
        idProfesor = getIntent().getLongExtra("idProfesor", -1L);
        estudianteNombre = getIntent().getStringExtra("estudianteNombre");
        materia = getIntent().getStringExtra("materia");
        fecha = getIntent().getStringExtra("fecha");
        horaInicio = getIntent().getStringExtra("horaInicio");
        horaFin = getIntent().getStringExtra("horaFin");

        // 2. Vincular vistas
        findViewById(R.id.btnVolverRegistrar).setOnClickListener(v -> finish());
        tvEstudianteAtencion = findViewById(R.id.tvEstudianteAtencion);
        tvMateriaModalidadAtencion = findViewById(R.id.tvMateriaModalidadAtencion);
        tvHorarioAtencion = findViewById(R.id.tvHorarioAtencion);
        etHoraIngreso = findViewById(R.id.etHoraIngreso);
        etHoraSalida = findViewById(R.id.etHoraSalida);
        etTemaTratado = findViewById(R.id.etTemaTratado);
        etCompromisos = findViewById(R.id.etCompromisos);
        tvErrorAtencion = findViewById(R.id.tvErrorAtencion);
        btnGuardarRegistro = findViewById(R.id.btnGuardarRegistro);

        // 3. Pintar datos de la asesoría
        tvEstudianteAtencion.setText(estudianteNombre != null ? estudianteNombre : "Estudiante");
        tvMateriaModalidadAtencion.setText((materia != null ? materia : "Asesoría") + " · Presencial");

        String inicio = horaInicio != null && horaInicio.length() >= 5 ? horaInicio.substring(0, 5) : "10:00";
        String fin = horaFin != null && horaFin.length() >= 5 ? horaFin.substring(0, 5) : "10:30";
        tvHorarioAtencion.setText("🕒  " + fecha + " · " + inicio + "–" + fin);

        // Precargar horas estimadas
        etHoraIngreso.setText(inicio);
        etHoraSalida.setText(fin);

        // 4. Botón Guardar registro
        btnGuardarRegistro.setOnClickListener(v -> guardarAtencion());

        // 5. Barra inferior
        findViewById(R.id.navInicioRegistrar).setOnClickListener(v -> finish());
    }

    private void guardarAtencion() {
        tvErrorAtencion.setVisibility(View.GONE);

        String ingreso = etHoraIngreso.getText() != null ? etHoraIngreso.getText().toString().trim() : "";
        String salida = etHoraSalida.getText() != null ? etHoraSalida.getText().toString().trim() : "";
        String tema = etTemaTratado.getText() != null ? etTemaTratado.getText().toString().trim() : "";
        String compromisos = etCompromisos.getText() != null ? etCompromisos.getText().toString().trim() : "";

        if (ingreso.isEmpty() || salida.isEmpty() || tema.isEmpty() || compromisos.isEmpty()) {
            tvErrorAtencion.setText("Por favor completa las horas, el tema tratado y los compromisos.");
            tvErrorAtencion.setVisibility(View.VISIBLE);
            return;
        }

        // Formato seguro para LocalTime del backend (HH:mm:ss)
        if (ingreso.length() == 5) ingreso += ":00";
        if (salida.length() == 5) salida += ":00";

        btnGuardarRegistro.setEnabled(false);
        btnGuardarRegistro.setText("Guardando registro...");

        RegistroAtencionRequest request = new RegistroAtencionRequest(
                idProfesor, ingreso, salida, tema, compromisos, ""
        );

        ApiClient.getService().registrarAtencion(idAsesoria, request).enqueue(new Callback<ReporteDTO>() {
            @Override
            public void onResponse(Call<ReporteDTO> call, Response<ReporteDTO> response) {
                btnGuardarRegistro.setEnabled(true);
                btnGuardarRegistro.setText("Guardar registro");

                if (response.isSuccessful()) {
                    Toast.makeText(RegistrarAtencionActivity.this, "¡Atención guardada! La asesoría pasó a Finalizada.", Toast.LENGTH_LONG).show();
                    finish(); // Regresa a la agenda y se recargará automáticamente
                } else {
                    tvErrorAtencion.setText("No se pudo registrar la atención (código " + response.code() + ").");
                    tvErrorAtencion.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ReporteDTO> call, Throwable t) {
                btnGuardarRegistro.setEnabled(true);
                btnGuardarRegistro.setText("Guardar registro");
                tvErrorAtencion.setText("Error al conectar con el servidor: " + t.getMessage());
                tvErrorAtencion.setVisibility(View.VISIBLE);
            }
        });
    }
}
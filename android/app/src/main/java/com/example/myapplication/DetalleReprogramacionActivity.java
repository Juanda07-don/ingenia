package com.example.myapplication;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleReprogramacionActivity extends AppCompatActivity {

    private Long idAsesoria;
    private Long idEstudiante;
    private String materia;
    private String profesor;
    private String fechaOriginal;
    private String horaInicioOriginal;
    private String horaFinOriginal;
    private String lugar;
    private String nuevaFecha;
    private String nuevaHoraInicio;
    private String nuevaHoraFin;
    private String motivo;

    private TextView tvMateriaDetalleRepro;
    private TextView tvProfesorDetalleRepro;
    private TextView tvHorarioPropuestoResumen;
    private TextView tvLugarDetalleRepro;
    private TextView tvHorarioOriginalTachado;
    private TextView tvFechaNuevaPropuesta;
    private TextView tvHorarioNuevoPropuesta;
    private TextView tvLugarNuevoPropuesta;
    private TextView tvMotivoPropuesta;

    private MaterialButton btnAceptarNuevoHorario;
    private MaterialButton btnRechazarNuevoHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reprogramacion);

        // 1. Recibir datos del Intent
        idAsesoria = getIntent().getLongExtra("idAsesoria", -1L);
        idEstudiante = getIntent().getLongExtra("idEstudiante", -1L);
        materia = getIntent().getStringExtra("materia");
        profesor = getIntent().getStringExtra("profesor");
        fechaOriginal = getIntent().getStringExtra("fechaOriginal");
        horaInicioOriginal = getIntent().getStringExtra("horaInicioOriginal");
        horaFinOriginal = getIntent().getStringExtra("horaFinOriginal");
        lugar = getIntent().getStringExtra("lugar");
        nuevaFecha = getIntent().getStringExtra("nuevaFecha");
        nuevaHoraInicio = getIntent().getStringExtra("nuevaHoraInicio");
        nuevaHoraFin = getIntent().getStringExtra("nuevaHoraFin");
        motivo = getIntent().getStringExtra("motivo");

        // 2. Vincular vistas
        findViewById(R.id.btnVolverDetalleRepro).setOnClickListener(v -> finish());
        tvMateriaDetalleRepro = findViewById(R.id.tvMateriaDetalleRepro);
        tvProfesorDetalleRepro = findViewById(R.id.tvProfesorDetalleRepro);
        tvHorarioPropuestoResumen = findViewById(R.id.tvHorarioPropuestoResumen);
        tvLugarDetalleRepro = findViewById(R.id.tvLugarDetalleRepro);
        tvHorarioOriginalTachado = findViewById(R.id.tvHorarioOriginalTachado);
        tvFechaNuevaPropuesta = findViewById(R.id.tvFechaNuevaPropuesta);
        tvHorarioNuevoPropuesta = findViewById(R.id.tvHorarioNuevoPropuesta);
        tvLugarNuevoPropuesta = findViewById(R.id.tvLugarNuevoPropuesta);
        tvMotivoPropuesta = findViewById(R.id.tvMotivoPropuesta);

        btnAceptarNuevoHorario = findViewById(R.id.btnAceptarNuevoHorario);
        btnRechazarNuevoHorario = findViewById(R.id.btnRechazarNuevoHorario);

        // 3. Pintar datos de la cita y la propuesta
        tvMateriaDetalleRepro.setText(materia != null ? materia : "Cálculo integral");
        tvProfesorDetalleRepro.setText(profesor != null ? profesor : "Docente UAO");

        String nInicio = nuevaHoraInicio != null && nuevaHoraInicio.length() >= 5 ? nuevaHoraInicio.substring(0, 5) : "10:00";
        String nFin = nuevaHoraFin != null && nuevaHoraFin.length() >= 5 ? nuevaHoraFin.substring(0, 5) : "10:30";
        tvHorarioPropuestoResumen.setText("🕒  " + (nuevaFecha != null ? nuevaFecha : "") + " · " + nInicio + "–" + nFin);
        tvLugarDetalleRepro.setText("📍  " + (lugar != null ? lugar : "Cubículo docente"));

        // Efecto tachado en el horario original
        String oInicio = horaInicioOriginal != null && horaInicioOriginal.length() >= 5 ? horaInicioOriginal.substring(0, 5) : "14:00";
        String oFin = horaFinOriginal != null && horaFinOriginal.length() >= 5 ? horaFinOriginal.substring(0, 5) : "14:30";
        tvHorarioOriginalTachado.setText(fechaOriginal + " · " + oInicio + "–" + oFin);
        tvHorarioOriginalTachado.setPaintFlags(tvHorarioOriginalTachado.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Nuevo horario
        tvFechaNuevaPropuesta.setText(nuevaFecha != null ? nuevaFecha : "Nueva fecha propuesta");
        tvHorarioNuevoPropuesta.setText(nInicio + "–" + nFin);
        tvLugarNuevoPropuesta.setText(lugar != null ? lugar : "Cubículo docente");
        tvMotivoPropuesta.setText("Motivo: " + (motivo != null && !motivo.isEmpty() ? motivo : "Ajuste de horario por parte del docente"));

        // 4. Botones de acción
        btnAceptarNuevoHorario.setOnClickListener(v -> responder(true));
        btnRechazarNuevoHorario.setOnClickListener(v -> confirmarRechazo());

        // 5. Barra inferior
        findViewById(R.id.navInicioDetalleRepro).setOnClickListener(v -> finish());
    }

    private void responder(boolean aceptar) {
        if (idAsesoria == -1L || idEstudiante == -1L) return;

        btnAceptarNuevoHorario.setEnabled(false);
        btnRechazarNuevoHorario.setEnabled(false);
        btnAceptarNuevoHorario.setText("Enviando respuesta...");

        RespuestaReprogramacionRequest request = new RespuestaReprogramacionRequest(idEstudiante, aceptar);
        ApiClient.getService().responderReprogramacion(idAsesoria, request).enqueue(new Callback<AsesoriaDTO>() {
            @Override
            public void onResponse(Call<AsesoriaDTO> call, Response<AsesoriaDTO> response) {
                btnAceptarNuevoHorario.setEnabled(true);
                btnRechazarNuevoHorario.setEnabled(true);
                btnAceptarNuevoHorario.setText("Aceptar nuevo horario");

                if (response.isSuccessful()) {
                    if (aceptar) {
                        Toast.makeText(DetalleReprogramacionActivity.this, "¡Nuevo horario confirmado! La cita quedó agendada.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DetalleReprogramacionActivity.this, "Has cancelado la propuesta de reprogramación.", Toast.LENGTH_LONG).show();
                    }
                    finish(); // Regresa a la lista de asesorías
                } else {
                    Toast.makeText(DetalleReprogramacionActivity.this, "No se pudo procesar la respuesta (código " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AsesoriaDTO> call, Throwable t) {
                btnAceptarNuevoHorario.setEnabled(true);
                btnRechazarNuevoHorario.setEnabled(true);
                btnAceptarNuevoHorario.setText("Aceptar nuevo horario");
                Toast.makeText(DetalleReprogramacionActivity.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmarRechazo() {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar propuesta")
                .setMessage("¿Deseas rechazar este nuevo horario propuesto?")
                .setPositiveButton("Sí, rechazar", (dialog, which) -> responder(false))
                .setNegativeButton("Volver", null)
                .show();
    }
}
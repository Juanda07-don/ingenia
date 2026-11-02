package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmarReservaActivity extends AppCompatActivity {

    private Long idDisponibilidad;
    private Long idEstudiante;
    private String nombreEstudiante;
    private String nombreProfesor;
    private String materiaProfesor;
    private String cubiculoProfesor;
    private String fecha;
    private String horaInicio;
    private String horaFin;

    // Vistas de Confirmación (Página 5)
    private View layoutConfirmacion;
    private TextView tvProfesorResumen;
    private TextView tvMateriaResumen;
    private TextView tvFechaResumen;
    private TextView tvHoraResumen;
    private TextView tvLugarResumen;
    private MaterialButton btnSepararEspacio;
    private MaterialButton btnCambiarHorario;

    // Vistas de Éxito (Página 6)
    private LinearLayout layoutExito;
    private TextView tvSubtituloExito;
    private TextView tvMateriaExito;
    private TextView tvProfesorExito;
    private TextView tvHorarioExito;
    private TextView tvLugarExito;
    private TextView tvCodigoReserva;
    private MaterialButton btnVolverInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_reserva);

        // 1. Recibir datos del Intent
        idDisponibilidad = getIntent().getLongExtra("idDisponibilidad", -1L);
        idEstudiante = getIntent().getLongExtra("idEstudiante", -1L);
        nombreEstudiante = getIntent().getStringExtra("nombreEstudiante");
        nombreProfesor = getIntent().getStringExtra("nombreProfesor");
        materiaProfesor = getIntent().getStringExtra("materiaProfesor");
        cubiculoProfesor = getIntent().getStringExtra("cubiculoProfesor");
        fecha = getIntent().getStringExtra("fecha");
        horaInicio = getIntent().getStringExtra("horaInicio");
        horaFin = getIntent().getStringExtra("horaFin");

        // 2. Vincular vistas
        layoutConfirmacion = findViewById(R.id.layoutConfirmacion);
        layoutExito = findViewById(R.id.layoutExito);

        tvProfesorResumen = findViewById(R.id.tvProfesorResumen);
        tvMateriaResumen = findViewById(R.id.tvMateriaResumen);
        tvFechaResumen = findViewById(R.id.tvFechaResumen);
        tvHoraResumen = findViewById(R.id.tvHoraResumen);
        tvLugarResumen = findViewById(R.id.tvLugarResumen);

        btnSepararEspacio = findViewById(R.id.btnSepararEspacio);
        btnCambiarHorario = findViewById(R.id.btnCambiarHorario);
        findViewById(R.id.btnVolverConfirmacion).setOnClickListener(v -> finish());

        tvSubtituloExito = findViewById(R.id.tvSubtituloExito);
        tvMateriaExito = findViewById(R.id.tvMateriaExito);
        tvProfesorExito = findViewById(R.id.tvProfesorExito);
        tvHorarioExito = findViewById(R.id.tvHorarioExito);
        tvLugarExito = findViewById(R.id.tvLugarExito);
        tvCodigoReserva = findViewById(R.id.tvCodigoReserva);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);

        // 3. Pintar los datos en el resumen
        tvProfesorResumen.setText(nombreProfesor != null ? nombreProfesor : "Docente UAO");
        tvMateriaResumen.setText(materiaProfesor != null ? materiaProfesor : "Asesoría académica");
        tvFechaResumen.setText(fecha != null ? fecha : "Fecha por definir");

        String inicio = horaInicio != null && horaInicio.length() >= 5 ? horaInicio.substring(0, 5) : horaInicio;
        String fin = horaFin != null && horaFin.length() >= 5 ? horaFin.substring(0, 5) : horaFin;
        tvHoraResumen.setText(inicio + "–" + fin);

        String lugar = cubiculoProfesor != null && !cubiculoProfesor.isEmpty() ? cubiculoProfesor : "Cubículo docente";
        tvLugarResumen.setText(lugar);

        // 4. Botón Cambiar Horario (vuelve al calendario)
        btnCambiarHorario.setOnClickListener(v -> finish());

        // 5. Botón Separar Espacio -> Llama al backend
        btnSepararEspacio.setOnClickListener(v -> realizarReserva());

        // 6. Botón Volver al inicio tras el éxito
        btnVolverInicio.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmarReservaActivity.this, HomeEstudianteActivity.class);
            intent.putExtra("idUsuario", idEstudiante);
            intent.putExtra("nombre", nombreEstudiante);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void realizarReserva() {
        if (idEstudiante == -1L || idDisponibilidad == -1L) {
            Toast.makeText(this, "Datos incompletos: Estudiante=" + idEstudiante + ", Horario=" + idDisponibilidad, Toast.LENGTH_SHORT).show();
            return;
        }

        btnSepararEspacio.setEnabled(false);
        btnSepararEspacio.setText("Separando espacio...");

        ReservaRequest request = new ReservaRequest(idEstudiante, idDisponibilidad);
        ApiClient.getService().reservarAsesoria(request).enqueue(new Callback<AsesoriaDTO>() {
            @Override
            public void onResponse(Call<AsesoriaDTO> call, Response<AsesoriaDTO> response) {
                btnSepararEspacio.setEnabled(true);
                btnSepararEspacio.setText("Separar espacio");

                if (response.isSuccessful() && response.body() != null) {
                    AsesoriaDTO creada = response.body();
                    mostrarExito(creada);
                } else if (response.code() == 409) {
                    // Página 20 de Figma: Ese horario acaba de ocuparse
                    mostrarAlertaHorarioOcupado();
                } else {
                    Toast.makeText(ConfirmarReservaActivity.this, "No fue posible realizar la reserva.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AsesoriaDTO> call, Throwable t) {
                btnSepararEspacio.setEnabled(true);
                btnSepararEspacio.setText("Separar espacio");
                Toast.makeText(ConfirmarReservaActivity.this, "Error de conexión con el servidor.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarExito(AsesoriaDTO a) {
        layoutConfirmacion.setVisibility(View.GONE);
        layoutExito.setVisibility(View.VISIBLE);

        tvSubtituloExito.setText("Tu espacio quedó reservado de inmediato con " + (nombreProfesor != null ? nombreProfesor : "el docente") + ".");
        tvMateriaExito.setText(a.getMateria());
        tvProfesorExito.setText(a.getProfesor());

        String inicio = a.getHoraInicio() != null && a.getHoraInicio().length() >= 5 ? a.getHoraInicio().substring(0, 5) : a.getHoraInicio();
        String fin = a.getHoraFin() != null && a.getHoraFin().length() >= 5 ? a.getHoraFin().substring(0, 5) : a.getHoraFin();
        tvHorarioExito.setText("🕒  " + a.getFecha() + " · " + inicio + "–" + fin);

        String lugar = a.getLugar() != null ? a.getLugar() : "Cubículo docente";
        tvLugarExito.setText("📍  " + lugar);

        String cod = a.getCodigoReserva() != null ? a.getCodigoReserva() : "ING-" + a.getId();
        tvCodigoReserva.setText("🔔  Te enviaremos un recordatorio 30 minutos antes. Código: " + cod);
    }

    private void mostrarAlertaHorarioOcupado() {
        new AlertDialog.Builder(this)
                .setTitle("Ese horario acaba de ocuparse")
                .setMessage("Otro estudiante reservó este espacio justo antes. Elige otro horario para continuar con tu asesoría.")
                .setPositiveButton("Elegir otro horario", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
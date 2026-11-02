package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteAsesoriaActivity extends AppCompatActivity {

    private Long idAsesoria;
    private Long idUsuario;
    private String materia;
    private String profesor;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String lugar;
    private String codigoReserva;

    private TextView tvReporteMateria;
    private TextView tvReporteProfesor;
    private TextView tvReporteHorario;
    private TextView tvReporteLugar;
    private TextView tvReporteFechaCompleta;
    private TextView tvReporteTemaTratado;
    private TextView tvReporteCompromisos;
    private TextView tvNombreArchivoPdf;
    private MaterialButton btnDescargarPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_asesoria);

        // 1. Recibir datos del Intent
        idAsesoria = getIntent().getLongExtra("idAsesoria", -1L);
        idUsuario = getIntent().getLongExtra("idUsuario", -1L);
        materia = getIntent().getStringExtra("materia");
        profesor = getIntent().getStringExtra("profesor");
        fecha = getIntent().getStringExtra("fecha");
        horaInicio = getIntent().getStringExtra("horaInicio");
        horaFin = getIntent().getStringExtra("horaFin");
        lugar = getIntent().getStringExtra("lugar");
        codigoReserva = getIntent().getStringExtra("codigoReserva");

        // 2. Vincular vistas
        findViewById(R.id.btnVolverReporte).setOnClickListener(v -> finish());
        tvReporteMateria = findViewById(R.id.tvReporteMateria);
        tvReporteProfesor = findViewById(R.id.tvReporteProfesor);
        tvReporteHorario = findViewById(R.id.tvReporteHorario);
        tvReporteLugar = findViewById(R.id.tvReporteLugar);
        tvReporteFechaCompleta = findViewById(R.id.tvReporteFechaCompleta);
        tvReporteTemaTratado = findViewById(R.id.tvReporteTemaTratado);
        tvReporteCompromisos = findViewById(R.id.tvReporteCompromisos);
        tvNombreArchivoPdf = findViewById(R.id.tvNombreArchivoPdf);
        btnDescargarPdf = findViewById(R.id.btnDescargarPdf);

        // 3. Pintar datos de la tarjeta superior
        tvReporteMateria.setText(materia != null ? materia : "Asesoría académica");
        tvReporteProfesor.setText(profesor != null ? profesor : "Docente UAO");

        String inicio = horaInicio != null && horaInicio.length() >= 5 ? horaInicio.substring(0, 5) : horaInicio;
        String fin = horaFin != null && horaFin.length() >= 5 ? horaFin.substring(0, 5) : horaFin;
        tvReporteHorario.setText("🕒  " + fecha + " · " + inicio + "–" + fin);
        tvReporteLugar.setText("📍  " + (lugar != null ? lugar : "Cubículo docente"));

        String cod = codigoReserva != null ? codigoReserva : ("ING-" + idAsesoria);
        tvNombreArchivoPdf.setText("reporte-" + cod + ".pdf · 184 KB");

        // 4. Botón Descargar PDF
        btnDescargarPdf.setOnClickListener(v -> {
            Toast.makeText(this, "Descargando reporte oficial: reporte-" + cod + ".pdf", Toast.LENGTH_LONG).show();
        });

        // 5. Barra inferior
        findViewById(R.id.navInicioReporte).setOnClickListener(v -> finish());

        // 6. Consultar los detalles oficiales del reporte al Backend
        cargarReporteBackend();
    }

    private void cargarReporteBackend() {
        if (idAsesoria == -1L || idUsuario == -1L) return;

        ApiClient.getService().getReporte(idAsesoria, idUsuario).enqueue(new Callback<ReporteDTO>() {
            @Override
            public void onResponse(Call<ReporteDTO> call, Response<ReporteDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReporteDTO r = response.body();

                    tvReporteFechaCompleta.setText(r.getFecha() != null ? r.getFecha() : fecha);
                    tvReporteTemaTratado.setText(r.getTemaTratado() != null ? r.getTemaTratado() : "Sin tema registrado");

                    String comp = r.getCompromisos() != null ? r.getCompromisos() : "Sin compromisos";
                    if (!comp.startsWith("✓")) {
                        comp = "✓ " + comp;
                    }
                    tvReporteCompromisos.setText(comp);

                    String cod = r.getCodigoReserva() != null ? r.getCodigoReserva() : ("ING-" + idAsesoria);
                    tvNombreArchivoPdf.setText("reporte-" + cod + ".pdf · 184 KB");
                } else {
                    Toast.makeText(ReporteAsesoriaActivity.this, "No se pudo cargar el detalle del reporte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReporteDTO> call, Throwable t) {
                Toast.makeText(ReporteAsesoriaActivity.this, "Error de red al consultar el reporte", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
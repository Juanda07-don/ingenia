package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarAsesoriaActivity extends AppCompatActivity {

    private TextView tabMateria;
    private TextView tabProfesor;
    private TextView tvLabelBusqueda;
    private TextInputEditText etBusqueda;
    private TextView tvCantidadResultados;
    private TextView tvMensajeVacio;
    private RecyclerView rvProfesores;
    private ProfesorAdapter adapter;

    private boolean buscarPorMateria = true;
    private Long idEstudiante;
    private String nombreEstudiante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_asesoria);

        idEstudiante = getIntent().getLongExtra("idUsuario", -1L);
        nombreEstudiante = getIntent().getStringExtra("nombre");

        // 1. Vincular vistas
        tabMateria = findViewById(R.id.tabMateria);
        tabProfesor = findViewById(R.id.tabProfesor);
        tvLabelBusqueda = findViewById(R.id.tvLabelBusqueda);
        etBusqueda = findViewById(R.id.etBusqueda);
        tvCantidadResultados = findViewById(R.id.tvCantidadResultados);
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio);
        rvProfesores = findViewById(R.id.rvProfesores);

        // 2. Configurar RecyclerView con clic para abrir el perfil del profesor
        adapter = new ProfesorAdapter(profesor -> {
            Intent intent = new Intent(BuscarAsesoriaActivity.this, PerfilProfesorActivity.class);
            intent.putExtra("idProfesor", profesor.getId());
            intent.putExtra("nombreProfesor", profesor.getNombre());
            intent.putExtra("cubiculoProfesor", profesor.getCubiculo());
            String materia = (profesor.getMaterias() != null && !profesor.getMaterias().isEmpty())
                    ? profesor.getMaterias().get(0) : "";
            intent.putExtra("materiaProfesor", materia);
            intent.putExtra("idEstudiante", idEstudiante);
            intent.putExtra("nombreEstudiante", nombreEstudiante);
            startActivity(intent);
        });
        rvProfesores.setLayoutManager(new LinearLayoutManager(this));
        rvProfesores.setAdapter(adapter);

        // 3. Pestañas (Materia vs Profesor)
        tabMateria.setOnClickListener(v -> seleccionarTab(true));
        tabProfesor.setOnClickListener(v -> seleccionarTab(false));

        // 4. Búsqueda en tiempo real al escribir
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s != null ? s.toString().trim() : "";
                if (query.length() >= 3) {
                    realizarBusqueda(query);
                } else {
                    adapter.setProfesores(null);
                    rvProfesores.setVisibility(View.GONE);
                    tvMensajeVacio.setText("Escribe al menos tres caracteres para buscar.");
                    tvMensajeVacio.setVisibility(View.VISIBLE);
                    tvCantidadResultados.setText("0 resultados");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 5. Barra inferior: volver al inicio
        findViewById(R.id.navInicio).setOnClickListener(v -> finish());
    }

    private void seleccionarTab(boolean porMateria) {
        this.buscarPorMateria = porMateria;
        if (porMateria) {
            tabMateria.setBackgroundResource(R.drawable.badge_bg);
            tabMateria.setTextColor(getColor(R.color.ingenia_primary));

            tabProfesor.setBackgroundResource(android.R.color.transparent);
            tabProfesor.setTextColor(getColor(R.color.ingenia_text_secondary));

            tvLabelBusqueda.setText("Materia o tema");
            etBusqueda.setHint("Ej. Cálculo integral");
        } else {
            tabProfesor.setBackgroundResource(R.drawable.badge_bg);
            tabProfesor.setTextColor(getColor(R.color.ingenia_primary));

            tabMateria.setBackgroundResource(android.R.color.transparent);
            tabMateria.setTextColor(getColor(R.color.ingenia_text_secondary));

            tvLabelBusqueda.setText("Nombre del profesor");
            etBusqueda.setHint("Ej. Laura Méndez");
        }

        String texto = etBusqueda.getText() != null ? etBusqueda.getText().toString().trim() : "";
        if (texto.length() >= 3) {
            realizarBusqueda(texto);
        }
    }

    private void realizarBusqueda(String query) {
        Call<List<ProfesorDTO>> call;
        if (buscarPorMateria) {
            call = ApiClient.getService().buscarPorMateria(query);
        } else {
            call = ApiClient.getService().buscarPorNombre(query);
        }

        call.enqueue(new Callback<List<ProfesorDTO>>() {
            @Override
            public void onResponse(Call<List<ProfesorDTO>> call, Response<List<ProfesorDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<ProfesorDTO> profesores = response.body();
                    adapter.setProfesores(profesores);
                    rvProfesores.setVisibility(View.VISIBLE);
                    tvMensajeVacio.setVisibility(View.GONE);
                    tvCantidadResultados.setText(profesores.size() + (profesores.size() == 1 ? " resultado" : " resultados"));
                } else {
                    adapter.setProfesores(null);
                    rvProfesores.setVisibility(View.GONE);
                    tvMensajeVacio.setText("No encontramos profesores con ese criterio.");
                    tvMensajeVacio.setVisibility(View.VISIBLE);
                    tvCantidadResultados.setText("0 resultados");
                }
            }

            @Override
            public void onFailure(Call<List<ProfesorDTO>> call, Throwable t) {
                adapter.setProfesores(null);
                rvProfesores.setVisibility(View.GONE);
                tvMensajeVacio.setText("Error al conectar con el servidor.");
                tvMensajeVacio.setVisibility(View.VISIBLE);
            }
        });
    }
}
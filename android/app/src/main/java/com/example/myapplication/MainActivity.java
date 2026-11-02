package com.example.myapplication;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etCorreo;
    private TextInputEditText etContrasena;
    private TextView tvError;
    private MaterialButton btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        tvError = findViewById(R.id.tvError);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> intentarIniciarSesion());
    }

    private void intentarIniciarSesion() {
        String correo = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
        String contrasena = etContrasena.getText() != null ? etContrasena.getText().toString().trim() : "";

        tvError.setVisibility(View.GONE);

        if (correo.isEmpty() || contrasena.isEmpty()) {
            tvError.setText("Por favor completa todos los campos");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        if (!correo.endsWith("@uao.edu.co")) {
            tvError.setText("Debes usar tu correo institucional @uao.edu.co");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");

        LoginRequest request = new LoginRequest(correo, contrasena);
        ApiClient.getService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnIngresar.setEnabled(true);
                btnIngresar.setText("Ingresar");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse usuario = response.body();

                    if ("PROFESOR".equalsIgnoreCase(usuario.getRol())) {
                        // ROL DOCENTE: Abrir HomeProfesorActivity
                        Intent intent = new Intent(MainActivity.this, HomeProfesorActivity.class);
                        intent.putExtra("idUsuario", usuario.getIdUsuario());
                        intent.putExtra("nombre", usuario.getNombre());
                        intent.putExtra("rol", usuario.getRol());
                        startActivity(intent);
                        finish();
                    } else {
                        // ROL ESTUDIANTE: Abrir HomeEstudianteActivity
                        Intent intent = new Intent(MainActivity.this, HomeEstudianteActivity.class);
                        intent.putExtra("idUsuario", usuario.getIdUsuario());
                        intent.putExtra("nombre", usuario.getNombre());
                        intent.putExtra("rol", usuario.getRol());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    tvError.setText("Credenciales incorrectas, intenta nuevamente");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnIngresar.setEnabled(true);
                btnIngresar.setText("Ingresar");

                tvError.setText("No pudimos conectarte con el servidor. Revisa tu conexión.");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}

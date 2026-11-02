package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("id")
    private Long id;
    private String nombre;
    private String rol;

    public Long getId() { return id; }
    public Long getIdUsuario() { return id; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
}

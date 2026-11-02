package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class DisponibilidadDTO {
    @SerializedName("id")
    private Long id;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String lugar;
    private String cubiculo;
    private String estado;
    private boolean reservable;

    public Long getId() { return id; }
    public Long getIdDisponibilidad() { return id; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    public String getCubiculo() { return cubiculo != null ? cubiculo : lugar; }
    public String getLugar() { return lugar; }
    public String getEstado() { return estado; }
    public boolean isReservable() { return reservable; }
}

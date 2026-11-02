package com.example.myapplication;

public class ConfigurarEspaciosRequest {
    private Long materiaId;
    private String fecha;
    private String horaInicio;
    private String horaFin;

    public ConfigurarEspaciosRequest(Long materiaId, String fecha, String horaInicio, String horaFin) {
        this.materiaId = materiaId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getMateriaId() { return materiaId; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
}

package com.example.myapplication;

public class RegistroAtencionRequest {
    private Long profesorId;
    private String horaIngreso;
    private String horaSalida;
    private String temaTratado;
    private String compromisos;
    private String observaciones;

    public RegistroAtencionRequest(Long profesorId, String horaIngreso, String horaSalida,
                                   String temaTratado, String compromisos, String observaciones) {
        this.profesorId = profesorId;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
        this.temaTratado = temaTratado;
        this.compromisos = compromisos;
        this.observaciones = observaciones;
    }

    public Long getProfesorId() { return profesorId; }
    public String getHoraIngreso() { return horaIngreso; }
    public String getHoraSalida() { return horaSalida; }
    public String getTemaTratado() { return temaTratado; }
    public String getCompromisos() { return compromisos; }
    public String getObservaciones() { return observaciones; }
}
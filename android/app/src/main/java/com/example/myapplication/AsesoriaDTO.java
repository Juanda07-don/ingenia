package com.example.myapplication;

public class AsesoriaDTO {
    private Long id;
    private String codigoReserva;
    private String estado;
    private String materia;
    private String profesor;
    private String estudiante;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String lugar;
    private PropuestaDTO propuesta;

    public Long getId() { return id; }
    public String getCodigoReserva() { return codigoReserva; }
    public String getEstado() { return estado; }
    public String getMateria() { return materia; }
    public String getProfesor() { return profesor; }
    public String getEstudiante() { return estudiante; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    public String getLugar() { return lugar; }
    public PropuestaDTO getPropuesta() { return propuesta; }
}

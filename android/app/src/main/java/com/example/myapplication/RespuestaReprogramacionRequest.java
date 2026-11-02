package com.example.myapplication;

public class RespuestaReprogramacionRequest {
    private Long estudianteId;
    private boolean aceptar;

    public RespuestaReprogramacionRequest(Long estudianteId, boolean aceptar) {
        this.estudianteId = estudianteId;
        this.aceptar = aceptar;
    }

    public Long getEstudianteId() { return estudianteId; }
    public boolean isAceptar() { return aceptar; }
}

package com.example.myapplication;

public class ReservaRequest {
    private Long estudianteId;
    private Long disponibilidadId;

    public ReservaRequest(Long estudianteId, Long disponibilidadId) {
        this.estudianteId = estudianteId;
        this.disponibilidadId = disponibilidadId;
    }

    public Long getEstudianteId() { return estudianteId; }
    public Long getDisponibilidadId() { return disponibilidadId; }
}

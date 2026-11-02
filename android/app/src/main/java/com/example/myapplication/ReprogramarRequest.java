package com.example.myapplication;

public class ReprogramarRequest {
    private Long profesorId;
    private Long nuevaDisponibilidadId;
    private String motivo;

    public ReprogramarRequest(Long profesorId, Long nuevaDisponibilidadId, String motivo) {
        this.profesorId = profesorId;
        this.nuevaDisponibilidadId = nuevaDisponibilidadId;
        this.motivo = motivo;
    }

    public Long getProfesorId() { return profesorId; }
    public Long getNuevaDisponibilidadId() { return nuevaDisponibilidadId; }
    public String getMotivo() { return motivo; }
}

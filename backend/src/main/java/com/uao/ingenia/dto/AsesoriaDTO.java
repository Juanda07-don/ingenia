package com.uao.ingenia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AsesoriaDTO(Long id, String codigoReserva, String estado, String materia,
                        String profesor, String estudiante, LocalDate fecha,
                        LocalTime horaInicio, LocalTime horaFin, String lugar,
                        PropuestaDTO propuesta) {}
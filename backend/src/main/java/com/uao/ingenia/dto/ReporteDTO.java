package com.uao.ingenia.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReporteDTO(Long asesoriaId, String codigoReserva, String materia, String profesor,
                        String estudiante, LocalDate fecha, LocalTime horaIngreso, LocalTime horaSalida,
                        String temaTratado, String observaciones, String compromisos,
                        LocalDateTime fechaGeneracion) {
                            
                        }

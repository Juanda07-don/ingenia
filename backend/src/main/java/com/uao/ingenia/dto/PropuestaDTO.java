package com.uao.ingenia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record PropuestaDTO(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                        String lugar, String motivo) {
                            
                        }


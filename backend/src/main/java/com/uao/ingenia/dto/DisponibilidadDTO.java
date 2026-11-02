package com.uao.ingenia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibilidadDTO(Long id, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                String lugar, String estado, boolean reservable) {
                                    
                                }

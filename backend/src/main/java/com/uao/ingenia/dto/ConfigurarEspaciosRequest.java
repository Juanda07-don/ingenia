package com.uao.ingenia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConfigurarEspaciosRequest(Long materiaId, LocalDate fecha,
                                        LocalTime horaInicio, LocalTime horaFin) {
                                            
                                        }
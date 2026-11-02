package com.uao.ingenia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record EspacioDTO(Long id, String materia, LocalDate fecha, LocalTime horaInicio,
                        LocalTime horaFin, String lugar, String estado) {}

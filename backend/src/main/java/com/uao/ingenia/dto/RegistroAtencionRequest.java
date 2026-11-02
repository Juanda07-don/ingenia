package com.uao.ingenia.dto;

import java.time.LocalTime;

public record RegistroAtencionRequest(Long profesorId, LocalTime horaIngreso, LocalTime horaSalida,
                                    String temaTratado, String compromisos, String observaciones) {}



package com.uao.ingenia.service;

import com.uao.ingenia.exception.OperacionInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ReglasDeTiempo {

    private final boolean validarFechaSesion;

    public ReglasDeTiempo(@Value("${ingenia.reglas.validar-fecha-sesion:false}") boolean validarFechaSesion) {
        this.validarFechaSesion = validarFechaSesion;
    }

    // true si ese momento ya empezó o ya pasó
    public boolean yaPaso(LocalDate fecha, LocalTime hora) {
        if (!validarFechaSesion) {
            return false;
        }
        return !LocalDateTime.of(fecha, hora).isAfter(LocalDateTime.now());
    }

    // Solo actúa si el interruptor está en true
    public void exigirDiaDeLaSesion(LocalDate fecha, String mensaje) {
        if (validarFechaSesion && fecha.isAfter(LocalDate.now())) {
            throw new OperacionInvalidaException(mensaje);
        }
    }
}

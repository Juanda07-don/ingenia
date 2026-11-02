package com.uao.ingenia.exception;

public class HorarioOcupadoException extends RuntimeException {
    public HorarioOcupadoException() { super("Ese horario acaba de ocuparse"); }
}
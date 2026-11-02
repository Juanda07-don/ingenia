package com.uao.ingenia.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Credenciales incorrectas, intenta nuevamente");
    }
}

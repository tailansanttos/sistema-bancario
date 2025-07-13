package com.tailan.santos.banco.exception;

public class TransacaoNotFoundException extends RuntimeException {
    public TransacaoNotFoundException(String message) {
        super(message);
    }
}

package com.tailan.santos.banco.exception;

public class ClienteNotFound extends RuntimeException {
    public ClienteNotFound(String message) {
        super(message);
    }
}

package com.tailan.santos.banco.dtos.erro;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, Integer status, String error, String message) {
}

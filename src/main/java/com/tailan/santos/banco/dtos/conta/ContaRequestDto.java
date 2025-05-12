package com.tailan.santos.banco.dtos.conta;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContaRequestDto(
        @NotNull(message = "Precisa passar um saldo.")
        BigDecimal saldo) {
}

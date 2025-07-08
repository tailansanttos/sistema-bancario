package com.tailan.santos.banco.dtos.cliente;

import jakarta.validation.constraints.NotNull;

public record ClienteUpdateRequestDto(
        @NotNull(message = "Nome não pode ser nulo.")
        String nome,
        @NotNull(message = "Telefone não pode ser nulo.")
        String telefone,
        @NotNull(message = "Endereço não pode ser nulo.")
        String endereco) {
}

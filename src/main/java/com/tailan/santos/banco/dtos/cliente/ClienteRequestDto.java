package com.tailan.santos.banco.dtos.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteRequestDto(
        @NotBlank
        String nome,
        @CPF
        @NotBlank
        String cpf,
        @Email
        @NotBlank
        String email,
        @NotBlank
        String telefone,
        @NotBlank
        String endereco) {
}

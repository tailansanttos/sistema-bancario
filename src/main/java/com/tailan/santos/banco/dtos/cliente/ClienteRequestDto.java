package com.tailan.santos.banco.dtos.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteRequestDto(
        @NotNull(message = "Nome não pode ser nulo.")
        String nome,
        @CPF(message = "Campo CPF precisa ser válido!")
        String cpf,
        @Email(message = "Campo email precisa ser válido.")
        String email,
        @NotNull(message = "Telefone não pode ser nulo.")
        String telefone,
        @NotNull(message = "Endereço não pode ser nulo.")
        String endereco)
{
}

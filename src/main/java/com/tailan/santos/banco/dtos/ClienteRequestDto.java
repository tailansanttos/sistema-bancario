package com.tailan.santos.banco.dtos;

import com.tailan.santos.banco.model.Conta;

public record ClienteRequestDto(String nome, String cpf, String email, String telefone, String endereco) {
}

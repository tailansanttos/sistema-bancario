package com.tailan.santos.banco.dtos.cliente;

public record ClienteRequestDto(String nome, String cpf, String email, String telefone, String endereco) {
}

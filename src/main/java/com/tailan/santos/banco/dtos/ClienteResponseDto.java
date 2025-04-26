package com.tailan.santos.banco.dtos;

import java.util.UUID;

public record ClienteResponseDto(String nome, String email, String telefone, String endereco, UUID contaId) {
}

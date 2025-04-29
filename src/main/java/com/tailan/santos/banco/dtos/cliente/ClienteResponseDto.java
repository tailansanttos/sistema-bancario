package com.tailan.santos.banco.dtos.cliente;

import java.util.UUID;

public record ClienteResponseDto(UUID clienteId, String nome, String email, String telefone, String endereco) {
}

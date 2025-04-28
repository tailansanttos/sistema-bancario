package com.tailan.santos.banco.dtos.conta;

import java.util.UUID;

public record ContaResponseDto(UUID contaId, String nomeCliente, String emailCliente, String telefoneCliente, String enderecoCliente) {
}

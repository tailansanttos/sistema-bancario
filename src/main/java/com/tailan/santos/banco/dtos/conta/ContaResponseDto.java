package com.tailan.santos.banco.dtos.conta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponseDto(UUID contaId,
                               BigDecimal saldoConta,
                               String nomeCliente,
                               String emailCliente,
                               String telefoneCliente,
                               String enderecoCliente) {
}

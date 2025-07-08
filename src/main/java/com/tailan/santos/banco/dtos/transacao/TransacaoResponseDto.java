package com.tailan.santos.banco.dtos.transacao;

import com.tailan.santos.banco.enums.TransacaoStatus;
import com.tailan.santos.banco.enums.TransacaoTipo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoResponseDto(UUID transacaoId,
                                   BigDecimal valor,
                                   LocalDateTime dataHora,
                                   TransacaoTipo tipo,
                                   UUID contaOrigem,
                                   UUID contaDestino,
                                   TransacaoStatus status) {
}

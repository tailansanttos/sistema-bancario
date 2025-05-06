package com.tailan.santos.banco.dtos.transacao;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaDto(UUID contaTransfereId, UUID contaRecebeId, BigDecimal valor) {
}

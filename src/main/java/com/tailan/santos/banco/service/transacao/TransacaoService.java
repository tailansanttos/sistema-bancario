package com.tailan.santos.banco.service.transacao;

import com.tailan.santos.banco.dtos.transacao.DepositoESaqueTransacaoDto;
import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import com.tailan.santos.banco.dtos.transacao.TransferenciaDto;
import com.tailan.santos.banco.model.Transacao;
import com.tailan.santos.banco.enums.TransacaoStatus;

import java.util.List;
import java.util.UUID;

public interface TransacaoService {
    TransacaoResponseDto realizarDeposito(UUID contaId, DepositoESaqueTransacaoDto depositoRequest);
    TransacaoResponseDto realizarSaque(UUID contaId, DepositoESaqueTransacaoDto saqueRequest);
    TransacaoResponseDto realizarTransferencia(TransferenciaDto transacaoRequest);
    List<TransacaoResponseDto> extratoConta(UUID contaId);

    TransacaoResponseDto entityToDto(Transacao transacao);

    void processarTransferenciaRecebida(UUID transacaoId);
    void atualizarStatusTransacao(UUID transacaoId, TransacaoStatus status);

}

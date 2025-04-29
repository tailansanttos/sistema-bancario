package com.tailan.santos.banco.service;

import com.tailan.santos.banco.dtos.transacao.DepositoESaqueTransacaoDto;
import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import com.tailan.santos.banco.dtos.transacao.TransferenciaDto;
import com.tailan.santos.banco.exception.ContaNotFoundException;
import com.tailan.santos.banco.exception.SaldoInsuficienteException;
import com.tailan.santos.banco.model.Conta;
import com.tailan.santos.banco.model.Transacao;
import com.tailan.santos.banco.model.TransacaoStatus;
import com.tailan.santos.banco.model.TransacaoTipo;
import com.tailan.santos.banco.repositories.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaService contaService) {
        this.transacaoRepository = transacaoRepository;
        this.contaService = contaService;
    }

    public TransacaoResponseDto depositoConta(UUID contaId, DepositoESaqueTransacaoDto depositoDto) {
        Conta buscarConta = contaService.pegarContaPorId(contaId);
        if (buscarConta == null) {
            throw new ContaNotFoundException("Conta não encontrada");
        }

        if (depositoDto.valor().compareTo(BigDecimal.ZERO) <0) {
            throw new SaldoInsuficienteException("Valor do deposito precisa ser maior que zero");
        }

        contaService.adicionarSaldo(buscarConta, depositoDto.valor());

        Transacao transacao = new Transacao();
        transacao.setContaDestino(buscarConta);
        transacao.setValor(depositoDto.valor());
        transacao.setStatus(TransacaoStatus.CONCLUIDO);
        transacao.setTipo(TransacaoTipo.DEPOSITO);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setContaOrigem(null);

        transacaoRepository.save(transacao);

        TransacaoResponseDto transacaoResponseDto = new TransacaoResponseDto(
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                //Se contaOrigem não for null, pega o id. Se for null, coloca null mesmo.
                transacao.getContaOrigem() != null ? transacao.getContaOrigem().getId():null,
                transacao.getContaDestino().getId(),
                transacao.getStatus()
        );

        return transacaoResponseDto;
    }

    public TransacaoResponseDto saqueConta(UUID contaId, DepositoESaqueTransacaoDto saqueDto) {
        Conta buscarConta = contaService.pegarContaPorId(contaId);
        if (buscarConta == null) {
            throw new ContaNotFoundException("Conta não encontrada");
        }

        if (buscarConta.getSaldo().compareTo(saqueDto.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo da conta insuficiente");
        }

        contaService.removeSaldo(buscarConta, saqueDto.valor());

        Transacao transacao = new Transacao();
        transacao.setContaOrigem(buscarConta);
        transacao.setValor(saqueDto.valor());
        transacao.setStatus(TransacaoStatus.CONCLUIDO);
        transacao.setTipo(TransacaoTipo.SAQUE);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setContaDestino(null);
        transacaoRepository.save(transacao);

        TransacaoResponseDto transacaoResponseDto = new TransacaoResponseDto(
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                transacao.getContaOrigem().getId(),
                //Se contaDestino não for null, pega o id. Se for null, coloca null mesmo.
                transacao.getContaDestino()!= null ? transacao.getContaDestino().getId():null,
                transacao.getStatus()
        );
        return transacaoResponseDto;
    }


    public TransacaoResponseDto transferenciaContas(TransferenciaDto transferenciaDto){
        BigDecimal valorTransferencia = transferenciaDto.valor();

        Conta contaTransfere = contaService.pegarContaPorId(transferenciaDto.contaTransfereId());
        Conta contaRecebe = contaService.pegarContaPorId(transferenciaDto.contaRecebeId());

        //VERIFICA SE AS CONTAS NAO SAO AS MESMAS
        if (contaRecebe.getId() == contaTransfere.getId()) {
            throw new ContaNotFoundException("Não pode transferencia entre as mesmas contas.");
        }

        //VERIFICA SE A CONTA QUE TRANSFERE TEM SALDO
        if (contaTransfere.getSaldo().compareTo(valorTransferencia)<0){
            throw new SaldoInsuficienteException("Saldo da conta insuficiente");
        }

        //VERIFICA SE O VALOR DA TRANSFERENCIA É MAIOR QUE 0
        if (valorTransferencia.compareTo(BigDecimal.ZERO) <0){
            throw new SaldoInsuficienteException("Valor do deposito precisa ser maior que zero");
        }

        contaService.removeSaldo(contaTransfere, valorTransferencia);
        contaService.adicionarSaldo(contaRecebe, valorTransferencia);
        Transacao transacao = new Transacao();
        transacao.setContaOrigem(contaRecebe);
        transacao.setValor(transferenciaDto.valor());
        transacao.setStatus(TransacaoStatus.CONCLUIDO);
        transacao.setTipo(TransacaoTipo.TRANSFERENCIA);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setContaDestino(contaRecebe);
        transacaoRepository.save(transacao);

        TransacaoResponseDto transacaoResponseDto = new TransacaoResponseDto(
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                transacao.getContaOrigem().getId(),
                transacao.getContaDestino().getId(),
                transacao.getStatus()
        );
        return transacaoResponseDto;

    }


    public List<TransacaoResponseDto> extratoConta(UUID contaId) {
        List<Transacao> buscarTranscao = transacaoRepository.findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(contaId, contaId);
        return buscarTranscao.stream().map(transacao -> new TransacaoResponseDto(
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                transacao.getContaOrigem() != null ? transacao.getContaOrigem().getId():null,
                transacao.getContaDestino()!= null ? transacao.getContaDestino().getId():null,
                transacao.getStatus()
        )).toList();
    }


}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;
    private final KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TransacaoService.class);

    public TransacaoService(TransacaoRepository transacaoRepository, ContaService contaService, KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate) {
        this.transacaoRepository = transacaoRepository;
        this.contaService = contaService;
        this.kafkaTemplate = kafkaTemplate;
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
                transacao.getId(),
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
                transacao.getId(),
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





    public List<TransacaoResponseDto> extratoConta(UUID contaId) {
        List<Transacao> buscarTranscao = transacaoRepository.findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(contaId, contaId);
        return buscarTranscao.stream().map(transacao -> new TransacaoResponseDto(
                transacao.getId(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                transacao.getContaOrigem() != null ? transacao.getContaOrigem().getId():null,
                transacao.getContaDestino()!= null ? transacao.getContaDestino().getId():null,
                transacao.getStatus()
        )).toList();
    }

    public TransacaoResponseDto transferenciaContas(TransferenciaDto transferenciaDto){
        BigDecimal valorTransferencia = transferenciaDto.valor();

        Conta contaTransfere = contaService.pegarContaPorId(transferenciaDto.contaTransfereId());
        Conta contaRecebe = contaService.pegarContaPorId(transferenciaDto.contaRecebeId());

        //VERIFICA SE AS CONTAS NAO SAO AS MESMAS
        if (contaRecebe.getId() == contaTransfere.getId()) {
            throw new ContaNotFoundException("Não pode transferencia entre as mesmas contas.");
        }

        //VERIFICA SE O VALOR DA TRANSFERENCIA É MAIOR QUE 0
        if (valorTransferencia.compareTo(BigDecimal.ZERO) <0){
            throw new SaldoInsuficienteException("Valor do deposito precisa ser maior que zero");
        }

        //CRIA UMA NOVA TRANSACAO
        Transacao transacao = new Transacao();
        transacao.setContaOrigem(contaTransfere);
        transacao.setValor(transferenciaDto.valor());
        transacao.setStatus(TransacaoStatus.PENDENTE);
        transacao.setTipo(TransacaoTipo.TRANSFERENCIA);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setContaDestino(contaRecebe);

        //SALVA NO BANCO
        transacaoRepository.save(transacao);



        TransacaoResponseDto transacaoResponseDto = new TransacaoResponseDto(
                transacao.getId(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                transacao.getContaOrigem().getId(),
                transacao.getContaDestino().getId(),
                transacao.getStatus());

        transacaoRepository.save(transacao);

        // Envia para Kafka (a transferência ainda não foi processada)
        kafkaTemplate.send("transferencias", transacaoResponseDto);
        logger.info("Transferencia em processamento.");
        //O STATUS VAI CHEGAR PENDENTE DO KAFKA, MAS O STATUS REAL É ATUALIZADO NO KAFKA, CONCLUIDO OU NÃO.

        return transacaoResponseDto;
    }



    @KafkaListener(topics = "transferencias", groupId = "transferencias-realizadas")

    public void consumirMensagens(TransacaoResponseDto transacaoResponseDto) {

        //AQUI VAI PEGAR A TRANSACAO PELO ID, E VAI VERIFICAR O SALDO, SE É VALIDO.
        //SE FOR VALIDO, VAI ATUALIZAR OS SALDOS NAS CONTAS ENVOLVIDA NA TRANFERENCIA, VAI ATUALIZAR O STATUS DA TRANSACAO PARA CONCLUIDO SE DER TUDO CERTO, E VAI MANDAR A MENSAGEM
       UUID transferenciaId = transacaoResponseDto.transacaoId();

        try{
            //PEGA A TRANSFERENCIA PELO ID, E  VAI ATUALIZAR O SALDO DAS CONTAS ENTRE A TRANSFERENCIA
            Transacao transacao = transacaoRepository.findById(transferenciaId).orElseThrow(() -> new ContaNotFoundException("Transação não encontrada."));
            BigDecimal valorTransferencia = transacao.getValor();

            //VERIFICA SE A TRANSACAO JA FOI PROCESSADA, PRA EVITAR DUPLICIDADE
            if (transacao.getStatus().equals(TransacaoStatus.CONCLUIDO) || transacao.getStatus().equals(TransacaoStatus.CANCELADO)){
                logger.info("Transação já foi processada.");
                return;
            }

            //VERIFICA SE A CONTA QUE TRANSFERE TEM SALDO
            if (transacao.getContaOrigem().getSaldo().compareTo(valorTransferencia)<0){
                //SE N TIVER, A TRANSFERENCIA SERÁ CANCELADA
                atualizarStatus(transacao.getId(), TransacaoStatus.CANCELADO);
                throw new SaldoInsuficienteException("Saldo insuficiente");
            }

            //ADICIONA E REMOVE O SALDO DAS COTNAS
            contaService.adicionarSaldo(transacao.getContaDestino(), valorTransferencia);
            contaService.removeSaldo(transacao.getContaOrigem(), valorTransferencia);

            atualizarStatus(transacao.getId(), TransacaoStatus.CONCLUIDO);

            logger.info("Trasferencia realizada com sucesso: valor={}, origem{}, destino={}, status={}",
                    transacao.getValor(),transacao.getContaOrigem(),transacao.getContaDestino(),transacao.getStatus());

        }catch (Exception e){
            atualizarStatus(transacaoResponseDto.transacaoId(), TransacaoStatus.CANCELADO);
            logger.info("Erro ao processar transferencia");
        }

    }



    public void atualizarStatus(UUID transacaoId, TransacaoStatus status) {
        Transacao transacao = transacaoRepository.findById(transacaoId).orElseThrow(() ->
                new ContaNotFoundException("Transação não encontrada"));
        transacao.setStatus(status);
        transacaoRepository.save(transacao);
    }





}

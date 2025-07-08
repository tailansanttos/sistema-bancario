package com.tailan.santos.banco.service.transacao;

import com.tailan.santos.banco.dtos.transacao.DepositoESaqueTransacaoDto;
import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import com.tailan.santos.banco.dtos.transacao.TransferenciaDto;
import com.tailan.santos.banco.exception.ContaNotFoundException;
import com.tailan.santos.banco.exception.SaldoInsuficienteException;
import com.tailan.santos.banco.model.Conta;
import com.tailan.santos.banco.model.Transacao;
import com.tailan.santos.banco.enums.TransacaoStatus;
import com.tailan.santos.banco.enums.TransacaoTipo;
import com.tailan.santos.banco.repositories.TransacaoRepository;

import com.tailan.santos.banco.service.conta.ContaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransacaoServiceImpl implements TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;
    private final KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TransacaoServiceImpl.class);

    public TransacaoServiceImpl(TransacaoRepository transacaoRepository, ContaService contaService, KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate) {
        this.transacaoRepository = transacaoRepository;
        this.contaService = contaService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @Override
    public TransacaoResponseDto realizarDeposito(UUID contaId, DepositoESaqueTransacaoDto depositoRequest) {
        Conta conta = contaService.getContaById(contaId);
        if (depositoRequest.valor() == null || depositoRequest.valor().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Valor precisa ser positivo, e não pode ser nulo.");
        }

        Transacao transacao = new Transacao();
        transacao.setContaDestino(conta);
        transacao.setValor(depositoRequest.valor());
        transacao.setTipo(TransacaoTipo.DEPOSITO);
        transacao.setStatus(TransacaoStatus.CONCLUIDO);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setContaOrigem(null);

        this.contaService.adicionarSaldo(conta, depositoRequest.valor());

        Transacao transacaoSalva =  transacaoRepository.save(transacao);

        return entityToDto(transacaoSalva);

    }

    @Transactional
    @Override
    public TransacaoResponseDto realizarSaque(UUID contaId, DepositoESaqueTransacaoDto saqueRequest) {
        Conta conta = contaService.getContaById(contaId);

        if (saqueRequest.valor() == null || saqueRequest.valor().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Valor precisa ser positivo, e não pode ser nulo.");
        }

        Transacao transacao = new Transacao();
        transacao.setContaDestino(null);
        transacao.setValor(saqueRequest.valor());
        transacao.setTipo(TransacaoTipo.SAQUE);
        transacao.setStatus(TransacaoStatus.CONCLUIDO);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setContaOrigem(conta);

        this.contaService.removeSaldo(conta, saqueRequest.valor());

        Transacao transacaoSalva =  transacaoRepository.save(transacao);

        return entityToDto(transacaoSalva);
    }

    @Transactional
    @Override
    public TransacaoResponseDto realizarTransferencia(TransferenciaDto transacaoRequest) {
        BigDecimal valorTransferencia = transacaoRequest.valor();

        if (transacaoRequest.contaTransfereId().equals(transacaoRequest.contaRecebeId())){
            throw new IllegalArgumentException("Não pode haver transferencia entre a mesma conta");
        }
        if (valorTransferencia == null || valorTransferencia.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Valor da transferência precisa ser positivo, e não pode ser nulo."); // Mudei exceção e mensagem
        }

        //BUSCAR AS CONTAS
        UUID contaTransfereId = transacaoRequest.contaTransfereId();
        UUID contaRecebeId = transacaoRequest.contaRecebeId();


        Conta contaTransfere = this.contaService.getContaById(contaTransfereId);
        Conta contaRecebe = this.contaService.getContaById(contaRecebeId);

        if (contaTransfere.getSaldo().compareTo(valorTransferencia) <0){
            throw new SaldoInsuficienteException("Saldo insuficiente para transferencia.");
        }

        Transacao transacao = new Transacao();
        transacao.setContaOrigem(contaTransfere);
        transacao.setValor(valorTransferencia);
        transacao.setTipo(TransacaoTipo.TRANSFERENCIA);
        transacao.setStatus(TransacaoStatus.PENDENTE);
        transacao.setDataHora(LocalDateTime.now());
        transacao.setContaDestino(contaRecebe);

        Transacao transacaoSalva =  transacaoRepository.save(transacao);
        return entityToDto(transacaoSalva);
    }

    @Override
    @Transactional
    public List<TransacaoResponseDto> extratoConta(UUID contaId) {
        List<Transacao> listTransacaoConta = transacaoRepository.findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(contaId, contaId);
        return listTransacaoConta.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public TransacaoResponseDto entityToDto(Transacao transacao) {
        return new TransacaoResponseDto(
                transacao.getId(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getTipo(),
                transacao.getContaOrigem().getId() !=null ? transacao.getContaOrigem().getId() : null,
                transacao.getContaDestino().getId() !=null ? transacao.getContaDestino().getId() : null,
                transacao.getStatus()
        );


    }

    @Override
    @Transactional
    public void processarTransferenciaRecebida(UUID transacaoId) {
        Transacao transacao = transacaoRepository.findById(transacaoId).orElseThrow(() -> new ContaNotFoundException("Transação não encontrada."));

        try{
            if (transacao.getStatus().equals(TransacaoStatus.CONCLUIDO)){
                logger.warn("Transação ID {} ká foi processada", transacaoId);
                return;
            }
            UUID contaTransfereId = transacao.getContaOrigem().getId();
            UUID contaRecebeId = transacao.getContaDestino().getId();

            Conta contaTransfere = this.contaService.getContaById(contaTransfereId);
            Conta contaRecebe = this.contaService.getContaById(contaRecebeId);

            BigDecimal valorTransferencia = transacao.getValor();


            this.contaService.adicionarSaldo(contaRecebe, valorTransferencia);
            this.contaService.removeSaldo(contaTransfere, valorTransferencia);

            atualizarStatusTransacao(transacao.getId(), TransacaoStatus.CONCLUIDO);
            logger.info("Trasferencia realizada com sucesso: valor={}, origem{}, destino={}, status={}",
                    transacao.getValor(),transacao.getContaOrigem(),transacao.getContaDestino(),transacao.getStatus());
        }catch (SaldoInsuficienteException e){
            atualizarStatusTransacao(transacao.getId(), TransacaoStatus.CANCELADO);
            logger.error("Falha na transferência ID {}: Saldo insuficiente. Mensagem: {}", transacaoId, e.getMessage());
        }catch (ContaNotFoundException e){
            atualizarStatusTransacao(transacao.getId(), TransacaoStatus.CANCELADO);
            logger.error("Erro na transferência ID {}: Transação ou conta não encontrada. Mensagem: {}", transacaoId, e.getMessage());
        }catch (Exception e){
            atualizarStatusTransacao(transacao.getId(), TransacaoStatus.CANCELADO);
            logger.error("Erro inesperado ao processar transferência ID {}. Mensagem: {}", transacaoId, e.getMessage(), e);
        }


    }

    @Override
    @Transactional
    public void atualizarStatusTransacao(UUID transacaoId, TransacaoStatus status) {
        Transacao transacao = transacaoRepository.findById(transacaoId).orElseThrow(() -> new ContaNotFoundException("Transação não encontrada."));
        transacao.setStatus(status);
        this.transacaoRepository.save(transacao);

    }
}

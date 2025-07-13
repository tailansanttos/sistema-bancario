package com.tailan.santos.banco.kafka;

import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransacaoKafkaProducer {
    private static final Logger log =  LoggerFactory.getLogger(TransacaoKafkaProducer.class);
    private final KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate;

    public TransacaoKafkaProducer(KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //ENVIA MENSAGEM PARA O KAFKA
    public void sendTransferencia(TransacaoResponseDto transacao) {
        try {
            kafkaTemplate.send("transferencias-v2", transacao.transacaoId().toString(), transacao);
            log.info("Mensagem de transferência enviada para Kafka. Transação ID: {}", transacao.transacaoId());
        }catch (Exception e) {
            log.error("Erro ao enviar mensagem de transferencia para o Kafka. Transação ID: {}", transacao.transacaoId(), e.getMessage(), e);
        }
    }

}

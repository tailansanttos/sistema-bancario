package com.tailan.santos.banco.kafka;

import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import com.tailan.santos.banco.service.transacao.TransacaoService;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransacaoKafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(TransacaoKafkaConsumer.class);
    private final TransacaoService transacaoService;


    //escuta mensagens do Kafka.

    public TransacaoKafkaConsumer(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }


  @KafkaListener(topics ="transferencias-v2", groupId ="transferencias-realizadas")
    public void consumirMensagens(TransacaoResponseDto transacaoDto){
        transacaoService.processarTransferenciaRecebida(transacaoDto.transacaoId());
  }
}

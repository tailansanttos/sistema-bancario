package com.tailan.santos.banco.kafka;

import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerConfig.class);
    private final KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate;

    public KafkaProducerConfig(KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendTransferencia(TransacaoResponseDto transacao) {
        try {
            kafkaTemplate.send("transferencias ", transacao.transacaoId().toString(), transacao);
            log.info("Mensagem de transferência enviada para Kafka. Transação ID: {}", transacao.transacaoId());
        }catch (Exception e) {
            log.error("Erro ao enviar mensagem de transferencia para o Kafka. Transação ID: {}", transacao.transacaoId(), e.getMessage(), e);
        }
    }
    @Bean
    public ProducerFactory<String, TransacaoResponseDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, TransacaoResponseDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

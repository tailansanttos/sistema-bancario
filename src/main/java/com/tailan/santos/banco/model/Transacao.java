package com.tailan.santos.banco.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_transacoes")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    @Enumerated(EnumType.STRING)
    private TransacaoTipo tipo;

    @ManyToOne
    @JoinColumn(name = "conta_origem_id")
    private Conta contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id")
    private Conta contaDestino;

    @Enumerated(EnumType.STRING)
    private TransacaoStatus status;
}

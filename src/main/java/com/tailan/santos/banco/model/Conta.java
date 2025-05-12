package com.tailan.santos.banco.model;

import com.tailan.santos.banco.model.cliente.Cliente;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_contas")
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private BigDecimal saldo;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "contaOrigem")
    private List<Transacao> transacaosOrigem;

    @OneToMany(mappedBy = "contaDestino")
    private List<Transacao> transacaosDestino;

    public Conta(BigDecimal saldo, Cliente cliente, List<Transacao> transacaosOrigem, List<Transacao> transacaosDestino) {
        this.saldo = saldo;
        this.cliente = cliente;
        this.transacaosOrigem = transacaosOrigem;
        this.transacaosDestino = transacaosDestino;
    }
    public Conta() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Transacao> getTransacaosOrigem() {
        return transacaosOrigem;
    }

    public void setTransacaosOrigem(List<Transacao> transacaosOrigem) {
        this.transacaosOrigem = transacaosOrigem;
    }

    public List<Transacao> getTransacaosDestino() {
        return transacaosDestino;
    }

    public void setTransacaosDestino(List<Transacao> transacaosDestino) {
        this.transacaosDestino = transacaosDestino;
    }
}

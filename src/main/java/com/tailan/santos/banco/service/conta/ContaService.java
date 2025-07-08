package com.tailan.santos.banco.service.conta;

import com.tailan.santos.banco.dtos.conta.ContaRequestDto;
import com.tailan.santos.banco.dtos.conta.ContaResponseDto;
import com.tailan.santos.banco.model.Conta;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ContaService {
    public ContaResponseDto cadastraConta(UUID clienteId, ContaRequestDto contaRequestDto);

    public List<ContaResponseDto> listaContasCadastradas();
    public ContaResponseDto pegarContaPorId(UUID contaId);

    public Conta getContaById(UUID contaId);

    public void adicionarSaldo(Conta conta, BigDecimal valor);
    public void removeSaldo(Conta conta, BigDecimal valor);


    public ContaResponseDto entityToDto(Conta conta);
}

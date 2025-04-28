package com.tailan.santos.banco.service;

import com.tailan.santos.banco.dtos.cliente.ClienteResponseDto;
import com.tailan.santos.banco.dtos.conta.ContaRequestDto;
import com.tailan.santos.banco.dtos.conta.ContaResponseDto;
import com.tailan.santos.banco.model.Cliente;
import com.tailan.santos.banco.model.Conta;
import com.tailan.santos.banco.repositories.ClienteRepository;
import com.tailan.santos.banco.repositories.ContaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ContaService {
    private final ContaRepository contaRepository;
    private final ClienteService clienteService;

    public ContaService(ContaRepository contaRepository, ClienteService clienteService) {
        this.contaRepository = contaRepository;
        this.clienteService = clienteService;
    }

    public ContaResponseDto cadastraConta(UUID clienteId, ContaRequestDto contaRequestDto) {
        Cliente cliente = clienteService.getCliente(clienteId);
        if (cliente == null) {
            throw new RuntimeException("Cliente não encontrado");
        }

        Conta conta = new Conta();
        conta.setCliente(cliente);
        conta.setSaldo(contaRequestDto.saldo());
        Conta contaSalva = contaRepository.save(conta);

        ContaResponseDto response = new ContaResponseDto(
                contaSalva.getId(),
                conta.getCliente().getNome(),
                conta.getCliente().getEmail(),
                conta.getCliente().getTelefone(),
                conta.getCliente().getEndereco()
        );
        return response;
    }

    public List<ContaResponseDto> listaContasCadastradas() {
        List<Conta> contas = contaRepository.findAll();
      return contas.stream().map(conta -> new ContaResponseDto(
                conta.getId(),
                conta.getCliente().getNome(),
                conta.getCliente().getEmail(),
                conta.getCliente().getTelefone(),
                conta.getCliente().getEndereco()
        )).toList();
    }

    public Conta pegarContaPorId(UUID contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return conta;
    }

    public void adicionarSaldo(Conta conta, BigDecimal valor) {
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }

    public void removeSaldo(Conta conta, BigDecimal valor) {
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
    }
}

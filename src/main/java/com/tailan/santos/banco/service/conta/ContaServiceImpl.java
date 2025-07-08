package com.tailan.santos.banco.service.conta;

import com.tailan.santos.banco.dtos.conta.ContaRequestDto;
import com.tailan.santos.banco.dtos.conta.ContaResponseDto;
import com.tailan.santos.banco.exception.ContaNotFoundException;
import com.tailan.santos.banco.model.Cliente;
import com.tailan.santos.banco.model.Conta;
import com.tailan.santos.banco.repositories.ContaRepository;
import com.tailan.santos.banco.service.clientes.ClienteService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContaServiceImpl implements ContaService {
    private final ContaRepository contaRepository;
    private final ClienteService clienteService;


    public ContaServiceImpl(ContaRepository contaRepository, ClienteService clienteService) {
        this.contaRepository = contaRepository;
        this.clienteService = clienteService;
    }

    @Override
    public ContaResponseDto cadastraConta(UUID clienteId, ContaRequestDto contaRequestDto) {
        Cliente cliente = clienteService.getClienteById(clienteId);

        if (contaRequestDto.saldo() == null || contaRequestDto.saldo().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Saldo tem que ser positivo, e não pode ser nulo.");
        }
        Conta conta   = new Conta();
        conta.setCliente(cliente);
        conta.setSaldo(contaRequestDto.saldo());
        Conta contaSalva = contaRepository.save(conta);

        return entityToDto(contaSalva);

    }

    @Override
    public List<ContaResponseDto> listaContasCadastradas() {
        List<Conta> contas = contaRepository.findAll();
        return contas.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public ContaResponseDto pegarContaPorId(UUID contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new ContaNotFoundException("Conta inexistente"));
        return entityToDto(conta);
    }


    @Override
    public Conta getContaById(UUID contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new ContaNotFoundException("Conta inexistente"));

        return conta;
    }


    @Override
    public void adicionarSaldo(Conta conta, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor a ser adicionado deve ser positivo.");
        }

        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }

    @Override
    public void removeSaldo(Conta conta, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor a ser adicionado deve ser positivo.");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
    }

    @Override
    public ContaResponseDto entityToDto(Conta conta) {
        return new ContaResponseDto(
                conta.getId(),
                conta.getSaldo(),
                conta.getCliente().getNome(),
                conta.getCliente().getEmail(),
                conta.getCliente().getTelefone(),
                conta.getCliente().getEndereco()
        );
    }
}

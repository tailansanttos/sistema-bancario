package com.tailan.santos.banco.service;

import com.tailan.santos.banco.dtos.ClienteRequestDto;
import com.tailan.santos.banco.dtos.ClienteResponseDto;
import com.tailan.santos.banco.model.Cliente;
import com.tailan.santos.banco.repositories.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponseDto cadastraCliente(ClienteRequestDto cliente) {
        Cliente newCliente = new Cliente();
        newCliente.setNome(cliente.nome());
        newCliente.setEmail(cliente.email());
        newCliente.setCpf(cliente.cpf());
        newCliente.setEndereco(cliente.endereco());
        newCliente.setTelefone(cliente.telefone());


        Cliente clienteSalvo = clienteRepository.save(newCliente);
        ClienteResponseDto response = new ClienteResponseDto(
                clienteSalvo.getNome(),
                clienteSalvo.getEmail(),
                clienteSalvo.getTelefone(),
                clienteSalvo.getEndereco()
        );
        return response;
    }



    public List<ClienteResponseDto> listaClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream().map(cliente -> new ClienteResponseDto(
                cliente.getNome(),cliente.getEmail(),cliente.getTelefone(),cliente.getEndereco()
        )).toList();
    }
}

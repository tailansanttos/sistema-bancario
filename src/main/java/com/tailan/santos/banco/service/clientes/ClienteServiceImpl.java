package com.tailan.santos.banco.service.clientes;

import com.tailan.santos.banco.dtos.cliente.ClienteRequestDto;
import com.tailan.santos.banco.dtos.cliente.ClienteResponseDto;
import com.tailan.santos.banco.dtos.cliente.ClienteUpdateRequestDto;
import com.tailan.santos.banco.exception.ClienteNotFound;
import com.tailan.santos.banco.exception.CustomerAlreadyExistsException;
import com.tailan.santos.banco.model.Cliente;
import com.tailan.santos.banco.repositories.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    public ClienteServiceImpl(ClienteRepository clienteRepository)
    {
        this.clienteRepository = clienteRepository;
    }


    @Override
    public Cliente getClienteById(UUID clienteId){
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(()
                -> new ClienteNotFound("Cliente não cadastrado."));
        return cliente;
    }


    @Override
    @Transactional
    public ClienteResponseDto cadastrarCliente(ClienteRequestDto request) {
        Cliente cliente = new Cliente();


      validarClienteCpf(request.cpf());

      cliente.setCpf(request.cpf());
      cliente.setNome(request.nome());
      cliente.setEmail(request.email());
      cliente.setEndereco(request.endereco());
      cliente.setTelefone(request.telefone());
      Cliente clienteSalvo = clienteRepository.save(cliente);

      return entityToDto(clienteSalvo);
    }

    @Transactional
    @Override
    public ClienteResponseDto atualizarCliente(UUID clienteId, ClienteUpdateRequestDto requestUpdate) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(()
                -> new ClienteNotFound("Cliente não cadastrado."));

        cliente.setNome(requestUpdate.nome());
        cliente.setTelefone(requestUpdate.telefone());
        cliente.setEndereco(requestUpdate.endereco());

        Cliente clienteAtualizado = clienteRepository.save(cliente);


        return entityToDto(clienteAtualizado);
    }


    @Override
    @Transactional
    public void deletarCliente(UUID clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(()
                -> new ClienteNotFound("Cliente não cadastrado."));
        clienteRepository.delete(cliente);
    }


    @Override
    public List<ClienteResponseDto> listarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        //transforma a lista de clientes, numa lista de clienteResponseDto
        return clientes.stream().map
                        (this::entityToDto).
                collect(Collectors.toList());
    }


    @Override
    public ClienteResponseDto buscarClientePorId(UUID clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(()
                -> new ClienteNotFound("Cliente não cadastrado."));
        return entityToDto(cliente);
    }

    @Override
    public ClienteResponseDto entityToDto(Cliente cliente) {
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEndereco()
        );
    }

    private void validarClienteCpf(String cpf){
        Optional<Cliente> cliente = clienteRepository.findByCpf(cpf);
        if (cliente.isPresent()){
            throw new CustomerAlreadyExistsException("Cliente com CPF " + cpf + " já cadastrado.");
        }
    }



}

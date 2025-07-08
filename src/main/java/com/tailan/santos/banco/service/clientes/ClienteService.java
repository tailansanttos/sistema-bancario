package com.tailan.santos.banco.service.clientes;

import com.tailan.santos.banco.dtos.cliente.ClienteRequestDto;
import com.tailan.santos.banco.dtos.cliente.ClienteResponseDto;
import com.tailan.santos.banco.dtos.cliente.ClienteUpdateRequestDto;
import com.tailan.santos.banco.model.Cliente;

import java.util.List;
import java.util.UUID;

public interface ClienteService {

    public ClienteResponseDto cadastrarCliente(ClienteRequestDto request);

    public ClienteResponseDto atualizarCliente(UUID clienteId, ClienteUpdateRequestDto requestUpdate);
    public void deletarCliente(UUID clienteId);
    public List<ClienteResponseDto> listarClientes();
    public ClienteResponseDto buscarClientePorId(UUID clienteId);
    public Cliente getClienteById(UUID clienteId);



    public ClienteResponseDto entityToDto(Cliente cliente);
}

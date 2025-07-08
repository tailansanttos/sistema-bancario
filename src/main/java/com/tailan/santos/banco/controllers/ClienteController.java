package com.tailan.santos.banco.controllers;

import com.tailan.santos.banco.dtos.cliente.ClienteRequestDto;
import com.tailan.santos.banco.dtos.cliente.ClienteResponseDto;
import com.tailan.santos.banco.dtos.cliente.ClienteUpdateRequestDto;
import com.tailan.santos.banco.dtos.response.ApiResponse;
import com.tailan.santos.banco.service.clientes.ClienteServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteServiceImpl clienteServiceImpl;
    public ClienteController(ClienteServiceImpl clienteServiceImpl) {
        this.clienteServiceImpl = clienteServiceImpl;
    }

    @PostMapping("/cliente/add")
    public ResponseEntity<ApiResponse> createCliente(@RequestBody ClienteRequestDto clienteRequestDto) {
        ClienteResponseDto cliente = clienteServiceImpl.cadastrarCliente(clienteRequestDto);
        return ResponseEntity.ok(new ApiResponse("Cliente cadastrado com sucesso", cliente));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> listarClientesCadastrados(){
        List<ClienteResponseDto> clientes = clienteServiceImpl.listarClientes();
        return ResponseEntity.ok(new ApiResponse("Clientes cadastrados: ", clientes));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse> listarClientePorId(@PathVariable UUID clienteId){
        ClienteResponseDto cliente = clienteServiceImpl.buscarClientePorId(clienteId);
        return ResponseEntity.ok(new ApiResponse("Cliente: ", cliente));
    }

    @PutMapping("/cliente/{clienteId}/update")
    public ResponseEntity<ApiResponse> updateCliente(@PathVariable UUID clienteId, @RequestBody ClienteUpdateRequestDto clienteRequestDto){
        ClienteResponseDto cliente = clienteServiceImpl.atualizarCliente(clienteId, clienteRequestDto);
        return ResponseEntity.ok(new ApiResponse("Dados do cliente atualizado com sucesso. ", cliente));
    }

    @DeleteMapping("/clinete{clienteId}")
    public ResponseEntity<ApiResponse> deleteCliente(@PathVariable UUID clienteId){
        clienteServiceImpl.deletarCliente(clienteId);
        return ResponseEntity.ok(new ApiResponse("Cliente deletado com sucesso!", clienteId));
    }


}

package com.tailan.santos.banco.controllers;

import com.tailan.santos.banco.dtos.cliente.ClienteRequestDto;
import com.tailan.santos.banco.dtos.cliente.ClienteResponseDto;
import com.tailan.santos.banco.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDto> cadastroCliente(@RequestBody ClienteRequestDto clienteRequestDto) {
        ClienteResponseDto clienteResponseDto = clienteService.cadastraCliente(clienteRequestDto);
        return new ResponseEntity<>(clienteResponseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listarClientes() {
        List<ClienteResponseDto> lista = clienteService.listaClientes();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

}

package com.tailan.santos.banco.controllers;

import com.tailan.santos.banco.dtos.conta.ContaRequestDto;
import com.tailan.santos.banco.dtos.conta.ContaResponseDto;

import com.tailan.santos.banco.service.ContaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("contas")
public class ContaController {
    private final ContaService contaService;
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/{clienteId}")
    public ResponseEntity<ContaResponseDto> cadastrarContaDoCliente(@PathVariable("clienteId")UUID clienteId, @RequestBody ContaRequestDto contaRequestDto) {
        ContaResponseDto contaResponseDto = contaService.cadastraConta(clienteId, contaRequestDto);
        return new ResponseEntity<>(contaResponseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDto>> listarContasCadastradas() {
        List<ContaResponseDto> lista = contaService.listaContasCadastradas();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

}

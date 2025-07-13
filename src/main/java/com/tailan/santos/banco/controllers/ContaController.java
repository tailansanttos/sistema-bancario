package com.tailan.santos.banco.controllers;

import com.tailan.santos.banco.dtos.conta.ContaRequestDto;
import com.tailan.santos.banco.dtos.conta.ContaResponseDto;

import com.tailan.santos.banco.dtos.response.ApiResponse;
import com.tailan.santos.banco.service.conta.ContaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contas")
public class ContaController {
    private final ContaService contaService;
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/conta/{clienteId}")
    public ResponseEntity<ApiResponse> cadastrarContaDoCliente(@PathVariable("clienteId")UUID clienteId, @RequestBody ContaRequestDto contaRequestDto) {
        ContaResponseDto contaResponseDto = contaService.cadastraConta(clienteId, contaRequestDto);
        return ResponseEntity.ok(new ApiResponse("Conta cadastrada com sucesso", contaResponseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listarContasCadastradas() {
        List<ContaResponseDto> lista = contaService.listaContasCadastradas();
        return ResponseEntity.ok(new ApiResponse("Contas cadastradas: ", lista));
    }

}

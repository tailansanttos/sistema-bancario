package com.tailan.santos.banco.controllers;

import com.tailan.santos.banco.dtos.conta.ContaRequestDto;
import com.tailan.santos.banco.dtos.conta.ContaResponseDto;
import com.tailan.santos.banco.dtos.response.ApiResponse;
import com.tailan.santos.banco.model.Conta;
import com.tailan.santos.banco.service.conta.ContaServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/contas")
public class ContaController {
    private final ContaServiceImpl contaServiceImpl;
    public ContaController(ContaServiceImpl contaServiceImpl) {
        this.contaServiceImpl = contaServiceImpl;
    }

    @PostMapping("/conta/add")
    public ResponseEntity<ApiResponse> cadastrarConta(@RequestParam UUID clienteId, @RequestBody ContaRequestDto contaRequest) {
        ContaResponseDto conta = contaServiceImpl.cadastraConta(clienteId, contaRequest);
        return ResponseEntity.ok(new ApiResponse("Conta adicionado com sucesso!", conta));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> listarContasCadastradas() {
        List<ContaResponseDto> listContas = contaServiceImpl.listaContasCadastradas();
        return ResponseEntity.ok(new ApiResponse("Contas cadastradas!", listContas));
    }

    @GetMapping("/conta{contaId}")
    public ResponseEntity<ApiResponse> listarContaPorId(@PathVariable UUID contaId) {
        ContaResponseDto conta = contaServiceImpl.pegarContaPorId(contaId);
        return ResponseEntity.ok(new ApiResponse("Conta encontrada. ", conta));
    }



}

package com.tailan.santos.banco.controllers;

import com.tailan.santos.banco.dtos.response.ApiResponse;
import com.tailan.santos.banco.dtos.transacao.DepositoESaqueTransacaoDto;
import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import com.tailan.santos.banco.dtos.transacao.TransferenciaDto;
import com.tailan.santos.banco.service.transacao.TransacaoService;
import com.tailan.santos.banco.service.transacao.TransacaoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {
    private final TransacaoService transacaoService;
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping("/transacao/deposito")
    public ResponseEntity<ApiResponse> realizarDeposito(@RequestParam ("contaId") UUID contaId, @RequestBody DepositoESaqueTransacaoDto depositoRequestDto){
        TransacaoResponseDto depositoResponse = transacaoService.realizarDeposito(contaId, depositoRequestDto);
        return ResponseEntity.ok(new ApiResponse("Deposito realizado com sucesso", depositoResponse));
    }

    @PostMapping("/transacao/saque")
    public ResponseEntity<ApiResponse> realizarSaque(@RequestParam ("contaId") UUID contaId, @RequestBody DepositoESaqueTransacaoDto saqueRequestDto){
        TransacaoResponseDto saqueResponse = transacaoService.realizarSaque(contaId, saqueRequestDto);
        return ResponseEntity.ok(new ApiResponse("Saque realizado com sucesso", saqueResponse));
    }

    @PostMapping("/transacao/transferencia")
    public ResponseEntity<ApiResponse> realizarTransferencia(@RequestBody TransferenciaDto transacaoRequest){
        TransacaoResponseDto transferenciaResponse = transacaoService.realizarTransferencia(transacaoRequest) ;
        return ResponseEntity.ok(new ApiResponse("Transferencia realizada com sucesso", transferenciaResponse));
    }

    @PostMapping("/transacao/{contaId}/extrato")
    public ResponseEntity<ApiResponse> pegarExtratoConta(@PathVariable("contaId") UUID contaId){
        List<TransacaoResponseDto> listExtrato = transacaoService.extratoConta(contaId);
        return ResponseEntity.ok(new ApiResponse("Extrato da sua conta:",  listExtrato) );
    }





}


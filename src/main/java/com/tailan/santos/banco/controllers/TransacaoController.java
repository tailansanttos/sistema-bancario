package com.tailan.santos.banco.controllers;

import com.tailan.santos.banco.dtos.transacao.DepositoESaqueTransacaoDto;
import com.tailan.santos.banco.dtos.transacao.TransacaoResponseDto;
import com.tailan.santos.banco.dtos.transacao.TransferenciaDto;
import com.tailan.santos.banco.service.TransacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    private final TransacaoService transacaoService;
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping("/deposito/{contaId}")
    public ResponseEntity<TransacaoResponseDto> depositoConta(@PathVariable("contaId")UUID contaId, @RequestBody DepositoESaqueTransacaoDto deposito){
        TransacaoResponseDto transacao = transacaoService.depositoConta(contaId, deposito);
        return new ResponseEntity<>(transacao, HttpStatus.CREATED  );
    }

    @PostMapping("/saque/{contaId}")
    public ResponseEntity<TransacaoResponseDto> saqueConta(@PathVariable("contaId")UUID contaId, @RequestBody DepositoESaqueTransacaoDto saque){
        TransacaoResponseDto transacao = transacaoService.saqueConta(contaId, saque);
        return new ResponseEntity<>(transacao, HttpStatus.CREATED  );
    }

    @PostMapping("/transferencia")
    public ResponseEntity<TransacaoResponseDto> transferenciaConta(@RequestBody TransferenciaDto transferenciaDto){
        TransacaoResponseDto transferencia = transacaoService.transferenciaContas(transferenciaDto);
        return new ResponseEntity<>(transferencia, HttpStatus.CREATED  );
    }

    @GetMapping("/extrato/{contaId}")
    public ResponseEntity<List<TransacaoResponseDto>> extratoConta(@PathVariable("contaId")UUID contaId){
        List<TransacaoResponseDto> transacoes = transacaoService.extratoConta(contaId);
        return new ResponseEntity<>(transacoes, HttpStatus.CREATED  );
    }

}


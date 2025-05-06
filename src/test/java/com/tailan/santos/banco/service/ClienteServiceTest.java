package com.tailan.santos.banco.service;

import com.tailan.santos.banco.dtos.cliente.ClienteRequestDto;
import com.tailan.santos.banco.model.Cliente;
import com.tailan.santos.banco.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock the repository save method
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            // Set a mock ID for the saved client
            return cliente;
        });
    }

    @Test
    void testCadastroClienteComCpfValido() {
        // CPF válido: 529.982.247-25
        ClienteRequestDto requestDto = new ClienteRequestDto(
                "Nome Teste",
                "529.982.247-25",
                "email@teste.com",
                "123456789",
                "Endereço Teste"
        );

        // Não deve lançar exceção
        assertDoesNotThrow(() -> clienteService.cadastraCliente(requestDto));
    }

    @Test
    void testCadastroClienteComCpfInvalido() {
        // CPF inválido: 111.111.111-11 (todos os dígitos iguais)
        ClienteRequestDto requestDto = new ClienteRequestDto(
                "Nome Teste",
                "111.111.111-11",
                "email@teste.com",
                "123456789",
                "Endereço Teste"
        );

        // Deve lançar CpfInvalidoException
        assertThrows(CpfInvalidoException.class, () -> clienteService.cadastraCliente(requestDto));
    }

    @Test
    void testCadastroClienteComCpfInvalidoDigitosVerificadores() {
        // CPF com dígitos verificadores inválidos: 529.982.247-00
        ClienteRequestDto requestDto = new ClienteRequestDto(
                "Nome Teste",
                "529.982.247-00",
                "email@teste.com",
                "123456789",
                "Endereço Teste"
        );

        // Deve lançar CpfInvalidoException
        assertThrows(CpfInvalidoException.class, () -> clienteService.cadastraCliente(requestDto));
    }
}
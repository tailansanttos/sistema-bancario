# Sistema Bancário API

Esta é uma API de um sistema bancário desenvolvido com Spring Boot e Kafka, utilizando Docker para containerização. O sistema permite realizar operações bancárias como depósito, saque, transferência, e consulta de extrato, além de registrar transferencias de forma assíncrona via Kafka.

## Sumário
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Endpoints](#endpoints)
  - [Cadastro de Cliente](#cadastro-de-cliente)
  - [Listar Clientes](#listar-clientes)
  - [Listar Contas](#listar-contas)
  - [Criação de Contas dos clientes](#criação-de-contas)
  - [Deposito](#deposito-conta)
  - [Saque](#saque-conta)
  - [Transferência](#transferencia-conta)
  - [Extrato](#extrato-da-conta)
 
 ## Funcionalidades
- **Cadastro de Cliente**: Permite cadastrar um novo cliente.
- **Listar Clientes**: Lista todos clientes já cadastrado.
- **Criação de Contas dos clientes**: Permite criar uma conta passando o ID do cliente.
- **Listar Contas**: Lista todas contas já criadas.
- **Depósito**: Permite que um cliente deposite um valor na sua conta.
- **Saque**: Permite que um cliente saque um valor da sua conta.
- **Transferência**: Realiza transferências entre contas bancárias, com validação de saldo e contas de origem/destino. A transferência de valores é processada de forma assíncrona, usando Kafka para gerenciar o fluxo de dados.
- **Extrato**: Permite que o cliente pegue o extrato com todas transações da sua conta. 

## Tecnologias Utilizadas
- **Java**
- **Spring Boot**: Framework parra desenvolvimento da API.
- **Kafka**: Sistema de mensageria assíncrona para processamento de transferências bancárias.
- **PostgreSQL**: Banco de dados usado para persistência dos dados.
- **Maven** (para gerenciamento de dependências)
- **Docker**: Utilizado para containerizar a aplicação e facilitar o deploy.
- **Spring Security**:  Para controle de autenticação e autorização (implementação futura).

## Pré-requisitos
- Java 17+ 
- Maven
- Banco de dados configurado (ex. MySQL com schema criado)
- Chave de API para serviços externos (se necessário, ex. Alpha Vantage, Yahoo Finance)

## Instalação
1. Clone o repositório:
   ```bash
   git clone https://github.com/tailansanttos/agregador-de-investimentos.git

  ## Endpoints

### Cadastro de Cliente
Cria um novo cliente no sistema.

- **URL**: `/clientes`
- **Método**: `POST`
- **Body**:
  ```json
  {
   "nome": "Tailan teste",
   "cpf": "934.567.890-12",
   "email": "teste@example.com",
   "telefone": "(71) 99876-5432",
   "endereco": "São Marcos"
  }

### Listar clientes
Retorna os clientes cadastrados.

- **URL**: `/clientes`
- **Método**: `GET`
- **Body**:
  ```json
  {
    "clienteId": "7538d303-ba10-4c76-9dbf-71d03ca35be8",
		"nome": "Tailan teste",
    "cpf": "934.567.890-12",
    "email": "teste@example.com",
    "telefone": "(71) 99876-5432",
    "endereco": "São Marcos"
  }

### Criação de Contas dos clientes
Cria conta passando Id do cliente.

- **URL**: `/contas/{clienteId}`
- **Método**: `POST`
- **Body**:
  ```json
  {
	"saldo": 1500.00
  }


### Listar Contas
Retorna todas contas já criadas.

- **URL**: `/contas`
- **Método**: `GET`
- **Body**:
  ```json
  [
	{
		"contaId": "d6ef36df-8cea-4432-92cf-0477fdc6f49d",
		"saldoConta": 1500.00,
		"nomeCliente": "Tailan teste",
		"emailCliente": "teste@example.com",
		"telefoneCliente": "(71) 99876-5432",
		"enderecoCliente": "São Marcos"
	}
  ]
  
### Depósito
Deposito um valor na conta.

- **URL**: `/transacoes/{contaId}/deposito`
- **Método**: `POST`
- **Body**:
  ```json
  [
	{
		"valor": 500.00
	}
  ]

### Saque
Sacar um valor na conta.

- **URL**: `/transacoes/{contaId}/saque`
- **Método**: `POST`
- **Body**:
  ```json
  [
	{
		"valor": 500.00
	}
  ]

### Transferência
Transfere dinheiro para outra conta.

- **URL**: `/transacoes/transferencia`
- **Método**: `POST`
- **Body**:
  ```json
  [
	{
		"contaTransfereId": "d6ef36df-8cea-4432-92cf-0477fdc6f49d",
    "contaRecebeId": "288dc70a-f3a2-49f0-842d-d8f8e2627e90",
    "valor": 120.00
	}
  ]

### Extrato
Pega o extrato da conta

- **URL**: `/transacoes/{contaId}/extrato`
- **Método**: `GET`
- **Body**:
  ```json
  [
	{
	 "valor": 100,
		"dataHora": "2025-04-29T20:26:58.090252",
		"tipo": "SAQUE",
		"contaOrigem": "392ec41e-83e6-44cc-9e1a-aebe68d80fa6",
		"contaDestino": null,
		"status": "CONCLUIDO"
	}

  ]



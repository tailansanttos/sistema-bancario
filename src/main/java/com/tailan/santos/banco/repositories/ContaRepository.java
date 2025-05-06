package com.tailan.santos.banco.repositories;

import com.tailan.santos.banco.model.Cliente;
import com.tailan.santos.banco.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContaRepository  extends JpaRepository<Conta, UUID> {
}

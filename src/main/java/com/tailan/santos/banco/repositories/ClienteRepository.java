package com.tailan.santos.banco.repositories;

import com.tailan.santos.banco.model.Cliente;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Registered
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findById(UUID clienteId);


}

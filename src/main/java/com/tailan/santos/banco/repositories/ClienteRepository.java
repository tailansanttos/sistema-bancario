package com.tailan.santos.banco.repositories;

import com.tailan.santos.banco.model.cliente.Cliente;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

@Registered
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Cliente findByEmail(String email);
    Cliente findByCpf(String cpf);
}

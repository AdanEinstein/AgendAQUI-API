package com.agendaqui.AgendAQUI.repository;

import com.agendaqui.AgendAQUI.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    public Optional<Cliente> getByLogin_Id(Long id);
}

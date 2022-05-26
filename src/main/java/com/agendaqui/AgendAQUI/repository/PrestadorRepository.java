package com.agendaqui.AgendAQUI.repository;

import com.agendaqui.AgendAQUI.model.Cliente;
import com.agendaqui.AgendAQUI.model.PrestadorServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrestadorRepository extends JpaRepository<PrestadorServico, Long> {
    public Optional<PrestadorServico> getByLogin_Id(Long id);
}

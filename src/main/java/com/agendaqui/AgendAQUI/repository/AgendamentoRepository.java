package com.agendaqui.AgendAQUI.repository;

import com.agendaqui.AgendAQUI.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
}

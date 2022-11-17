package com.agendaqui.AgendAQUI.repository;

import com.agendaqui.AgendAQUI.model.Agendamento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AgendamentoRepository extends PagingAndSortingRepository<Agendamento, Long> {
    public List<Agendamento> findAllByDataEHoraContainsAndPrestador_Id(String data, Long id, Pageable pageable);
    public List<Agendamento> findAllByDataEHoraContainsAndCliente_Id(String data, Long id, Pageable pageable);
}

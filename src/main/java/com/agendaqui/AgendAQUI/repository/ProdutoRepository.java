package com.agendaqui.AgendAQUI.repository;

import com.agendaqui.AgendAQUI.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Query("SELECT p FROM Produto p WHERE p.prestador.id = :idPrestador")
    Optional<List<Produto>> listarProdutos(@Param("idPrestador") Long id);
}

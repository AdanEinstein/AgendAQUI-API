package com.agendaqui.AgendAQUI.repository;

import com.agendaqui.AgendAQUI.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    public Optional<Login> findByLogin(String login);
    public boolean existsLoginByLoginAndId(String login, Long id);
}

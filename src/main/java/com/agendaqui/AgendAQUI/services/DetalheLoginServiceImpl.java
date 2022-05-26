package com.agendaqui.AgendAQUI.services;

import com.agendaqui.AgendAQUI.data.DetalheLoginData;
import com.agendaqui.AgendAQUI.model.Login;
import com.agendaqui.AgendAQUI.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DetalheLoginServiceImpl implements UserDetailsService {

    @Autowired
    private final LoginRepository loginRepository;

    public DetalheLoginServiceImpl(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Login> usuario = loginRepository.findByLogin(username);
        if (usuario.isEmpty()){
            throw new UsernameNotFoundException("Usuário [" + username + "] não encontrado");
        }
        return new DetalheLoginData(usuario);
    }
}

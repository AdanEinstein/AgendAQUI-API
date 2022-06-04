package com.agendaqui.AgendAQUI.controller;

import com.agendaqui.AgendAQUI.model.Cliente;
import com.agendaqui.AgendAQUI.model.Login;
import com.agendaqui.AgendAQUI.model.PrestadorServico;
import com.agendaqui.AgendAQUI.repository.ClienteRepository;
import com.agendaqui.AgendAQUI.repository.LoginRepository;
import com.agendaqui.AgendAQUI.repository.PrestadorRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginRepository loginRepository;
    private final PasswordEncoder encoder;
    private final ClienteRepository clienteRepository;
    private final PrestadorRepository prestadorRepository;

    public LoginController(LoginRepository loginRepository, PasswordEncoder encoder, ClienteRepository clienteRepository, PrestadorRepository prestadorRepository) {
        this.loginRepository = loginRepository;
        this.encoder = encoder;
        this.clienteRepository = clienteRepository;
        this.prestadorRepository = prestadorRepository;
    }

    @GetMapping("/listarTodos") // Rota que retorna todos os logins cadastrados no banco
    public ResponseEntity<List<Login>> listarTodos() {
        try {
            return ResponseEntity.ok(loginRepository.findAll());
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/token") // Rota verifica se o token no header da requisição é válida ou não
    public ResponseEntity<Boolean> validarToken() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/validarSenha") // Rota responsável por verificar se o login e a senha correspondem a do usuário
    public ResponseEntity<Boolean> validarSenha(@RequestParam String login,
                                                @RequestParam String senha) {
        try {
            Optional<Login> optLogin = loginRepository.findByLogin(login);
            if (optLogin.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
            boolean valid = false;
            Login usuario = optLogin.get();
            valid = encoder.matches(senha, usuario.getPassword());

            HttpStatus status = valid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
            return ResponseEntity.status(status).body(valid);
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/loginID")
    // Rota responsável por retornar as informações necessárias do usuário
    // Caso seja cliente -> Retorna Cliente
    // Caso seja Prestador || Cliente && Prestador -> Retorna Prestador
    // Caso não seja nenhum -> Retorna um {} (objeto vazio)
    public ResponseEntity<Object> getLoginDetails(@RequestParam String login, @RequestParam String senha) {
        try {
            Optional<Login> optUsuario = loginRepository.findByLogin(login);
            if (optUsuario.isEmpty()) {
                return ResponseEntity.badRequest().body("Login inválido");
            }
            boolean valid = false;
            Login usuario = optUsuario.get();
            valid = encoder.matches(senha, usuario.getPassword());
            if (valid) {
                Optional<Cliente> cliente = clienteRepository.getByLogin_Id(usuario.getId());
                Optional<PrestadorServico> prestador = prestadorRepository.getByLogin_Id(usuario.getId());
                if (cliente.isEmpty() && prestador.isEmpty()) {
                    return ResponseEntity.ok().body(new Object());
                } else if (cliente.isEmpty()) {
                    return ResponseEntity.ok(prestador.get());
                } else if (prestador.isEmpty()) {
                    return ResponseEntity.ok(cliente.get());
                } else {
                    return ResponseEntity.ok(prestador.get());
                }
            } else {
                return ResponseEntity.badRequest().body("Senha inválida");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error de requisição: " + e.getMessage());
        }
    }

    @PostMapping("/salvar") // Rota para cadastrar um novo login na base de dados
    public ResponseEntity<Object> salvar(@RequestBody Login login) {
        try {
            Optional<Login> loginRequisitado = loginRepository.findByLogin(login.getLogin());
            if (loginRequisitado.isPresent()) {
                return ResponseEntity.badRequest().body("Login já cadastrado!");
            }
            login.setPassword(encoder.encode(login.getPassword()));
            return ResponseEntity.ok(loginRepository.save(login));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de requisição: " + e.getMessage());
        }
    }

    @PutMapping("/alterar")
    // Rota para alterar um login
    //{
    //    "id": <id>,
    //    "login": "<login>",
    //    "oldpassword": "<senha antiga>",
    //    "password": "<nova senha>"
    //}
    public ResponseEntity<Object> alterarLogin(@RequestBody ObjectNode json) {
        try {
            Long idJson = json.get("id").asLong();
            String loginJson = json.get("login").asText();
            String oldPasswordJson = json.get("oldpassword").asText();
            String passwordJson = json.get("password").asText();
            Optional<Login> loginAlterado = loginRepository.findById(idJson);
            if (loginAlterado.isEmpty()) {
                return ResponseEntity.badRequest().body("Alteração inválida");
            } else {
                boolean validacaoSenha = encoder.matches(oldPasswordJson, loginAlterado.get().getPassword());
                if (validacaoSenha) {
                    Login login = new Login(idJson, loginJson, encoder.encode(passwordJson));
                    return ResponseEntity.ok().body(loginRepository.save(login));
                } else {
                    return ResponseEntity.ok().body("Senhas não correspondentes!");
                }
            }
        } catch (DataAccessException sql){
            return ResponseEntity.badRequest().body("Login pertence a outro usuário!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de requisição: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletar") // Rota para deletar um login pelo id informado como parâmetro
    public ResponseEntity<Object> deletarLogin(@RequestParam Long loginid) {
        try {
            Optional<Login> loginDeletado = loginRepository.findById(loginid);
            if (loginDeletado.isEmpty()) {
                return ResponseEntity.badRequest().body("Login não existe!");
            }
            loginRepository.deleteById(loginid);
            return ResponseEntity.ok().body(loginDeletado.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de requisição: " + e.getMessage());
        }
    }

}

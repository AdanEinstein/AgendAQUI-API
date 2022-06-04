package com.agendaqui.AgendAQUI.controller;

import com.agendaqui.AgendAQUI.model.Cliente;
import com.agendaqui.AgendAQUI.model.Login;
import com.agendaqui.AgendAQUI.repository.ClienteRepository;
import com.agendaqui.AgendAQUI.repository.LoginRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {
    private final ClienteRepository clienteRepository;
    private final LoginRepository loginRepository;

    public ClienteController(ClienteRepository clienteRepository, LoginRepository loginRepository) {
        this.clienteRepository = clienteRepository;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/listarTodos") // Rota que retorna todos os clientes do banco de dados
    public ResponseEntity<List<Cliente>> listarClientes() {
        try {
            return ResponseEntity.ok(clienteRepository.findAll());
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarClientePorId(@PathVariable("id") Long id) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente.isPresent()) {
                return ResponseEntity.ok().body(cliente.get());
            } else {
                return ResponseEntity.badRequest().body("Cliente não encontrado");
            }
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @PostMapping("/salvar")
    // Rota responsável por cadastrar um novo cliente para no banco
    //{
    //    "nome": "<nome>",
    //    "cpf": "<cpf>",
    //    "telefone": "<telefone>",
    //    "dataNascimento": "<data nascimento ( dd/MM/yyyy )>",
    //    "loginid": <id>
    //}
    public ResponseEntity<Object> salvar(@RequestBody ObjectNode json) {
        try {
            String nomeJson = json.get("nome").asText();
            String cpfJson = json.get("cpf").asText();
            String telefoneJson = json.get("telefone").asText();
            String dataNascimentoJson = json.get("dataNascimento").asText();
            Long loginidJson = json.get("loginid").asLong();
            Optional<Login> login = loginRepository.findById(loginidJson);
            if (login.isEmpty()) {
                return ResponseEntity.badRequest().body("Login id não existe");
            } else {
                Optional<Cliente> cliente = clienteRepository.getByLogin_Id(loginidJson);
                if (cliente.isPresent()) {
                    return ResponseEntity.badRequest().body("Login pertence a outro usuário");
                } else {
                    Cliente clienteNovo = new Cliente();
                    clienteNovo.setNome(nomeJson);
                    clienteNovo.setCpf(cpfJson);
                    clienteNovo.setTelefone(telefoneJson);
                    clienteNovo.setDataNascimento(dataNascimentoJson);
                    clienteNovo.setLogin(login.get());
                    return ResponseEntity.ok(clienteRepository.save(clienteNovo));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }

    @PutMapping("/alterar")
    // Rota responsável por alterar um cliente já cadastrado no banco
    //{
    //    "id": <id>
    //    "nome": "<nome>",
    //    "cpf": "<cpf>",
    //    "telefone": "<telefone>",
    //    "dataNascimento": "<data nascimento ( dd/MM/yyyy )>",
    //}
    public ResponseEntity<Object> alterarCliente(@RequestBody ObjectNode json) {
        try {
            Long idJson = json.get("id").asLong();
            String nomeJson = json.get("nome").asText();
            String cpfJson = json.get("cpf").asText();
            String telefoneJson = json.get("telefone").asText();
            String dataNascimentoJson = json.get("dataNascimento").asText();
            Optional<Cliente> clienteRequisitado = clienteRepository.findById(idJson);
            if (clienteRequisitado.isEmpty()) {
                return ResponseEntity.badRequest().body("Cliente não está cadastrado!");
            } else {
                Cliente clienteAlterado = clienteRequisitado.get();
                clienteAlterado.setNome(nomeJson);
                clienteAlterado.setCpf(cpfJson);
                clienteAlterado.setTelefone(telefoneJson);
                clienteAlterado.setDataNascimento(dataNascimentoJson);
                return ResponseEntity.ok().body(clienteRepository.save(clienteAlterado));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }

    @DeleteMapping("/deletar") // Rota para deletar um cliente pelo id informado como parâmetro
    public ResponseEntity<Object> deletarCliente(@RequestParam Long id) {
        try {
            Optional<Cliente> clienteDeletado = clienteRepository.findById(id);
            if (clienteDeletado.isEmpty()) {
                return ResponseEntity.badRequest().body("Cliente não existe!");
            }
            clienteRepository.deleteById(id);
            return ResponseEntity.ok().body(clienteDeletado.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }
}

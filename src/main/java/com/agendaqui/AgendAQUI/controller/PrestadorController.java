package com.agendaqui.AgendAQUI.controller;

import com.agendaqui.AgendAQUI.model.Cliente;
import com.agendaqui.AgendAQUI.model.Login;
import com.agendaqui.AgendAQUI.model.PrestadorServico;
import com.agendaqui.AgendAQUI.repository.LoginRepository;
import com.agendaqui.AgendAQUI.repository.PrestadorRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prestador")
public class PrestadorController {
    private final PrestadorRepository prestadorRepository;
    private final LoginRepository loginRepository;

    public PrestadorController(PrestadorRepository prestadorRepository, LoginRepository loginRepository) {
        this.prestadorRepository = prestadorRepository;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/listarTodos") // Rota que retorna todos os prestadores de serviços cadastrados na base de dados
    public ResponseEntity<List<PrestadorServico>> listarPrestadores() {
        try {
            return ResponseEntity.ok(prestadorRepository.findAll());
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/{id}") // Rota busca um prestador por id
    public ResponseEntity<Object> buscarPrestadorPorId(@PathVariable("id") Long id) {
        try {
            Optional<PrestadorServico> prestadorServico = prestadorRepository.findById(id);
            if (prestadorServico.isPresent()) {
                return ResponseEntity.ok().body(prestadorServico.get());
            } else {
                return ResponseEntity.badRequest().body("Prestador não encontrado");
            }
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @PostMapping("/salvar")
    // Rota que cadastra um novo prestador de serviço
    //{"
    //    "nome": "<nome>",
    //    "cpfj": "<CPF / CNPJ>",
    //    "telefone": "<telefone>",
    //    "descricao": "<descrição>",
    //    "email": "<e-mail>",
    //    "paginaFacebook": "<Página Facebook>",
    //    "loginid": <id>
    //}"
    public ResponseEntity<Object> salvar(@RequestBody ObjectNode json) {
        try {
            String nomeJson = json.get("nome").asText();
            String cpfjJson = json.get("cpfj").asText();
            String telefoneJson = json.get("telefone").asText();
            String descricaoJson = json.get("descricao").asText();
            String emailJson = json.get("email").asText();
            String paginaFacebookJson = json.get("paginaFacebook").asText();
            Long loginidJson = json.get("loginid").asLong();
            Optional<Login> login = loginRepository.findById(loginidJson);
            if (login.isEmpty()) {
                return ResponseEntity.badRequest().body("Login id não existe");
            } else {
                Optional<PrestadorServico> prestador = prestadorRepository.getByLogin_Id(loginidJson);
                if (prestador.isPresent()) {
                    return ResponseEntity.badRequest().body("Login pertence a outro usuário");
                } else {
                    PrestadorServico prestadorServico = new PrestadorServico();
                    prestadorServico.setNome(nomeJson);
                    prestadorServico.setCpfj(cpfjJson);
                    prestadorServico.setTelefone(telefoneJson);
                    prestadorServico.setDescricao(descricaoJson);
                    prestadorServico.setEmail(emailJson);
                    prestadorServico.setPaginaFacebook(paginaFacebookJson);
                    prestadorServico.setLogin(login.get());
                    return ResponseEntity.ok(prestadorRepository.save(prestadorServico));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }

    @PutMapping("/alterar")
    // Rota responsável por alterar um prestador já cadastrado no banco
    //{
    //    "id": <id>
    //    "nome": "<nome>",
    //    "cpfj": "<CPF / CNPJ>",
    //    "telefone": "<telefone>",
    //    "descricao": "<descrição>",
    //    "email": "<e-mail>",
    //    "paginaFacebook": "<Página Facebook>",
    //}"
    public ResponseEntity<Object> alterarPrestador(@RequestBody ObjectNode json) {
        try {
            Long idJson = json.get("id").asLong();
            String nomeJson = json.get("nome").asText();
            String cpfjJson = json.get("cpfj").asText();
            String telefoneJson = json.get("telefone").asText();
            String descricaoJson = json.get("descricao").asText();
            String emailJson = json.get("email").asText();
            String paginaFacebookJson = json.get("paginaFacebook").asText();
            Optional<PrestadorServico> prestadorRequisitado = prestadorRepository.findById(idJson);
            if (prestadorRequisitado.isEmpty()) {
                return ResponseEntity.badRequest().body("Prestador de serviço não está cadastrado!");
            } else {
                PrestadorServico prestadorServico = prestadorRequisitado.get();
                prestadorServico.setNome(nomeJson);
                prestadorServico.setCpfj(cpfjJson);
                prestadorServico.setTelefone(telefoneJson);
                prestadorServico.setDescricao(descricaoJson);
                prestadorServico.setEmail(emailJson);
                prestadorServico.setPaginaFacebook(paginaFacebookJson);
                return ResponseEntity.ok(prestadorRepository.save(prestadorServico));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }

    @DeleteMapping("/deletar") // Rota para deletar um prestador pelo id informado como parâmetro
    public ResponseEntity<Object> deletarPrestador(@RequestParam Long prestadorid) {
        try {
            Optional<PrestadorServico> prestadorDeletado = prestadorRepository.findById(prestadorid);
            if (prestadorDeletado.isEmpty()) {
                return ResponseEntity.badRequest().body("Prestador de serviço não existe!");
            }
            prestadorRepository.deleteById(prestadorid);
            return ResponseEntity.ok().body(prestadorDeletado.get());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }
}

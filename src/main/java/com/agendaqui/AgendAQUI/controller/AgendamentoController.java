package com.agendaqui.AgendAQUI.controller;

import com.agendaqui.AgendAQUI.model.Agendamento;
import com.agendaqui.AgendAQUI.model.Cliente;
import com.agendaqui.AgendAQUI.model.PrestadorServico;
import com.agendaqui.AgendAQUI.repository.AgendamentoRepository;
import com.agendaqui.AgendAQUI.repository.ClienteRepository;
import com.agendaqui.AgendAQUI.repository.PrestadorRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamento")
public class AgendamentoController {
    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final PrestadorRepository prestadorRepository;

    public AgendamentoController(AgendamentoRepository agendamentoRepository, ClienteRepository clienteRepository, PrestadorRepository prestadorRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.prestadorRepository = prestadorRepository;
    }

    @GetMapping("/listarTodos") // Rota que lista todos os agendamentos do banco paginados de 10 em 10
    public ResponseEntity<List<Agendamento>> listarAgendamentos(@RequestParam Integer page) {
        try {
            return ResponseEntity.ok(agendamentoRepository.findAll(Pageable.ofSize(10).withPage(page)).getContent());
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/listarTodosPorPrestador") // Rota que lista todos os agendamentos de um mesmo prestador
    // {
    //      "id": <id>,
    //      "data": "dd/MM/yyyy",
    //      "page": <page>,
    // }
    public ResponseEntity<List<Agendamento>> listarAgendamentosPrestador(@RequestBody ObjectNode json) {
        try {
            Long id = json.get("id").asLong();
            String data = json.get("data").asText();
            Integer page = json.get("page").asInt();
            return ResponseEntity.ok(agendamentoRepository.findAllByDataEHoraContainsAndPrestador_Id(data, id, Pageable.ofSize(10).withPage(page)));
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }
    @GetMapping("/listarTodosPorCliente") // Rota que lista todos os agendamentos de um mesmo cliente
    // {
    //      "id": <id>,
    //      "data": "dd/MM/yyyy",
    //      "page": <page>,
    // }
    public ResponseEntity<List<Agendamento>> listarAgendamentosCliente(@RequestBody ObjectNode json) {
        try {
            Long id = json.get("id").asLong();
            String data = json.get("data").asText();
            Integer page = json.get("page").asInt();
            return ResponseEntity.ok(agendamentoRepository.findAllByDataEHoraContainsAndCliente_Id(data, id, Pageable.ofSize(10).withPage(page)));
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }
    @GetMapping("/{id}") // Rota que busca um agendamento por id
    public ResponseEntity<Object> buscarPorId(@PathVariable("id") Long id){
        try{
            Optional<Agendamento> agendamentoOptional = agendamentoRepository.findById(id);
            if(agendamentoOptional.isPresent()){
                return ResponseEntity.ok().body(agendamentoOptional.get());
            } else {
                return ResponseEntity.badRequest().body("Agendamento não encontrado!");
            }
        }catch (Exception e){
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @PostMapping("/salvar")
    // Rota responsável por cadastrar um novo agendamento para no banco
    //{
    //    "clienteid": <cliente id>,
    //    "prestadorid": <prestador id>,
    //    "dataehora":<data e hora (dd/MM/yyy HH:mm:ss)>"
    //    "produtosagendados": {
    //          "descricao": ""
    //          "preco": <Valor>
    //     }
    //}
    public ResponseEntity<Object> salvar(@RequestBody ObjectNode json) {
        try {
            Long clienteIdJson = json.get("clienteid").asLong();
            Long prestadorIdJson = json.get("prestadorid").asLong();
            String produtosJson = json.get("produtosagendados").toString();
            String dataEHoraJson = json.has("dataehora") ? json.get("dataehora").asText() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Optional<Cliente> clienteOptional = clienteRepository.findById(clienteIdJson);
            Optional<PrestadorServico> prestadorOptional = prestadorRepository.findById(prestadorIdJson);
            if (clienteOptional.isPresent() && prestadorOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();
                PrestadorServico prestadorServico = prestadorOptional.get();
                Agendamento agendamento = new Agendamento();
                agendamento.setCliente(cliente);
                agendamento.setPrestador(prestadorServico);
                agendamento.setDataEHora(dataEHoraJson);
                agendamento.setProdutosAgendados(produtosJson);
                agendamento.setStatus(0);
                return ResponseEntity.ok(agendamentoRepository.save(agendamento));
            } else {
                return clienteOptional.isPresent() ? ResponseEntity.badRequest().body("Prestador não encontrado!")
                        : prestadorOptional.isPresent() ? ResponseEntity.badRequest().body("Cliente não encontrado!")
                        : ResponseEntity.badRequest().body("Cliente e Prestador não encontrados!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de requisição: " + e.getMessage());
        }
    }

    @PatchMapping("alterar/{id}")
    // Rota que altera o agendamento dependendo do body da requisição
    public ResponseEntity<Object> alterarAgendamento(@PathVariable("id") Long id, @RequestBody ObjectNode json) {
        try {
            Optional<Agendamento> agendamentoOptional = agendamentoRepository.findById(id);
            if (agendamentoOptional.isPresent()) {
                Agendamento agendamento = agendamentoOptional.get();
                Integer statusJson = json.has("status") ? json.get("status").asInt() : agendamento.getStatus();
                String dataEHoraJson = json.has("dataehora") ? json.get("dataehora").asText() : agendamento.getDataEHora();
                String produtosJson = json.has("produtosagendados") ? json.get("produtosagendados").toString() : agendamento.getProdutosAgendados();
                agendamento.setStatus(statusJson);
                agendamento.setDataEHora(dataEHoraJson);
                agendamento.setProdutosAgendados(produtosJson);
                return ResponseEntity.ok().body(agendamentoRepository.save(agendamento));
            } else {
                return ResponseEntity.badRequest().body("Agendamento não encontrado!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de requisição: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletar") // Rota que deleta um agendamento pelo id
    public ResponseEntity<Object> deletar(@RequestParam Long id) {
        try {
            Optional<Agendamento> agendamentoOptional = agendamentoRepository.findById(id);
            if (agendamentoOptional.isPresent()) {
                Agendamento agendamentoDeletado = agendamentoOptional.get();
                agendamentoRepository.deleteById(id);
                return ResponseEntity.ok().body(agendamentoDeletado);
            } else {
                return ResponseEntity.badRequest().body("Agendamento não encontrado!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de requisição: " + e.getMessage());
        }
    }
}

package com.agendaqui.AgendAQUI.controller;

import com.agendaqui.AgendAQUI.model.Agendamento;
import com.agendaqui.AgendAQUI.model.Cliente;
import com.agendaqui.AgendAQUI.model.PrestadorServico;
import com.agendaqui.AgendAQUI.repository.AgendamentoRepository;
import com.agendaqui.AgendAQUI.repository.ClienteRepository;
import com.agendaqui.AgendAQUI.repository.PrestadorRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/agendamento")
public class AgendamentoController {
    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final PrestadorRepository prestadorRepository;

    public AgendamentoController(AgendamentoRepository agendamentoRepository, ClienteRepository clienteRepository, PrestadorRepository prestadorRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.prestadorRepository = prestadorRepository;
    }

    @GetMapping("/listarTodos")
    public ResponseEntity<List<Agendamento>> listarClientes() {
        try {
            return ResponseEntity.ok(agendamentoRepository.findAll());
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }
    @GetMapping("/{id}")
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
    //    "dataehora: "<data e hora (dd/MM/yyy HH:mm:ss)>"
    //}
    public ResponseEntity<Object> salvar(@RequestBody ObjectNode json) {
        try {
            Long clienteIdJson = json.get("clienteid").asLong();
            Long prestadorIdJson = json.get("prestadorid").asLong();
            Timestamp dataEHoraJson = json.has("dataehora") ? Timestamp.valueOf(json.get("dataehora").asText()) : Timestamp.from(Instant.now());
            Optional<Cliente> clienteOptional = clienteRepository.findById(clienteIdJson);
            Optional<PrestadorServico> prestadorOptional = prestadorRepository.findById(prestadorIdJson);
            if (clienteOptional.isPresent() && prestadorOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();
                PrestadorServico prestadorServico = prestadorOptional.get();
                Agendamento agendamento = new Agendamento();
                agendamento.setCliente(cliente);
                agendamento.setPrestador(prestadorServico);
                agendamento.setDataEHora(dataEHoraJson);
                agendamento.setAprovado(false);
                agendamento.setEstrelas(0);
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
                Boolean aprovadoJson = json.has("aprovado") ? json.get("aprovado").asBoolean() : agendamento.getAprovado();
                Integer estrelasJson = json.has("estrelas") ? json.get("estrelas").asInt() : agendamento.getEstrelas();
                Timestamp dataEHoraJson = json.has("dataehora") ? Timestamp.valueOf(json.get("dataehora").asText()) : agendamento.getDataEHora();
                agendamento.setAprovado(aprovadoJson);
                agendamento.setEstrelas(estrelasJson);
                agendamento.setDataEHora(dataEHoraJson);
                return ResponseEntity.ok().body(agendamentoRepository.save(agendamento));
            } else {
                return ResponseEntity.badRequest().body("Agendamento não encontrado!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de requisição: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletar")
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

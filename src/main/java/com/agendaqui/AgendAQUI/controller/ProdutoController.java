package com.agendaqui.AgendAQUI.controller;

import com.agendaqui.AgendAQUI.model.PrestadorServico;
import com.agendaqui.AgendAQUI.model.Produto;
import com.agendaqui.AgendAQUI.repository.PrestadorRepository;
import com.agendaqui.AgendAQUI.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@RestController
@RequestMapping(path = "/produtos")
public class ProdutoController {
    private final ProdutoRepository produtoRepository;
    private final PrestadorRepository prestadorRepository;

    public ProdutoController(ProdutoRepository produtoRepository, PrestadorRepository prestadorRepository) {
        this.produtoRepository = produtoRepository;
        this.prestadorRepository = prestadorRepository;
    }

    @GetMapping("/listarTodos") // Rota que retorna todos os produtos da base de dados
    public ResponseEntity<List<Produto>> listarProdutos() {
        try {
            return ResponseEntity.ok(produtoRepository.findAll());
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/{id}") // Rota busca um produto por id
    public ResponseEntity<Object> buscarPorId(@PathVariable("id") Long id) {
        try {
            Optional<Produto> produto = produtoRepository.findById(id);
            if (produto.isPresent()) {
                return ResponseEntity.ok().body(produto.get());
            } else {
                return ResponseEntity.badRequest().body("Produto não encontrado");
            }
        } catch (Exception e) {
            ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
            return null;
        }
    }

    @PostMapping("/salvar")
    // Rota responsável por cadastrar um novo produto para no banco
    //{
    //    "descricao": "<descricao>",
    //    "preco": <preco>,
    //    "prestadorid": <id>
    //}
    public ResponseEntity<Object> salvarProduto(@RequestBody ObjectNode json) {
        try {
            String descricaoJson = json.get("descricao").asText();
            Double precoJson = json.get("preco").asDouble();
            Long prestadorIdJson = json.get("prestadorid").asLong();
            Optional<PrestadorServico> prestador = prestadorRepository.findById(prestadorIdJson);
            if (prestador.isPresent()) {
                Produto produto = new Produto();
                produto.setDescricao(descricaoJson);
                produto.setPreco(precoJson);
                produto.setPrestador(prestador.get());
                Optional<List<Produto>> produtos = produtoRepository.listarProdutos(prestadorIdJson);
                if (produtos.isEmpty()) {
                    List<Produto> listaNova = new ArrayList<>();
                    listaNova.add(produto);
                    prestador.get().setProdutos(listaNova);
                    return ResponseEntity.ok(produtoRepository.save(produto));
                } else {
                    List<Produto> lista = produtos.get();
                    lista.add(produto);
                    prestador.get().setProdutos(lista);
                    return ResponseEntity.ok(produtoRepository.save(produto));
                }
            } else {
                return ResponseEntity.badRequest().body("Prestador não cadastrado!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }

    @PutMapping("/alterar")
    // Rota responsável por alterar um produto já cadastrado no banco
    //{
    //    "id": <id>,
    //    "descricao": "<descricao>",
    //    "preco": <preco>,
    //}
    public ResponseEntity<Object> alterarProduto(@RequestBody ObjectNode json) {
        try {
            Long idJson = json.get("id").asLong();
            String descricaoJson = json.get("descricao").asText();
            Double precoJson = json.get("preco").asDouble();
            Optional<Produto> produto = produtoRepository.findById(idJson);
            if (produto.isPresent()) {
                Produto produtoAlterado = produto.get();
                produtoAlterado.setDescricao(descricaoJson);
                produtoAlterado.setPreco(precoJson);
                return ResponseEntity.ok(produtoRepository.save(produtoAlterado));
            } else {
                return ResponseEntity.badRequest().body("Produto não cadastrado!");
            }
        } catch (
                Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }

    @DeleteMapping("/deletar")
    // Rota responsável por deletar um produto já cadastrado no banco
    public ResponseEntity<Object> deletarProduto(@RequestParam Long produtoid) {
        try {
            Predicate<Produto> filtrarProdutosPeloId = produto -> produto.getId() != produtoid;
            Optional<Produto> produto = produtoRepository.findById(produtoid);
            if (produto.isPresent()) {
                Produto produtoDeletado = produto.get();
                Optional<List<Produto>> produtos = produtoRepository.listarProdutos(produtoDeletado.getPrestador().getId());
                if (produtos.isEmpty()) {
                    return ResponseEntity.badRequest().body("Erro de inconsistência nos dados!");
                } else {
                    List<Produto> listaAtualizada = produtos.get().stream().filter(filtrarProdutosPeloId).toList();
                    produtoDeletado.getPrestador().setProdutos(listaAtualizada);
                    produtoRepository.deleteById(produtoid);
                    return ResponseEntity.ok().body(produtoDeletado);
                }
            } else {
                return ResponseEntity.badRequest().body("Produto não cadastrado!");
            }
        } catch (
                Exception e) {
            return ResponseEntity.badRequest().body("Requisição com parâmetros errados!\n" + e.getMessage());
        }
    }
}

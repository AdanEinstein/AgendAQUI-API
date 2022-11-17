package com.agendaqui.AgendAQUI.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Prestador")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PrestadorServico implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpfj;
    private String telefone;
    private String descricao;
    private String categoria;
    private String email;
    private String paginaFacebook;
    @OneToOne(cascade = CascadeType.ALL)
    private Login login;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Produto>produtos;
}

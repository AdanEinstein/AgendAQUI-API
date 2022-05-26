package com.agendaqui.AgendAQUI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Produto")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Produto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private Double preco;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private PrestadorServico prestador;
}

package br.com.gerenciamentoteatro.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_ingresso")
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    @Column(name = "data_exibicao", nullable = false)
    private LocalDate dataExibicao;

    @Column(nullable = false)
    private Integer quantidade;

    public Ingresso() {}

    public Ingresso(Cliente cliente, Peca peca, LocalDate dataExibicao, Integer quantidade) {
        this.cliente = cliente;
        this.peca = peca;
        this.dataExibicao = dataExibicao;
        this.quantidade = quantidade;
    }

    public Ingresso(Long id, Cliente cliente, Peca peca, LocalDate dataExibicao, Integer quantidade) {
        this.id = id;
        this.cliente = cliente;
        this.peca = peca;
        this.dataExibicao = dataExibicao;
        this.quantidade = quantidade;
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Peca getPeca() { return peca; }
    public LocalDate getDataExibicao() { return dataExibicao; }
    public Integer getQuantidade() { return quantidade; }
}
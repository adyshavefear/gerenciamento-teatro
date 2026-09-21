package br.com.gerenciamentoteatro.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_proposta_aluguel")
public class PropostaAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    public PropostaAluguel() {}

    public PropostaAluguel(LocalDate dataCriacao, Artista artista, Peca peca) {
        this.dataCriacao = dataCriacao;
        this.artista = artista;
        this.peca = peca;
    }

    public PropostaAluguel(Long id, LocalDate dataCriacao, Artista artista, Peca peca) {
        this.id = id;
        this.dataCriacao = dataCriacao;
        this.artista = artista;
        this.peca = peca;
    }

    public Long getId() { return id; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public Artista getArtista() { return artista; }
    public Peca getPeca() { return peca; }
}
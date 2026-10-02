package br.com.gerenciamentoteatro.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_proposta_aluguel")
public class PropostaAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_proposta", nullable = false)
    private LocalDate dataProposta;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    public PropostaAluguel() {}

    public PropostaAluguel(LocalDate dataProposta, Artista artista, Peca peca) {
        this.dataProposta = dataProposta;
        this.artista = artista;
        this.peca = peca;
    }

    public PropostaAluguel(Long id, LocalDate dataProposta, Artista artista, Peca peca) {
        this.id = id;
        this.dataProposta = dataProposta;
        this.artista = artista;
        this.peca = peca;
    }

    public Long getId() { return id; }
    public LocalDate getDataProposta() { return dataProposta; }
    public void setDataProposta(LocalDate dataProposta) { this.dataProposta = dataProposta; }
    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }
    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }
}
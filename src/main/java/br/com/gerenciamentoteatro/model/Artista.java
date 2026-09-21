package br.com.gerenciamentoteatro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_artista")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    private String telefone;
    private String email;

    public Artista() {}

    public Artista(String cpf, String nomeCompleto, String telefone, String email) {
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.telefone = telefone;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getCpf() { return cpf; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
}
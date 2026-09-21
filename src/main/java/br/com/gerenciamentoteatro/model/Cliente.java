package br.com.gerenciamentoteatro.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    private String telefone;
    private String email;
    private String genero;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    public Cliente() {}

    public Cliente(String cpf, String nomeCompleto, String telefone, String email, String genero, LocalDate dataNascimento) {
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.telefone = telefone;
        this.email = email;
        this.genero = genero;
        this.dataNascimento = dataNascimento;
    }

    public Long getId() { return id; }
    public String getCpf() { return cpf; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getGenero() { return genero; }
    public LocalDate getDataNascimento() { return dataNascimento; }
}
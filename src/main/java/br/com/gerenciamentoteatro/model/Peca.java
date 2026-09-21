package br.com.gerenciamentoteatro.model;

import br.com.gerenciamentoteatro.model.enums.Turno;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_peca")
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fim", nullable = false)
    private LocalTime horarioFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    public Peca() {}

    public Peca(String nome, LocalDate dataInicio, LocalDate dataFim, LocalTime horarioInicio, LocalTime horarioFim, Turno turno) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.turno = turno;
    }

    public Peca(Long id, String nome, LocalDate dataInicio, LocalDate dataFim, LocalTime horarioInicio, LocalTime horarioFim, Turno turno) {
        this.id = id;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.turno = turno;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public LocalTime getHorarioFim() { return horarioFim; }
    public Turno getTurno() { return turno; }
}
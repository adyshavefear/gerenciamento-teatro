package br.com.gerenciamentoteatro.model;

import br.com.gerenciamentoteatro.model.enums.Turno;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;

@Entity
@Table(name = "tb_regra_preco")
public class RegraPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DayOfWeek diaSemana;

    @Enumerated(EnumType.STRING)
    private Turno turno;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Enumerated(EnumType.STRING)
    private Month mes;

    private Integer ano;

    public RegraPreco() {}

    public RegraPreco(BigDecimal valorHora, DayOfWeek diaSemana, Turno turno, LocalTime horarioInicio, LocalTime horarioFim, Month mes, Integer ano) {
        this.valorHora = valorHora;
        this.diaSemana = diaSemana;
        this.turno = turno;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.mes = mes;
        this.ano = ano;
    }

    public RegraPreco(Long id, BigDecimal valorHora, DayOfWeek diaSemana, Turno turno, LocalTime horarioInicio, LocalTime horarioFim, Month mes, Integer ano) {
        this.id = id;
        this.valorHora = valorHora;
        this.diaSemana = diaSemana;
        this.turno = turno;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.mes = mes;
        this.ano = ano;
    }

    public Long getId() { return id; }
    public BigDecimal getValorHora() { return valorHora; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public Turno getTurno() { return turno; }
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public LocalTime getHorarioFim() { return horarioFim; }
    public Month getMes() { return mes; }
    public Integer getAno() { return ano; }
}
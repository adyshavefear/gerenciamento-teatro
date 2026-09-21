package br.com.gerenciamentoteatro.model;

import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_contrato_aluguel")
public class ContratoAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proposta_id", referencedColumnName = "id", nullable = false)
    private PropostaAluguel proposta;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusContrato status;

    public ContratoAluguel() {}

    public ContratoAluguel(PropostaAluguel proposta, LocalDate dataInicio, LocalDate dataFim, BigDecimal valorTotal) {
        this.proposta = proposta;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorTotal = valorTotal;
        this.status = StatusContrato.EM_CONTRATACAO;
    }

    public ContratoAluguel(Long id, PropostaAluguel proposta, LocalDate dataInicio, LocalDate dataFim, BigDecimal valorTotal) {
        this.id = id;
        this.proposta = proposta;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorTotal = valorTotal;
        this.status = StatusContrato.EM_CONTRATACAO;
    }

    public Long getId() { return id; }
    public PropostaAluguel getProposta() { return proposta; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public StatusContrato getStatus() { return status; }
    public void setStatus(StatusContrato status) { this.status = status; }

    @Override
    public String toString() {
        if (proposta != null && proposta.getPeca() != null) {
            return proposta.getPeca().getNome() + " (Contrato #" + id + ")";
        }
        return "Contrato #" + id;
    }
}
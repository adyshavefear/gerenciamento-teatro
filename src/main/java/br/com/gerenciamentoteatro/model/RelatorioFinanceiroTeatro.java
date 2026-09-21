package br.com.gerenciamentoteatro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RelatorioFinanceiroTeatro {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal totalVendaIngressos;
    private BigDecimal totalContratosAluguel;
    private BigDecimal receitaTotalGeral;

    public RelatorioFinanceiroTeatro(LocalDate dataInicio, LocalDate dataFim, BigDecimal totalVendaIngressos, BigDecimal totalContratosAluguel) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.totalVendaIngressos = totalVendaIngressos;
        this.totalContratosAluguel = totalContratosAluguel;
        this.receitaTotalGeral = totalVendaIngressos.add(totalContratosAluguel);
    }

    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public BigDecimal getTotalVendaIngressos() { return totalVendaIngressos; }
    public BigDecimal getTotalContratosAluguel() { return totalContratosAluguel; }
    public BigDecimal getReceitaTotalGeral() { return receitaTotalGeral; }
}
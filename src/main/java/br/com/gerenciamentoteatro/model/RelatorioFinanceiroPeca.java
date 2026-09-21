package br.com.gerenciamentoteatro.model;

import java.math.BigDecimal;

public class RelatorioFinanceiroPeca {

    private String nomePeca;
    private String nomeArtista;
    private BigDecimal arrecadacaoIngressos;
    private BigDecimal custoAluguel;
    private BigDecimal valorLiquidoRepasse; // Arrecadação - Aluguel

    public RelatorioFinanceiroPeca(String nomePeca, String nomeArtista, BigDecimal arrecadacaoIngressos, BigDecimal custoAluguel) {
        this.nomePeca = nomePeca;
        this.nomeArtista = nomeArtista;
        this.arrecadacaoIngressos = arrecadacaoIngressos;
        this.custoAluguel = custoAluguel;
        this.valorLiquidoRepasse = arrecadacaoIngressos.subtract(custoAluguel);
    }

    public String getNomePeca() { return nomePeca; }
    public String getNomeArtista() { return nomeArtista; }
    public BigDecimal getArrecadacaoIngressos() { return arrecadacaoIngressos; }
    public BigDecimal getCustoAluguel() { return custoAluguel; }
    public BigDecimal getValorLiquidoRepasse() { return valorLiquidoRepasse; }
}
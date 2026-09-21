package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.model.Ingresso;
import br.com.gerenciamentoteatro.model.RelatorioFinanceiroPeca;
import br.com.gerenciamentoteatro.model.RelatorioFinanceiroTeatro;
import br.com.gerenciamentoteatro.repository.ContratoAluguelRepository;
import br.com.gerenciamentoteatro.repository.IngressoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RelatorioFinanceiroService {

    private final ContratoAluguelRepository contratoRepository;
    private final IngressoRepository ingressoRepository;

    public RelatorioFinanceiroService(ContratoAluguelRepository contratoRepository, IngressoRepository ingressoRepository) {
        this.contratoRepository = contratoRepository;
        this.ingressoRepository = ingressoRepository;
    }

    /**
     * REQUISITO 13: Relatório Financeiro da Peça no período do contrato.
     */
    public RelatorioFinanceiroPeca gerarRelatorioPeca(Long contratoId, BigDecimal precoTicket) {
        ContratoAluguel contrato = contratoRepository.buscarPorId(contratoId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado."));

        Long pecaId = contrato.getProposta().getPeca().getId();
        List<Ingresso> ingressos = ingressoRepository.listarTodos();

        // Calcula total de ingressos vendidos para esta peça
        int totalIngressos = ingressos.stream()
                .filter(i -> i.getPeca().getId().equals(pecaId))
                .mapToInt(Ingresso::getQuantidade)
                .sum();

        BigDecimal arrecadacaoTotal = precoTicket.multiply(BigDecimal.valueOf(totalIngressos));
        BigDecimal custoAluguel = contrato.getValorTotal();

        return new RelatorioFinanceiroPeca(
                contrato.getProposta().getPeca().getNome(),
                contrato.getProposta().getArtista().getNomeCompleto(),
                arrecadacaoTotal,
                custoAluguel
        );
    }

    /**
     * REQUISITO 16: Relatório Financeiro do Teatro por intervalo de tempo.
     */
    public RelatorioFinanceiroTeatro gerarRelatorioTeatro(LocalDate dataInicio, LocalDate dataFim, BigDecimal precoPadraoTicket) {
        List<ContratoAluguel> contratos = contratoRepository.listarTodos();
        List<Ingresso> ingressos = ingressoRepository.listarTodos();

        // Soma aluguéis de contratos ativos/encerrados dentro do período
        BigDecimal totalAlugueis = contratos.stream()
                .filter(c -> !c.getDataFim().isBefore(dataInicio) && !c.getDataInicio().isAfter(dataFim))
                .map(ContratoAluguel::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Soma venda de ingressos dentro do período
        int totalIngressos = ingressos.stream()
                .filter(i -> !i.getDataExibicao().isBefore(dataInicio) && !i.getDataExibicao().isAfter(dataFim))
                .mapToInt(Ingresso::getQuantidade)
                .sum();

        BigDecimal totalIngressosArrecadado = precoPadraoTicket.multiply(BigDecimal.valueOf(totalIngressos));

        return new RelatorioFinanceiroTeatro(dataInicio, dataFim, totalIngressosArrecadado, totalAlugueis);
    }
}
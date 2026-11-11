package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.model.Ingresso;
import br.com.gerenciamentoteatro.model.PropostaAluguel;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.repository.ContratoAluguelRepository;
import br.com.gerenciamentoteatro.repository.IngressoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

public class ContratoAluguelService {

    private final ContratoAluguelRepository contratoRepository;
    private final IngressoRepository ingressoRepository;
    private final RegraPrecoService regraPrecoService;

    public ContratoAluguelService(ContratoAluguelRepository contratoRepository,
                                  IngressoRepository ingressoRepository,
                                  RegraPrecoService regraPrecoService) {
        this.contratoRepository = contratoRepository;
        this.ingressoRepository = ingressoRepository;
        this.regraPrecoService = regraPrecoService;
    }


    public void cadastrarProposta(ContratoAluguel contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("O contrato de aluguel não pode ser nulo.");
        }
        validarConflitoPeriodo(
                contrato.getId(),
                contrato.getDataInicio(),
                contrato.getDataFim()
        );
        contrato.setValorTotal(recalcularValorContrato(contrato, contrato.getDataFim()));
        contratoRepository.salvar(contrato);
    }
    public void atualizarContrato(ContratoAluguel contrato) {
        validarConflitoPeriodo(contrato.getId(), contrato.getDataInicio(), contrato.getDataFim());
        contratoRepository.salvar(contrato);
    }

    public List<ContratoAluguel> listarPropostas() {
        return contratoRepository.listarTodos();
    }

    public Optional<ContratoAluguel> buscarPorId(Long id) {
        return contratoRepository.buscarPorId(id);
    }


    public void encerrarContrato(Long contratoId, LocalDate dataEncerramentoEfetiva, BigDecimal valorIngressosRepassado) {
        ContratoAluguel contrato = contratoRepository.buscarPorId(contratoId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado."));

        if (contrato.getStatus() == StatusContrato.ENCERRADO || contrato.getStatus() == StatusContrato.ENCERRADO_ANTECIPADAMENTE) {
            throw new IllegalStateException("O contrato já está encerrado.");
        }

        if (dataEncerramentoEfetiva == null || dataEncerramentoEfetiva.isBefore(contrato.getDataInicio())) {
            throw new IllegalArgumentException("O encerramento não pode ser anterior ao início do contrato.");
        }

        // Validação Req 14: Não permite antecipar se houver ingressos vendidos para data posterior à data de encerramento
        List<Ingresso> ingressosVendidos = ingressoRepository.listarTodos();
        boolean possuiIngressoFuturo = ingressosVendidos.stream()
                .filter(i -> i.getPeca().getId().equals(contrato.getProposta().getPeca().getId()))
                .anyMatch(i -> i.getDataExibicao().isAfter(dataEncerramentoEfetiva));

        if (possuiIngressoFuturo) {
            throw new IllegalStateException("Não é possível encerrar o contrato: existem ingressos vendidos para datas posteriores a " + dataEncerramentoEfetiva);
        }

        if (dataEncerramentoEfetiva.isBefore(contrato.getDataFim())) {
            BigDecimal novoValorAluguel = recalcularValorContrato(contrato, dataEncerramentoEfetiva);
            contrato.setDataFim(dataEncerramentoEfetiva);
            contrato.setValorTotal(novoValorAluguel);
            contrato.setStatus(StatusContrato.ENCERRADO_ANTECIPADAMENTE);
        } else {
            contrato.setStatus(StatusContrato.ENCERRADO);
        }

        contratoRepository.salvar(contrato);

        System.out.println("Contrato " + contratoId + " encerrado em " + dataEncerramentoEfetiva);
        System.out.println("Valor de ingressos repassado ao artista: R$ " + valorIngressosRepassado);
    }


    public void estenderContrato(Long contratoId, LocalDate novaDataFim) {
        ContratoAluguel contrato = contratoRepository.buscarPorId(contratoId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado."));

        if (!novaDataFim.isAfter(contrato.getDataFim())) {
            throw new IllegalArgumentException("A nova data final deve ser posterior à data final atual (" + contrato.getDataFim() + ").");
        }

        LocalDate inicioAdicional = contrato.getDataFim().plusDays(1);

        // Valida se o período estendido conflita com outras peças cadastradas
        validarConflitoPeriodo(contrato.getId(), inicioAdicional, novaDataFim);

        BigDecimal novoValorTotal = recalcularValorContrato(contrato, novaDataFim);
        contrato.setDataFim(novaDataFim);
        contrato.setValorTotal(novoValorTotal);
        contrato.setStatus(StatusContrato.CONTRATADO_COM_ALTERACAO);

        contratoRepository.salvar(contrato);

        System.out.println("[EMAIL SIMULADO] Novo contrato estendido enviado para " + contrato.getProposta().getArtista().getEmail());
    }

    private void validarConflitoPeriodo(Long contratoIdAtual, LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Período do contrato inválido.");
        }
        List<ContratoAluguel> outrosContratos = contratoRepository.listarTodos();

        for (ContratoAluguel c : outrosContratos) {
            if (!Objects.equals(c.getId(), contratoIdAtual)
                    && c.getStatus() != StatusContrato.ENCERRADO
                    && c.getStatus() != StatusContrato.ENCERRADO_ANTECIPADAMENTE) {
                boolean sobrepoe = !(fim.isBefore(c.getDataInicio()) || inicio.isAfter(c.getDataFim()));
                if (sobrepoe) {
                    throw new IllegalArgumentException("Não é possível realizar a operação: o período conflita com a peça " + c.getProposta().getPeca().getNome());
                }
            }
        }
    }

    private BigDecimal recalcularValorContrato(ContratoAluguel contrato, LocalDate fim) {
        BigDecimal total = BigDecimal.ZERO;
        LocalDate atual = contrato.getDataInicio();

        while (!atual.isAfter(fim)) {
            var peca = contrato.getProposta().getPeca();
            var inicioExibicao = atual.atTime(peca.getHorarioInicio());
            var fimExibicao = atual.atTime(peca.getHorarioFim());
            if (!fimExibicao.isAfter(inicioExibicao)) fimExibicao = fimExibicao.plusDays(1);
            BigDecimal valorDia = regraPrecoService.calcularValor(inicioExibicao, fimExibicao);
            total = total.add(valorDia);
            atual = atual.plusDays(1);
        }

        return total;
    }
}

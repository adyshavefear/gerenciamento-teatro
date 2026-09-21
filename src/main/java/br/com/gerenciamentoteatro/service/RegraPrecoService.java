package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.RegraPreco;
import br.com.gerenciamentoteatro.model.enums.Turno;
import br.com.gerenciamentoteatro.repository.RegraPrecoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;
import java.util.Optional;

public class RegraPrecoService {

    private final RegraPrecoRepository repository;

    public RegraPrecoService(RegraPrecoRepository repository) {
        this.repository = repository;
    }

    public void cadastrarRegra(RegraPreco regra) {
        if (regra == null) {
            throw new IllegalArgumentException("A regra de preço não pode ser nula.");
        }
        if (regra.getValorHora() == null || regra.getValorHora().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da hora deve ser maior que zero.");
        }
        repository.salvar(regra);
    }

    public List<RegraPreco> listarRegras() {
        return repository.listarTodos();
    }

    public Optional<RegraPreco> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public void excluirRegra(Long id) {
        repository.excluir(id);
    }

    public boolean seAplicaA(RegraPreco regra, LocalDateTime dataHora) {
        if (regra == null) {
            throw new IllegalArgumentException("A regra de preço não pode ser nula.");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("A data e hora não podem ser nulas.");
        }

        if (regra.getAno() != null && dataHora.getYear() != regra.getAno()) {
            return false;
        }

        if (regra.getMes() != null && dataHora.getMonth() != regra.getMes()) {
            return false;
        }

        if (regra.getDiaSemana() != null && dataHora.getDayOfWeek() != regra.getDiaSemana()) {
            return false;
        }

        LocalTime horaAgendada = dataHora.toLocalTime();

        if (regra.getHorarioInicio() != null && regra.getHorarioFim() != null) {
            if (horaAgendada.isBefore(regra.getHorarioInicio()) || horaAgendada.isAfter(regra.getHorarioFim())) {
                return false;
            }
        }

        if (regra.getTurno() != null) {
            Turno turnoAgendado = descobrirTurno(horaAgendada);
            if (regra.getTurno() != turnoAgendado) {
                return false;
            }
        }

        return true;
    }

    public List<RegraPreco> encontrarRegrasAplicaveis(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new IllegalArgumentException("A data e hora não podem ser nulas.");
        }

        return repository.listarTodos()
                .stream()
                .filter(regra -> seAplicaA(regra, dataHora))
                .toList();
    }

    public RegraPreco selecionarMelhorRegra(List<RegraPreco> regras) {
        if (regras == null || regras.isEmpty()) {
            return null;
        }

        return regras.stream()
                .max((regra1, regra2) -> regra1.getValorHora().compareTo(regra2.getValorHora()))
                .orElse(null);
    }

    public BigDecimal calcularValor(LocalDateTime inicio, LocalDateTime fim) {
        validarPeriodo(inicio, fim);

        BigDecimal valorTotal = BigDecimal.ZERO;
        LocalDateTime momentoAtual = inicio;

        while (momentoAtual.isBefore(fim)) {
            RegraPreco melhorRegra = selecionarMelhorRegra(encontrarRegrasAplicaveis(momentoAtual));

            if (melhorRegra == null) {
                throw new IllegalArgumentException("Não existe regra de preço aplicável ao período: " + momentoAtual);
            }

            LocalDateTime proximoMomento = momentoAtual.toLocalDate().plusDays(1).atStartOfDay();

            if (proximoMomento.isAfter(fim)) {
                proximoMomento = fim;
            }

            long minutos = Duration.between(momentoAtual, proximoMomento).toMinutes();

            BigDecimal horas = BigDecimal.valueOf(minutos)
                    .divide(BigDecimal.valueOf(60), 10, RoundingMode.HALF_UP);

            BigDecimal valorPeriodo = melhorRegra.getValorHora().multiply(horas);
            valorTotal = valorTotal.add(valorPeriodo);

            momentoAtual = proximoMomento;
        }

        return valorTotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValorHoraAplicavel(
            LocalTime horario,
            DayOfWeek diaSemana,
            Turno turno,
            Month mes,
            Integer ano) {

        List<RegraPreco> regras = repository.listarTodos();
        BigDecimal maiorValor = BigDecimal.ZERO;

        for (RegraPreco regra : regras) {
            boolean compativel = true;

            if (regra.getDiaSemana() != null && !regra.getDiaSemana().equals(diaSemana)) compativel = false;
            if (regra.getTurno() != null && !regra.getTurno().equals(turno)) compativel = false;
            if (regra.getMes() != null && !regra.getMes().equals(mes)) compativel = false;
            if (regra.getAno() != null && !regra.getAno().equals(ano)) compativel = false;

            if (regra.getHorarioInicio() != null && regra.getHorarioFim() != null) {
                if (horario.isBefore(regra.getHorarioInicio()) || horario.isAfter(regra.getHorarioFim())) {
                    compativel = false;
                }
            }

            if (compativel && regra.getValorHora() != null) {
                if (regra.getValorHora().compareTo(maiorValor) > 0) {
                    maiorValor = regra.getValorHora();
                }
            }
        }

        return maiorValor;
    }

    private void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null) {
            throw new IllegalArgumentException("A data e hora de início não podem ser nulas.");
        }
        if (fim == null) {
            throw new IllegalArgumentException("A data e hora de fim não podem ser nulas.");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("A data e hora de fim devem ser posteriores ao início.");
        }
    }

    private Turno descobrirTurno(LocalTime hora) {
        if (hora.isBefore(LocalTime.of(12, 0))) {
            return Turno.MATUTINO;
        }
        if (hora.isBefore(LocalTime.of(18, 0))) {
            return Turno.VESPERTINO;
        }
        return Turno.NOTURNO;
    }
}
package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.RegraPreco;
import br.com.gerenciamentoteatro.model.enums.Turno;
import br.com.gerenciamentoteatro.repository.RegraPrecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegraPrecoServiceTest {

    private RegraPrecoService service;

    @BeforeEach
    void setUp() {
        service = new RegraPrecoService(
                new RegraPrecoRepository()
        );
    }

    @Test
    void deveAplicarRegraGeral() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("10.00"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                10,
                0
        );

        assertTrue(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void deveAplicarRegraPorAno() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("15.00"),
                null,
                null,
                null,
                null,
                null,
                2026
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                10,
                0
        );

        assertTrue(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void naoDeveAplicarRegraDeOutroAno() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("15.00"),
                null,
                null,
                null,
                null,
                null,
                2025
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                10,
                0
        );

        assertFalse(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void deveAplicarRegraPorMes() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("20.00"),
                null,
                null,
                null,
                null,
                Month.AUGUST,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                10,
                0
        );

        assertTrue(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void naoDeveAplicarRegraDeOutroMes() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("20.00"),
                null,
                null,
                null,
                null,
                Month.JULY,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                10,
                0
        );

        assertFalse(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void deveAplicarRegraPorDiaDaSemana() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("25.00"),
                DayOfWeek.TUESDAY,
                null,
                null,
                null,
                null,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                10,
                0
        );

        assertTrue(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void naoDeveAplicarRegraDeOutroDiaDaSemana() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("25.00"),
                DayOfWeek.MONDAY,
                null,
                null,
                null,
                null,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                10,
                0
        );

        assertFalse(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void deveAplicarRegraPorHorario() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("30.00"),
                null,
                null,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0),
                null,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                20,
                0
        );

        assertTrue(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void naoDeveAplicarRegraForaDoHorario() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("30.00"),
                null,
                null,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0),
                null,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                18,
                0
        );

        assertFalse(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void deveAplicarRegraPorTurno() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("35.00"),
                null,
                Turno.NOTURNO,
                null,
                null,
                null,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                20,
                0
        );

        assertTrue(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void naoDeveAplicarRegraDeOutroTurno() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("35.00"),
                null,
                Turno.MATUTINO,
                null,
                null,
                null,
                null
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                20,
                0
        );

        assertFalse(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void deveAplicarRegraComMultiplosCriterios() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("50.00"),
                DayOfWeek.TUESDAY,
                Turno.NOTURNO,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0),
                Month.AUGUST,
                2026
        );

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                20,
                0
        );

        assertTrue(
                service.seAplicaA(regra, dataHora)
        );
    }

    @Test
    void deveEncontrarTodasAsRegrasAplicaveis() {

        RegraPreco regraGeral = new RegraPreco(
                1L,
                new BigDecimal("10.00"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        RegraPreco regraAgosto = new RegraPreco(
                2L,
                new BigDecimal("15.00"),
                null,
                null,
                null,
                null,
                Month.AUGUST,
                null
        );

        RegraPreco regraTerca = new RegraPreco(
                3L,
                new BigDecimal("20.00"),
                DayOfWeek.TUESDAY,
                null,
                null,
                null,
                null,
                null
        );

        RegraPreco regraNoturna = new RegraPreco(
                4L,
                new BigDecimal("30.00"),
                DayOfWeek.TUESDAY,
                Turno.NOTURNO,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0),
                Month.AUGUST,
                2026
        );

        service.adicionarRegra(regraGeral);
        service.adicionarRegra(regraAgosto);
        service.adicionarRegra(regraTerca);
        service.adicionarRegra(regraNoturna);

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                20,
                0
        );

        List<RegraPreco> regras =
                service.encontrarRegrasAplicaveis(dataHora);

        assertEquals(4, regras.size());
    }

    @Test
    void deveSelecionarRegraDeMaiorValor() {

        RegraPreco regra10 = new RegraPreco(
                1L,
                new BigDecimal("10.00"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        RegraPreco regra30 = new RegraPreco(
                2L,
                new BigDecimal("30.00"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        RegraPreco regra20 = new RegraPreco(
                3L,
                new BigDecimal("20.00"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<RegraPreco> regras = List.of(
                regra10,
                regra30,
                regra20
        );

        RegraPreco melhorRegra =
                service.selecionarMelhorRegra(regras);

        assertNotNull(melhorRegra);

        assertEquals(
                new BigDecimal("30.00"),
                melhorRegra.getValorHora()
        );
    }

    @Test
    void deveRetornarNullQuandoNaoExistiremRegras() {

        RegraPreco melhorRegra =
                service.selecionarMelhorRegra(List.of());

        assertNull(melhorRegra);
    }

    @Test
    void deveRejeitarRegraNula() {

        LocalDateTime dataHora = LocalDateTime.of(
                2026,
                8,
                25,
                20,
                0
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.seAplicaA(null, dataHora)
        );
    }

    @Test
    void deveRejeitarDataHoraNula() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("10.00"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.seAplicaA(regra, null)
        );
    }
    @Test
    void deveCalcularValorParaTresHoras() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("30.00"),
                null,
                Turno.NOTURNO,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0),
                null,
                null
        );

        service.adicionarRegra(regra);

        LocalDateTime inicio = LocalDateTime.of(
                2026,
                8,
                25,
                19,
                0
        );

        LocalDateTime fim = LocalDateTime.of(
                2026,
                8,
                25,
                22,
                0
        );

        BigDecimal valor =
                service.calcularValor(inicio, fim);

        assertEquals(
                new BigDecimal("90.00"),
                valor
        );
    }
    @Test
    void deveCalcularValorParaDuasHorasEMeia() {

        RegraPreco regra = new RegraPreco(
                1L,
                new BigDecimal("30.00"),
                null,
                Turno.NOTURNO,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0),
                null,
                null
        );

        service.adicionarRegra(regra);

        LocalDateTime inicio = LocalDateTime.of(
                2026,
                8,
                25,
                19,
                30
        );

        LocalDateTime fim = LocalDateTime.of(
                2026,
                8,
                25,
                22,
                0
        );

        BigDecimal valor =
                service.calcularValor(inicio, fim);

        assertEquals(
                new BigDecimal("75.00"),
                valor
        );
    }
    @Test
    void deveRejeitarCalculoSemRegraAplicavel() {

        LocalDateTime inicio = LocalDateTime.of(
                2026,
                8,
                25,
                19,
                0
        );

        LocalDateTime fim = LocalDateTime.of(
                2026,
                8,
                25,
                22,
                0
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calcularValor(inicio, fim)
        );
    }
    @Test
    void deveCalcularValorQuandoHaMudancaDeRegraNoMesmoDia() {

        RegraPreco regraNoturnaInicial = new RegraPreco(
                1L,
                new BigDecimal("20.00"),
                DayOfWeek.TUESDAY,
                Turno.NOTURNO,
                LocalTime.of(19, 0),
                LocalTime.of(20, 59),
                null,
                2026
        );

        RegraPreco regraNoturnaFinal = new RegraPreco(
                2L,
                new BigDecimal("30.00"),
                DayOfWeek.TUESDAY,
                Turno.NOTURNO,
                LocalTime.of(21, 0),
                LocalTime.of(23, 0),
                null,
                2026
        );

        service.adicionarRegra(regraNoturnaInicial);
        service.adicionarRegra(regraNoturnaFinal);

        LocalDateTime inicio = LocalDateTime.of(
                2026,
                8,
                25,
                19,
                0
        );

        LocalDateTime fim = LocalDateTime.of(
                2026,
                8,
                25,
                23,
                0
        );

        BigDecimal valor =
                service.calcularValor(inicio, fim);

        assertEquals(
                new BigDecimal("100.00"),
                valor
        );
    }
}
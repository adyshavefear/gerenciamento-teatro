package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.model.PropostaAluguel;
import br.com.gerenciamentoteatro.model.Artista;
import br.com.gerenciamentoteatro.model.Peca;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.model.enums.StatusProposta;
import br.com.gerenciamentoteatro.model.enums.Turno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ContratoAluguelServiceTest {

    private ContratoAluguelService service;
    private ContratoAluguel contrato;

    @BeforeEach
    void setUp() {

        service = new ContratoAluguelService();

        Artista artista = new Artista(
                1L,
                "12345678901",
                "João da Silva",
                "83999999999",
                "joao@email.com",
                "Masculino",
                LocalDate.of(1990, 5, 10)
        );

        Peca peca = new Peca(
                1L,
                "Hamlet",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                LocalTime.of(19, 0),
                LocalTime.of(22, 0),
                Turno.NOTURNO
        );

        PropostaAluguel proposta = new PropostaAluguel(
                1L,
                LocalDate.of(2026, 8, 24),
                artista,
                peca
        );

        proposta.setStatus(StatusProposta.APROVADA);

        contrato = new ContratoAluguel(
                1L,
                proposta,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                new BigDecimal("15000.00")
        );
    }

    @Test
    void deveEncerrarContrato() {

        service.encerrarContrato(contrato);

        assertEquals(
                StatusContrato.ENCERRADO,
                contrato.getStatus()
        );
    }

    @Test
    void deveEncerrarContratoAntecipadamente() {

        service.encerrarAntecipadamente(contrato);

        assertEquals(
                StatusContrato.ENCERRADO_ANTECIPADAMENTE,
                contrato.getStatus()
        );
    }

    @Test
    void deveEstenderContrato() {

        LocalDate novaDataFim = LocalDate.of(2026, 10, 15);

        service.estenderContrato(
                contrato,
                novaDataFim
        );

        assertEquals(
                novaDataFim,
                contrato.getDataFim()
        );
    }

    @Test
    void naoDeveEstenderContratoComDataAnterior() {

        LocalDate novaDataFim = LocalDate.of(2026, 9, 20);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.estenderContrato(
                        contrato,
                        novaDataFim
                )
        );
    }

    @Test
    void naoDeveEncerrarContratoJaEncerrado() {

        service.encerrarContrato(contrato);

        assertThrows(
                IllegalStateException.class,
                () -> service.encerrarContrato(contrato)
        );
    }
}

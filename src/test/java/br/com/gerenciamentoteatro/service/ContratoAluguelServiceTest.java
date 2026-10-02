package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.*;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.model.enums.Turno;
import br.com.gerenciamentoteatro.repository.ContratoAluguelRepository;
import br.com.gerenciamentoteatro.repository.IngressoRepository;
import br.com.gerenciamentoteatro.repository.RegraPrecoRepository;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class ContratoAluguelServiceTest {

    private ContratoAluguelService contratoService;
    private ContratoAluguelRepository contratoRepository;
    private IngressoRepository ingressoRepository;
    private RegraPrecoService regraPrecoService;

    @Before
    public void setUp() {
        contratoRepository = new ContratoAluguelRepository();
        ingressoRepository = new IngressoRepository();
        RegraPrecoRepository regraPrecoRepository = new RegraPrecoRepository();
        regraPrecoService = new RegraPrecoService(regraPrecoRepository);

        contratoService = new ContratoAluguelService(contratoRepository, ingressoRepository, regraPrecoService);
    }

    @Test
    public void deveEncerrarContratoComSucesso() {
        Artista artista = new Artista("123.456.789-00", "Fernanda Montenegro", "(11) 98888-7777", "fernanda@teatro.com");
        Peca peca = new Peca(1L, "Auto da Compadecida", LocalDate.now(), LocalDate.now().plusDays(5), LocalTime.of(19, 0), LocalTime.of(21, 0), Turno.NOTURNO);
        PropostaAluguel proposta = new PropostaAluguel(1L, LocalDate.now(), artista, peca);

        ContratoAluguel contrato = new ContratoAluguel(101L, proposta, LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("1200.00"));
        contratoRepository.salvar(contrato);

        contratoService.encerrarContrato(101L, LocalDate.now().plusDays(2), new BigDecimal("500.00"));

        ContratoAluguel contratoAtualizado = contratoRepository.buscarPorId(101L).orElse(null);
        assertNotNull(contratoAtualizado);
        assertEquals(StatusContrato.ENCERRADO_ANTECIPADAMENTE, contratoAtualizado.getStatus());
    }
}
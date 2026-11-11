package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.*;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.model.enums.Turno;
import br.com.gerenciamentoteatro.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContratoAluguelServiceTest {
    private static final LocalDate DIA = LocalDate.of(2026, 8, 25);
    private ContratoAluguelRepository contratos;
    private IngressoRepository ingressos;
    private RegraPrecoService precos;
    private ContratoAluguelService service;

    @BeforeEach
    void setUp() {
        TestDatabase.reset();
        contratos = new ContratoAluguelRepository();
        ingressos = new IngressoRepository();
        precos = new RegraPrecoService(new RegraPrecoRepository());
        precos.cadastrarRegra(new RegraPreco(new BigDecimal("100.00"), null, null, null, null, null, null));
        service = new ContratoAluguelService(contratos, ingressos, precos);
    }

    private ContratoAluguel novoContrato() {
        Artista artista = new Artista("00000000000", "Test artist", "", "test@example.invalid");
        Peca peca = new Peca("Test play", DIA, DIA.plusDays(5), LocalTime.of(19, 0), LocalTime.of(21, 0), Turno.NOTURNO);
        PropostaAluguel proposta = new PropostaAluguel(DIA, artista, peca);
        return new ContratoAluguel(proposta, DIA, DIA.plusDays(5), BigDecimal.ZERO);
    }

    @Test
    void cadastraEEncerraComDuracaoCorreta() {
        ContratoAluguel contrato = novoContrato();
        service.cadastrarProposta(contrato);
        assertEquals(new BigDecimal("1200.00"), contratos.buscarPorId(contrato.getId()).orElseThrow().getValorTotal());
        service.encerrarContrato(contrato.getId(), DIA.plusDays(2), BigDecimal.ZERO);
        ContratoAluguel salvo = contratos.buscarPorId(contrato.getId()).orElseThrow();
        assertEquals(StatusContrato.ENCERRADO_ANTECIPADAMENTE, salvo.getStatus());
        assertEquals(new BigDecimal("600.00"), salvo.getValorTotal());
    }

    @Test
    void extensaoIgnoraProprioContratoEPersisteTotal() {
        ContratoAluguel contrato = novoContrato();
        service.cadastrarProposta(contrato);
        service.estenderContrato(contrato.getId(), DIA.plusDays(6));
        assertEquals(new BigDecimal("1400.00"), contratos.buscarPorId(contrato.getId()).orElseThrow().getValorTotal());
    }

    @Test
    void promocaoPersisteAposReleitura() {
        ContratoAluguel contrato = novoContrato();
        service.cadastrarProposta(contrato);
        contrato = contratos.buscarPorId(contrato.getId()).orElseThrow();
        contrato.setStatus(StatusContrato.CONTRATADO);
        service.atualizarContrato(contrato);
        assertEquals(StatusContrato.CONTRATADO, contratos.buscarPorId(contrato.getId()).orElseThrow().getStatus());
    }

    @Test
    void naoConfundeIdDePecaComIdDeContrato() {
        ContratoAluguel proposta = novoContrato();
        Peca peca = new Peca(7L, "Incoming play", DIA, DIA.plusDays(5), LocalTime.of(19, 0), LocalTime.of(21, 0), Turno.NOTURNO);
        proposta.getProposta().setPeca(peca);
        ContratoAluguel existente = new ContratoAluguel(7L, novoContrato().getProposta(), DIA, DIA.plusDays(5), BigDecimal.TEN);
        ContratoAluguelRepository fake = new ContratoAluguelRepository() {
            @Override public List<ContratoAluguel> listarTodos() { return List.of(existente); }
            @Override public void salvar(ContratoAluguel c) { fail("Conflicting contract must not be saved"); }
        };
        var servico = new ContratoAluguelService(fake, ingressos, precos);
        assertThrows(IllegalArgumentException.class, () -> servico.cadastrarProposta(proposta));
    }

    @Test
    void rejeitaPeriodoInvertido() {
        ContratoAluguel contrato = novoContrato();
        contrato.setDataFim(DIA.minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> service.cadastrarProposta(contrato));
    }
}

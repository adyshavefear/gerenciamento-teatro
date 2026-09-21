package br.com.gerenciamentoteatro;

import br.com.gerenciamentoteatro.model.*;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.model.enums.Turno;
import br.com.gerenciamentoteatro.repository.*;
import br.com.gerenciamentoteatro.service.*;
import br.com.gerenciamentoteatro.ui.TelaLoginUI;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {

        AdministradorRepository adminRepository = new AdministradorRepository();
        RegraPrecoRepository regraPrecoRepository = new RegraPrecoRepository();
        ContratoAluguelRepository contratoRepository = new ContratoAluguelRepository();
        IngressoRepository ingressoRepository = new IngressoRepository();
        ClienteRepository clienteRepository = new ClienteRepository();
        ArtistaRepository artistaRepository = new ArtistaRepository();


        AdministradorService adminService = new AdministradorService(adminRepository);
        RegraPrecoService regraPrecoService = new RegraPrecoService(regraPrecoRepository);
        ContratoAluguelService contratoService = new ContratoAluguelService(contratoRepository, ingressoRepository, regraPrecoService);
        ClienteService clienteService = new ClienteService(clienteRepository);
        ArtistaService artistaService = new ArtistaService(artistaRepository);
        IngressoService ingressoService = new IngressoService(ingressoRepository);
        RelatorioFinanceiroService relatorioService = new RelatorioFinanceiroService(contratoRepository, ingressoRepository);


        if (!adminService.existeAdministrador()) {
            carregarDadosDeTeste(adminService, regraPrecoService, artistaService, contratoService);
        }


        SwingUtilities.invokeLater(() -> {
            TelaLoginUI telaLogin = new TelaLoginUI(
                    adminService,
                    regraPrecoService,
                    contratoService,
                    clienteService,
                    artistaService,
                    ingressoService,
                    relatorioService
            );
            telaLogin.setVisible(true);
        });


        Runtime.getRuntime().addShutdownHook(new Thread(JPAUtil::fecharFactory));
    }

    private static void carregarDadosDeTeste(
            AdministradorService adminService,
            RegraPrecoService regraPrecoService,
            ArtistaService artistaService,
            ContratoAluguelService contratoService) {

        try {
            // Admin de Teste
            adminService.cadastrarAdministrador("admin@teatro.com", "123456");

            // Regras de Preço Fictícias (Instanciação sem ID)
            regraPrecoService.cadastrarRegra(new RegraPreco(new BigDecimal("100.00"), DayOfWeek.FRIDAY, Turno.NOTURNO, null, null, null, null));
            regraPrecoService.cadastrarRegra(new RegraPreco(new BigDecimal("150.00"), DayOfWeek.SATURDAY, Turno.NOTURNO, null, null, null, null));

            // Artista Locatário
            Artista artista = new Artista("123.456.789-00", "Fernanda Montenegro", "(11) 98888-7777", "fernanda@teatro.com", "Feminino", LocalDate.of(1929, 10, 16));
            artistaService.cadastrarOuObter(artista);

            // Peça & Proposta/Contrato de Aluguel (Instanciação sem ID)
            Peca peca = new Peca("Auto da Compadecida", LocalDate.now(), LocalDate.now().plusDays(5), LocalTime.of(19, 0), LocalTime.of(21, 0), Turno.NOTURNO);
            PropostaAluguel proposta = new PropostaAluguel(LocalDate.now(), artista, peca);

            ContratoAluguel contrato = new ContratoAluguel(proposta, LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("1200.00"));
            contrato.setStatus(StatusContrato.CONTRATADO);
            contratoService.cadastrarProposta(contrato);

            System.out.println("Seeder executado: Banco de dados inicializado com sucesso.");
        } catch (Exception e) {
            System.out.println("Aviso: Dados de teste já carregados ou erro no Seeder: " + e.getMessage());
        }
    }
}
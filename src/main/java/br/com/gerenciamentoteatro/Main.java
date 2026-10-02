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
        // 1. Instanciação dos Repositórios (conectados ao JPA/H2)
        AdministradorRepository adminRepository = new AdministradorRepository();
        RegraPrecoRepository regraPrecoRepository = new RegraPrecoRepository();
        ContratoAluguelRepository contratoRepository = new ContratoAluguelRepository();
        IngressoRepository ingressoRepository = new IngressoRepository();
        ClienteRepository clienteRepository = new ClienteRepository();
        ArtistaRepository artistaRepository = new ArtistaRepository();

        // 2. Instanciação dos Serviços
        AdministradorService adminService = new AdministradorService(adminRepository);
        RegraPrecoService regraPrecoService = new RegraPrecoService(regraPrecoRepository);
        ContratoAluguelService contratoService = new ContratoAluguelService(contratoRepository, ingressoRepository, regraPrecoService);
        ClienteService clienteService = new ClienteService(clienteRepository);
        ArtistaService artistaService = new ArtistaService(artistaRepository);
        IngressoService ingressoService = new IngressoService(ingressoRepository);
        RelatorioFinanceiroService relatorioService = new RelatorioFinanceiroService(contratoRepository, ingressoRepository);

        // 3. Seeder: Popula o banco de dados caso esteja vazio
        if (!adminService.existeAdministrador()) {
            carregarDadosDeTeste(adminService, regraPrecoService, artistaService, contratoService);
        }

        // 4. Hook para encerramento seguro do EntityManagerFactory ao fechar o app
        Runtime.getRuntime().addShutdownHook(new Thread(JPAUtil::fecharFactory));

        // 5. Inicializa a Interface Gráfica passando todos os serviços
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
    }

    private static void carregarDadosDeTeste(
            AdministradorService adminService,
            RegraPrecoService regraPrecoService,
            ArtistaService artistaService,
            ContratoAluguelService contratoService) {

        try {
            // Administrador Inicial
            adminService.cadastrarAdministrador("admin@teatro.com", "123456");

            // Regras de Preço Fictícias
            regraPrecoService.cadastrarRegra(new RegraPreco(new BigDecimal("100.00"), DayOfWeek.FRIDAY, Turno.NOTURNO, null, null, null, null));
            regraPrecoService.cadastrarRegra(new RegraPreco(new BigDecimal("150.00"), DayOfWeek.SATURDAY, Turno.NOTURNO, null, null, null, null));

            // Artista Locatário (Apenas 4 parâmetros: CPF, Nome, Telefone, Email)
            Artista artista = new Artista("123.456.789-00", "Fernanda Montenegro", "(11) 98888-7777", "fernanda@teatro.com");
            artistaService.cadastrarOuObter(artista);

            // Peça & Proposta/Contrato de Aluguel
            Peca peca = new Peca("Auto da Compadecida", LocalDate.now(), LocalDate.now().plusDays(5), LocalTime.of(19, 0), LocalTime.of(21, 0), Turno.NOTURNO);
            PropostaAluguel proposta = new PropostaAluguel(LocalDate.now(), artista, peca);

            ContratoAluguel contrato = new ContratoAluguel(proposta, LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("1200.00"));
            contrato.setStatus(StatusContrato.CONTRATADO);
            contratoService.cadastrarProposta(contrato);

            System.out.println("Seeder executado: Banco H2 populado com dados de teste inicial!");
        } catch (Exception e) {
            System.out.println("Aviso/Erro ao carregar Seeder: " + e.getMessage());
        }
    }
}
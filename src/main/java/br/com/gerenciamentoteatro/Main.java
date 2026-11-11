package br.com.gerenciamentoteatro;

import br.com.gerenciamentoteatro.repository.*;
import br.com.gerenciamentoteatro.service.*;
import br.com.gerenciamentoteatro.ui.TelaLoginUI;

import javax.swing.SwingUtilities;

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

}

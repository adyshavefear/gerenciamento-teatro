package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.service.*;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipalUI extends JFrame {

    private final RegraPrecoService regraPrecoService;
    private final ContratoAluguelService contratoAluguelService;
    private final ClienteService clienteService;
    private final ArtistaService artistaService;
    private final IngressoService ingressoService;
    private final RelatorioFinanceiroService relatorioFinanceiroService;

    public TelaPrincipalUI(
            RegraPrecoService regraPrecoService,
            ContratoAluguelService contratoAluguelService,
            ClienteService clienteService,
            ArtistaService artistaService,
            IngressoService ingressoService,
            RelatorioFinanceiroService relatorioFinanceiroService) {

        this.regraPrecoService = regraPrecoService;
        this.contratoAluguelService = contratoAluguelService;
        this.clienteService = clienteService;
        this.artistaService = artistaService;
        this.ingressoService = ingressoService;
        this.relatorioFinanceiroService = relatorioFinanceiroService;

        inicializarComponentes();

    }

    private void inicializarComponentes() {
        setTitle("Gerenciamento de Teatro - Painel Principal");
        setSize(1024, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Barra de Menus Superior
        JMenuBar menuBar = new JMenuBar();

        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem itemRegrasPreco = new JMenuItem("Regras de Preço");
        JMenuItem itemArtistas = new JMenuItem("Artistas");
        JMenuItem itemClientes = new JMenuItem("Clientes");
        menuCadastros.add(itemRegrasPreco);
        menuCadastros.add(itemArtistas);
        menuCadastros.add(itemClientes);

        JMenu menuOperacoes = new JMenu("Operações");
        JMenuItem itemPropostas = new JMenuItem("Propostas & Contratos");
        JMenuItem itemIngressos = new JMenuItem("Venda de Ingressos");
        menuOperacoes.add(itemPropostas);
        menuOperacoes.add(itemIngressos);

        JMenu menuRelatorios = new JMenu("Relatórios");
        JMenuItem itemListaPresenca = new JMenuItem("Lista de Presença");
        JMenuItem itemRelatorioPeca = new JMenuItem("Relatório Financeiro por Peça");
        JMenuItem itemRelatorioTeatro = new JMenuItem("Relatório Geral do Teatro");
        menuRelatorios.add(itemListaPresenca);
        menuRelatorios.add(itemRelatorioPeca);
        menuRelatorios.add(itemRelatorioTeatro);

        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemSair = new JMenuItem("Sair / Logout");
        itemSair.addActionListener(e -> this.dispose());
        menuSistema.add(itemSair);

        menuBar.add(menuCadastros);
        menuBar.add(menuOperacoes);
        menuBar.add(menuRelatorios);
        menuBar.add(menuSistema);
        setJMenuBar(menuBar);

        // 2. Painel Central Tabulado (Substituição dos placeholders pelos componentes reais)
        JTabbedPane tabbedPane = new JTabbedPane();

        // Aba 1: Regras de Preço (Req. 4, 5 e 6)
        PainelRegrasPrecoUI painelRegras = new PainelRegrasPrecoUI(regraPrecoService);
        tabbedPane.addTab("Regras de Preço", painelRegras);

        // Aba 2: Propostas & Contratos (Req. 7, 9, 10, 14 e 15)
        PainelPropostasContratosUI painelContratos = new PainelPropostasContratosUI(contratoAluguelService, artistaService);
        tabbedPane.addTab("Propostas & Contratos", painelContratos);

        // Aba 3: Venda de Ingressos (Req. 11)
        PainelVendaIngressosUI painelIngressos = new PainelVendaIngressosUI(clienteService, contratoAluguelService, ingressoService);
        tabbedPane.addTab("Venda de Ingressos", painelIngressos);

        // Aba 4: Relatórios & Presença (Req. 12, 13 e 16)
        PainelRelatoriosUI painelRelatorios = new PainelRelatoriosUI(ingressoService, relatorioFinanceiroService, contratoAluguelService);
        tabbedPane.addTab("Relatórios & Presença", painelRelatorios);

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Barra de Status Rodapé
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(new JLabel("Sessão iniciada como Administrador | BD2 & POO Project"));
        add(statusBar, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                br.com.gerenciamentoteatro.repository.JPAUtil.fecharFactory();
            }
        });
    }
}
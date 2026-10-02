package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.service.ContratoAluguelService;
import br.com.gerenciamentoteatro.service.IngressoService;
import br.com.gerenciamentoteatro.service.RelatorioFinanceiroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PainelRelatoriosUI extends JPanel {

    private final IngressoService ingressoService;
    private final RelatorioFinanceiroService relatorioService;
    private final ContratoAluguelService contratoService;

    public PainelRelatoriosUI(IngressoService ingressoService,
                              RelatorioFinanceiroService relatorioService,
                              ContratoAluguelService contratoService) {
        this.ingressoService = ingressoService;
        this.relatorioService = relatorioService;
        this.contratoService = contratoService;

        setLayout(new BorderLayout());
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JTabbedPane tabbedPaneRelatorios = new JTabbedPane();

        tabbedPaneRelatorios.addTab("Lista de Presença (Req. 12)", criarPainelListaPresenca());
        tabbedPaneRelatorios.addTab("Relatório por Peça (Req. 13)", new JPanel());
        tabbedPaneRelatorios.addTab("Relatório Geral Teatro (Req. 16)", new JPanel());

        add(tabbedPaneRelatorios, BorderLayout.CENTER);
    }

    private JPanel criarPainelListaPresenca() {
        JPanel painel = new JPanel(new BorderLayout());

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<ContratoAluguel> cbContratos = new JComboBox<>();
        carregarContratosParaCombo(cbContratos);

        JTextField txtDataFiltro = new JTextField(10);
        JButton btnGerar = new JButton("Gerar Lista");

        painelFiltro.add(new JLabel("Peça/Contrato:"));
        painelFiltro.add(cbContratos);
        painelFiltro.add(new JLabel("Filtrar Data (Opcional):"));
        painelFiltro.add(txtDataFiltro);
        painelFiltro.add(btnGerar);

        painel.add(painelFiltro, BorderLayout.NORTH);

        String[] colunas = {"Nome do Cliente", "CPF", "E-mail", "Qtd Ingressos Adquiridos"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(model);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;
    }

    private void carregarContratosParaCombo(JComboBox<ContratoAluguel> combo) {
        combo.removeAllItems();
        for (ContratoAluguel c : contratoService.listarPropostas()) {
            combo.addItem(c);
        }
    }
}
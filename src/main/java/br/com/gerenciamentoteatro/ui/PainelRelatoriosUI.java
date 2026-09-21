package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.model.*;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.service.ContratoAluguelService;
import br.com.gerenciamentoteatro.service.IngressoService;
import br.com.gerenciamentoteatro.service.RelatorioFinanceiroService;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PainelRelatoriosUI extends JPanel {

    private final IngressoService ingressoService;
    private final RelatorioFinanceiroService relatorioService;
    private final ContratoAluguelService contratoService;

    // ADICIONADO: Promovidos para atributos para permitir atualização dinâmica
    private JComboBox<ContratoAluguel> cbContratosPresenca;
    private JComboBox<ContratoAluguel> cbContratosRelatorio;

    public PainelRelatoriosUI(IngressoService ingressoService,
                              RelatorioFinanceiroService relatorioService,
                              ContratoAluguelService contratoService) {
        this.ingressoService = ingressoService;
        this.relatorioService = relatorioService;
        this.contratoService = contratoService;

        setLayout(new BorderLayout());
        inicializarComponentes();

        // ADICIONADO: Atualiza os combos sempre que a aba é aberta
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (cbContratosPresenca != null) carregarContratosParaCombo(cbContratosPresenca);
                if (cbContratosRelatorio != null) carregarContratosParaCombo(cbContratosRelatorio);
            }
            @Override public void ancestorRemoved(AncestorEvent event) {}
            @Override public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void inicializarComponentes() {
        JTabbedPane tabbedPaneRelatorios = new JTabbedPane();

        tabbedPaneRelatorios.addTab("Lista de Presença (Req. 12)", criarPainelListaPresenca());
        tabbedPaneRelatorios.addTab("Relatório por Peça (Req. 13)", criarPainelRelatorioPeca());
        tabbedPaneRelatorios.addTab("Relatório Geral Teatro (Req. 16)", criarPainelRelatorioTeatro());

        add(tabbedPaneRelatorios, BorderLayout.CENTER);
    }

    private JPanel criarPainelListaPresenca() {
        JPanel painel = new JPanel(new BorderLayout());

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbContratosPresenca = new JComboBox<>();
        carregarContratosParaCombo(cbContratosPresenca);

        JTextField txtDataFiltro = new JTextField(10);
        JButton btnGerar = new JButton("Gerar Lista");

        painelFiltro.add(new JLabel("Peça/Contrato:"));
        painelFiltro.add(cbContratosPresenca);
        painelFiltro.add(new JLabel("Filtrar Data (Opcional):"));
        painelFiltro.add(txtDataFiltro);
        painelFiltro.add(btnGerar);

        painel.add(painelFiltro, BorderLayout.NORTH);

        String[] colunas = {"Nome do Cliente", "CPF", "E-mail", "Qtd Ingressos Adquiridos"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(model);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnGerar.addActionListener(e -> {
            ContratoAluguel contrato = (ContratoAluguel) cbContratosPresenca.getSelectedItem();
            if (contrato == null) {
                JOptionPane.showMessageDialog(this, "Selecione um contrato.");
                return;
            }

            Long pecaId = contrato.getProposta().getPeca().getId();
            LocalDate dataFiltro = null;
            if (!txtDataFiltro.getText().isBlank()) {
                try {
                    dataFiltro = LocalDate.parse(txtDataFiltro.getText().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Data inválida. Use YYYY-MM-DD.");
                    return;
                }
            }

            List<ItemListaPresenca> lista = ingressoService.gerarListaPresenca(pecaId, dataFiltro);
            model.setRowCount(0);
            for (ItemListaPresenca item : lista) {
                model.addRow(new Object[]{item.getNomeCliente(), item.getCpfCliente(), item.getEmailCliente(), item.getTotalIngressosComprados()});
            }
        });

        return painel;
    }

    private JPanel criarPainelRelatorioPeca() {
        JPanel painel = new JPanel(new BorderLayout());

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbContratosRelatorio = new JComboBox<>();
        carregarContratosParaCombo(cbContratosRelatorio);

        JTextField txtPrecoTicket = new JTextField("50.00", 8);
        JButton btnCalcular = new JButton("Gerar Relatório");

        painelFiltro.add(new JLabel("Peça/Contrato:"));
        painelFiltro.add(cbContratosRelatorio);
        painelFiltro.add(new JLabel("Preço Ticket (R$):"));
        painelFiltro.add(txtPrecoTicket);
        painelFiltro.add(btnCalcular);

        painel.add(painelFiltro, BorderLayout.NORTH);

        JTextArea txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        painel.add(new JScrollPane(txtResultado), BorderLayout.CENTER);

        btnCalcular.addActionListener(e -> {
            ContratoAluguel c = (ContratoAluguel) cbContratosRelatorio.getSelectedItem();
            if (c == null) return;

            try {
                BigDecimal preco = new BigDecimal(txtPrecoTicket.getText().replace(",", "."));
                RelatorioFinanceiroPeca r = relatorioService.gerarRelatorioPeca(c.getId(), preco);

                StringBuilder sb = new StringBuilder();
                sb.append("===== RELATÓRIO FINANCEIRO DA PEÇA =====\n\n");
                sb.append("Peça: ").append(r.getNomePeca()).append("\n");
                sb.append("Artista Locatário: ").append(r.getNomeArtista()).append("\n");
                sb.append("------------------------------------------\n");
                sb.append("Total Arrecadado:       R$ ").append(r.getArrecadacaoIngressos()).append("\n");
                sb.append("Custo Total do Aluguel: R$ ").append(r.getCustoAluguel()).append("\n");
                sb.append("------------------------------------------\n");
                sb.append("VALOR LÍQUIDO REPASSE:  R$ ").append(r.getValorLiquidoRepasse()).append("\n");

                txtResultado.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage());
            }
        });

        return painel;
    }

    private JPanel criarPainelRelatorioTeatro() {
        JPanel painel = new JPanel(new BorderLayout());
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtDataInicio = new JTextField(8);
        JTextField txtDataFim = new JTextField(8);
        JTextField txtPrecoTicket = new JTextField("50.00", 6);
        JButton btnGerar = new JButton("Calcular Período");

        painelFiltro.add(new JLabel("Início (YYYY-MM-DD):"));
        painelFiltro.add(txtDataInicio);
        painelFiltro.add(new JLabel("Fim (YYYY-MM-DD):"));
        painelFiltro.add(txtDataFim);
        painelFiltro.add(new JLabel("Ticket Médio:"));
        painelFiltro.add(txtPrecoTicket);
        painelFiltro.add(btnGerar);

        painel.add(painelFiltro, BorderLayout.NORTH);

        JTextArea txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        painel.add(new JScrollPane(txtResultado), BorderLayout.CENTER);

        btnGerar.addActionListener(e -> {
            try {
                LocalDate dInicio = LocalDate.parse(txtDataInicio.getText().trim());
                LocalDate dFim = LocalDate.parse(txtDataFim.getText().trim());
                BigDecimal preco = new BigDecimal(txtPrecoTicket.getText().replace(",", "."));

                RelatorioFinanceiroTeatro r = relatorioService.gerarRelatorioTeatro(dInicio, dFim, preco);

                StringBuilder sb = new StringBuilder();
                sb.append("===== RELATÓRIO GERAL DO TEATRO =====\n\n");
                sb.append("Período: ").append(r.getDataInicio()).append(" até ").append(r.getDataFim()).append("\n");
                sb.append("--------------------------------------------------\n");
                sb.append("Receita de Aluguéis: R$ ").append(r.getTotalContratosAluguel()).append("\n");
                sb.append("Receita de Ingressos:R$ ").append(r.getTotalVendaIngressos()).append("\n");
                sb.append("--------------------------------------------------\n");
                sb.append("RECEITA BRUTA TOTAL: R$ ").append(r.getReceitaTotalGeral()).append("\n");

                txtResultado.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Preencha as datas corretamente (YYYY-MM-DD).");
            }
        });

        return painel;
    }

    private void carregarContratosParaCombo(JComboBox<ContratoAluguel> cb) {
        cb.removeAllItems();
        for (ContratoAluguel c : contratoService.listarPropostas()) {
            if (c.getStatus() == StatusContrato.CONTRATADO || c.getStatus() == StatusContrato.CONTRATADO_COM_ALTERACAO || c.getStatus() == StatusContrato.ENCERRADO) {
                cb.addItem(c);
            }
        }
    }
}
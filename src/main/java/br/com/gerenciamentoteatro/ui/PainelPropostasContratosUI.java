package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.service.ArtistaService;
import br.com.gerenciamentoteatro.service.ContratoAluguelService;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PainelPropostasContratosUI extends JPanel {

    private final ContratoAluguelService contratoService;
    private final ArtistaService artistaService;

    private JTable tabelaContratos;
    private DefaultTableModel tableModel;

    private JTextField txtFiltroTexto;
    private JComboBox<Object> cbFiltroStatus;

    public PainelPropostasContratosUI(ContratoAluguelService contratoService, ArtistaService artistaService) {
        this.contratoService = contratoService;
        this.artistaService = artistaService;
        setLayout(new BorderLayout());
        inicializarComponentes();
        atualizarTabela();

        // ADICIONADO: Atualiza a tabela sempre que o painel fica visível (troca de aba)
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                atualizarTabela();
            }
            @Override public void ancestorRemoved(AncestorEvent event) {}
            @Override public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void inicializarComponentes() {
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Busca"));

        painelFiltros.add(new JLabel("Status:"));
        cbFiltroStatus = new JComboBox<>();
        cbFiltroStatus.addItem("TODOS");
        for (StatusContrato s : StatusContrato.values()) {
            cbFiltroStatus.addItem(s);
        }
        painelFiltros.add(cbFiltroStatus);

        painelFiltros.add(new JLabel("Artista / Peça:"));
        txtFiltroTexto = new JTextField(15);
        painelFiltros.add(txtFiltroTexto);

        JButton btnFiltrar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar");
        painelFiltros.add(btnFiltrar);
        painelFiltros.add(btnLimpar);

        add(painelFiltros, BorderLayout.NORTH);

        String[] colunas = {"ID", "Peça", "Artista", "Data Início", "Data Fim", "Valor Total", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaContratos = new JTable(tableModel);
        add(new JScrollPane(tabelaContratos), BorderLayout.CENTER);

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovaProposta = new JButton("Nova Proposta");
        JButton btnPromover = new JButton("Promover a Contratado");
        JButton btnEstender = new JButton("Estender Contrato");
        JButton btnEncerrar = new JButton("Encerrar Contrato");

        painelAcoes.add(btnNovaProposta);
        painelAcoes.add(btnPromover);
        painelAcoes.add(btnEstender);
        painelAcoes.add(btnEncerrar);
        add(painelAcoes, BorderLayout.SOUTH);

        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnLimpar.addActionListener(e -> {
            cbFiltroStatus.setSelectedIndex(0);
            txtFiltroTexto.setText("");
            atualizarTabela();
        });
        btnNovaProposta.addActionListener(e -> acaoNovaProposta());
        btnPromover.addActionListener(e -> acaoPromoverContrato());
        btnEstender.addActionListener(e -> acaoEstenderContrato());
        btnEncerrar.addActionListener(e -> acaoEncerrarContrato());
    }

    public void atualizarTabela() {
        carregarDados(contratoService.listarPropostas());
    }

    private void carregarDados(List<ContratoAluguel> lista) {
        tableModel.setRowCount(0);
        for (ContratoAluguel c : lista) {
            Object[] linha = {
                    c.getId(),
                    c.getProposta() != null && c.getProposta().getPeca() != null ? c.getProposta().getPeca().getNome() : "N/A",
                    c.getProposta() != null && c.getProposta().getArtista() != null ? c.getProposta().getArtista().getNomeCompleto() : "N/A",
                    c.getDataInicio(),
                    c.getDataFim(),
                    "R$ " + c.getValorTotal(),
                    c.getStatus()
            };
            tableModel.addRow(linha);
        }
    }

    private void aplicarFiltros() {
        Object statusSelecionado = cbFiltroStatus.getSelectedItem();
        String termoBusca = txtFiltroTexto.getText().trim().toLowerCase();

        List<ContratoAluguel> filtrados = contratoService.listarPropostas().stream()
                .filter(c -> {
                    boolean bateStatus = statusSelecionado.equals("TODOS") || c.getStatus().equals(statusSelecionado);
                    String nomePeca = c.getProposta() != null && c.getProposta().getPeca() != null ? c.getProposta().getPeca().getNome().toLowerCase() : "";
                    String nomeArtista = c.getProposta() != null && c.getProposta().getArtista() != null ? c.getProposta().getArtista().getNomeCompleto().toLowerCase() : "";
                    boolean bateTexto = termoBusca.isEmpty() || nomePeca.contains(termoBusca) || nomeArtista.contains(termoBusca);
                    return bateStatus && bateTexto;
                })
                .toList();

        carregarDados(filtrados);
    }

    private void acaoNovaProposta() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioPropostaUI dialog = new FormularioPropostaUI((Frame) parent, artistaService, contratoService);
        dialog.setVisible(true);
        atualizarTabela();
    }

    private void acaoPromoverContrato() {
        int linha = tabelaContratos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contrato/proposta na tabela.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(linha, 0);
        ContratoAluguel contrato = contratoService.listarPropostas().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);

        if (contrato != null) {
            contrato.setStatus(StatusContrato.CONTRATADO);
            try {
                // Aqui o service deve persistir o status no banco de dados H2
                // contratoService.atualizar(contrato);
                JOptionPane.showMessageDialog(this, "Proposta promovida a CONTRATADO com sucesso!");
                atualizarTabela();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao promover: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoEstenderContrato() {
        int linha = tabelaContratos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contrato na tabela.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(linha, 0);
        String novaDataStr = JOptionPane.showInputDialog(this, "Informe a nova data final (YYYY-MM-DD):");

        if (novaDataStr != null && !novaDataStr.isBlank()) {
            try {
                LocalDate novaData = LocalDate.parse(novaDataStr.trim());
                contratoService.estenderContrato(id, novaData);
                JOptionPane.showMessageDialog(this, "Contrato estendido com sucesso!");
                atualizarTabela();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao estender: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void acaoEncerrarContrato() {
        int linha = tabelaContratos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contrato na tabela.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(linha, 0);
        String dataEncerramentoStr = JOptionPane.showInputDialog(this, "Informe a data efetiva de encerramento (YYYY-MM-DD):");

        if (dataEncerramentoStr != null && !dataEncerramentoStr.isBlank()) {
            try {
                LocalDate dataEfetiva = LocalDate.parse(dataEncerramentoStr.trim());
                contratoService.encerrarContrato(id, dataEfetiva, java.math.BigDecimal.ZERO);
                JOptionPane.showMessageDialog(this, "Contrato encerrado!");
                atualizarTabela();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao encerrar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.model.Cliente;
import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.model.Ingresso;
import br.com.gerenciamentoteatro.model.Peca;
import br.com.gerenciamentoteatro.model.enums.StatusContrato;
import br.com.gerenciamentoteatro.service.ClienteService;
import br.com.gerenciamentoteatro.service.ContratoAluguelService;
import br.com.gerenciamentoteatro.service.IngressoService;
import br.com.gerenciamentoteatro.service.PDFGeneratorService;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PainelVendaIngressosUI extends JPanel {

    private final ClienteService clienteService;
    private final ContratoAluguelService contratoService;
    private final IngressoService ingressoService;

    private JTextField txtCpf;
    private JTextField txtNome;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtGenero;
    private JTextField txtDataNascimento;

    private JComboBox<ContratoAluguel> cbPecasContratadas;
    private JTextField txtDataExibicao;
    private JSpinner spinnerQuantidade;

    public PainelVendaIngressosUI(ClienteService clienteService,
                                  ContratoAluguelService contratoService,
                                  IngressoService ingressoService) {
        this.clienteService = clienteService;
        this.contratoService = contratoService;
        this.ingressoService = ingressoService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();

        // Recarrega as peças contratadas dinamicamente ao abrir a aba
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                carregarPecasContratadas();
            }
            @Override public void ancestorRemoved(AncestorEvent event) {}
            @Override public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void inicializarComponentes() {
        JPanel painelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblClienteHeader = new JLabel("1. Identificação do Cliente Espectador");
        lblClienteHeader.setFont(new Font("Arial", Font.BOLD, 14));
        painelForm.add(lblClienteHeader, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; painelForm.add(new JLabel("CPF:*"), gbc);
        txtCpf = new JTextField(15);
        gbc.gridx = 1; painelForm.add(txtCpf, gbc);

        JButton btnBuscarCpf = new JButton("Buscar CPF");
        gbc.gridx = 2; painelForm.add(btnBuscarCpf, gbc);

        gbc.gridy = 2; gbc.gridx = 0; painelForm.add(new JLabel("Nome Completo:*"), gbc);
        txtNome = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; painelForm.add(txtNome, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0; painelForm.add(new JLabel("Telefone:"), gbc);
        txtTelefone = new JTextField(15);
        gbc.gridx = 1; painelForm.add(txtTelefone, gbc);

        gbc.gridy = 4; gbc.gridx = 0; painelForm.add(new JLabel("E-mail:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 1; painelForm.add(txtEmail, gbc);

        gbc.gridy = 5; gbc.gridx = 0; painelForm.add(new JLabel("Gênero:"), gbc);
        txtGenero = new JTextField(10);
        gbc.gridx = 1; painelForm.add(txtGenero, gbc);

        gbc.gridy = 6; gbc.gridx = 0; painelForm.add(new JLabel("Data Nasc. (YYYY-MM-DD):"), gbc);
        txtDataNascimento = new JTextField(10);
        gbc.gridx = 1; painelForm.add(txtDataNascimento, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JLabel lblVendaHeader = new JLabel("2. Seleção da Peça e Ingressos");
        lblVendaHeader.setFont(new Font("Arial", Font.BOLD, 14));
        painelForm.add(lblVendaHeader, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 8; gbc.gridx = 0; painelForm.add(new JLabel("Peça Contratada:*"), gbc);
        cbPecasContratadas = new JComboBox<>();
        carregarPecasContratadas();
        gbc.gridx = 1; gbc.gridwidth = 2; painelForm.add(cbPecasContratadas, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 9; gbc.gridx = 0; painelForm.add(new JLabel("Data Exibição (YYYY-MM-DD):*"), gbc);
        txtDataExibicao = new JTextField(10);
        gbc.gridx = 1; painelForm.add(txtDataExibicao, gbc);

        gbc.gridy = 10; gbc.gridx = 0; painelForm.add(new JLabel("Quantidade:*"), gbc);
        spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        gbc.gridx = 1; painelForm.add(spinnerQuantidade, gbc);

        add(new JScrollPane(painelForm), BorderLayout.CENTER);

        JButton btnFinalizarVenda = new JButton("Finalizar Venda de Ingresso");
        btnFinalizarVenda.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnFinalizarVenda, BorderLayout.SOUTH);

        btnBuscarCpf.addActionListener(e -> buscarClientePorCpf());
        btnFinalizarVenda.addActionListener(e -> finalizarVenda());
        cbPecasContratadas.addActionListener(e -> sugerirDataExibicao());
        sugerirDataExibicao();
    }

    private void carregarPecasContratadas() {
        cbPecasContratadas.removeAllItems();
        List<ContratoAluguel> ativos = contratoService.listarPropostas().stream()
                .filter(c -> c.getStatus() == StatusContrato.CONTRATADO || c.getStatus() == StatusContrato.CONTRATADO_COM_ALTERACAO)
                .toList();
        for (ContratoAluguel c : ativos) {
            cbPecasContratadas.addItem(c);
        }
    }

    private void sugerirDataExibicao() {
        ContratoAluguel contrato = (ContratoAluguel) cbPecasContratadas.getSelectedItem();
        if (contrato != null && contrato.getDataInicio() != null) {
            txtDataExibicao.setText(contrato.getDataInicio().toString());
        }
    }

    private void buscarClientePorCpf() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o CPF para busca.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Cliente> opt = clienteService.buscarPorCpf(cpf);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            txtNome.setText(c.getNomeCompleto());
            txtTelefone.setText(c.getTelefone());
            txtEmail.setText(c.getEmail());
            txtGenero.setText(c.getGenero());
            txtDataNascimento.setText(c.getDataNascimento() != null ? c.getDataNascimento().toString() : "");
            JOptionPane.showMessageDialog(this, "Cliente encontrado! Dados preenchidos.");
        } else {
            JOptionPane.showMessageDialog(this, "CPF não cadastrado. Preencha os campos para novo cadastro.");
        }
    }

    private void finalizarVenda() {
        try {
            String cpf = txtCpf.getText().trim();
            String nome = txtNome.getText().trim();
            ContratoAluguel contratoSelecionado = (ContratoAluguel) cbPecasContratadas.getSelectedItem();
            String dataStr = txtDataExibicao.getText().trim();
            int quantidade = (Integer) spinnerQuantidade.getValue();

            if (cpf.isBlank() || nome.isBlank() || contratoSelecionado == null || dataStr.isBlank()) {
                throw new IllegalArgumentException("Preencha todos os campos obrigatórios (*).");
            }

            LocalDate dataNasc = txtDataNascimento.getText().isBlank() ? null : LocalDate.parse(txtDataNascimento.getText().trim());
            Cliente cliente = new Cliente(cpf, nome, txtTelefone.getText().trim(), txtEmail.getText().trim(), txtGenero.getText().trim(), dataNasc);
            cliente = clienteService.cadastrarOuObter(cliente);

            Peca peca = contratoSelecionado.getProposta().getPeca();
            LocalDate dataExibicao = LocalDate.parse(dataStr);

            Ingresso ingresso = new Ingresso(cliente, peca, dataExibicao, quantidade);

            // Correção: registrarVenda é void, não atribuímos a nenhuma variável
            ingressoService.registrarVenda(ingresso);

            String caminhoPdf = "./Ingresso_" + System.currentTimeMillis() + ".pdf";
            try {
                PDFGeneratorService.gerarIngressoPDF(ingresso, caminhoPdf);
                JOptionPane.showMessageDialog(this, "Venda realizada!\nIngresso gerado: " + caminhoPdf);
            } catch (Exception pdfEx) {
                JOptionPane.showMessageDialog(this, "Venda salva, mas erro ao gerar PDF.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            limparFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        txtCpf.setText("");
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtGenero.setText("");
        txtDataNascimento.setText("");
        txtDataExibicao.setText("");
        spinnerQuantidade.setValue(1);
    }
}
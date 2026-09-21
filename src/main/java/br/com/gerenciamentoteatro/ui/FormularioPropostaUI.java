package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.model.*;
import br.com.gerenciamentoteatro.model.enums.Turno;
import br.com.gerenciamentoteatro.service.ArtistaService;
import br.com.gerenciamentoteatro.service.ContratoAluguelService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class FormularioPropostaUI extends JDialog {

    private final ArtistaService artistaService;
    private final ContratoAluguelService contratoService;

    // Removidos txtGenero e txtDataNasc
    private JTextField txtCpfArtista, txtNomeArtista, txtTelefone, txtEmail;
    private JTextField txtNomePeca, txtDataInicio, txtDataFim, txtHoraInicio, txtHoraFim;
    private JComboBox<Turno> cbTurno;
    private JTextField txtValorTotalCalculado;

    public FormularioPropostaUI(Frame parent, ArtistaService artistaService, ContratoAluguelService contratoService) {
        super(parent, "Nova Proposta de Aluguel", true);
        this.artistaService = artistaService;
        this.contratoService = contratoService;

        setSize(550, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel painelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Seção 1: Dados do Artista Locatário (Req. 7)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JLabel lblArtista = new JLabel("1. Dados do Artista Locatário");
        lblArtista.setFont(new Font("Arial", Font.BOLD, 13));
        painelForm.add(lblArtista, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; painelForm.add(new JLabel("CPF:*"), gbc);
        txtCpfArtista = new JTextField(15);
        gbc.gridx = 1; painelForm.add(txtCpfArtista, gbc);

        JButton btnBuscarCpf = new JButton("Buscar CPF");
        gbc.gridx = 2; painelForm.add(btnBuscarCpf, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 1; painelForm.add(new JLabel("Nome Completo:*"), gbc);
        txtNomeArtista = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; painelForm.add(txtNomeArtista, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0; painelForm.add(new JLabel("Telefone:"), gbc);
        txtTelefone = new JTextField(12);
        gbc.gridx = 1; painelForm.add(txtTelefone, gbc);

        gbc.gridy = 4; gbc.gridx = 0; painelForm.add(new JLabel("E-mail:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 1; painelForm.add(txtEmail, gbc);

        // Seção 2: Dados da Peça & Aluguel (Req. 7)
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        JLabel lblPeca = new JLabel("2. Dados da Peça e Período");
        lblPeca.setFont(new Font("Arial", Font.BOLD, 13));
        painelForm.add(lblPeca, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 6; gbc.gridx = 0; painelForm.add(new JLabel("Nome da Peça:*"), gbc);
        txtNomePeca = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; painelForm.add(txtNomePeca, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 7; gbc.gridx = 0; painelForm.add(new JLabel("Data Início (YYYY-MM-DD):*"), gbc);
        txtDataInicio = new JTextField(10);
        gbc.gridx = 1; painelForm.add(txtDataInicio, gbc);

        gbc.gridy = 8; gbc.gridx = 0; painelForm.add(new JLabel("Data Fim (YYYY-MM-DD):*"), gbc);
        txtDataFim = new JTextField(10);
        gbc.gridx = 1; painelForm.add(txtDataFim, gbc);

        gbc.gridy = 9; gbc.gridx = 0; painelForm.add(new JLabel("Início Exibição (HH:mm):*"), gbc);
        txtHoraInicio = new JTextField(10);
        gbc.gridx = 1; painelForm.add(txtHoraInicio, gbc);

        gbc.gridy = 10; gbc.gridx = 0; painelForm.add(new JLabel("Fim Exibição (HH:mm):*"), gbc);
        txtHoraFim = new JTextField(10);
        gbc.gridx = 1; painelForm.add(txtHoraFim, gbc);

        gbc.gridy = 11; gbc.gridx = 0; painelForm.add(new JLabel("Turno:*"), gbc);
        cbTurno = new JComboBox<>(Turno.values());
        gbc.gridx = 1; painelForm.add(cbTurno, gbc);

        add(new JScrollPane(painelForm), BorderLayout.CENTER);

        // Painel de Ações inferiores
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar Proposta");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ações dos Botões
        btnBuscarCpf.addActionListener(e -> buscarArtistaPorCpf());
        btnSalvar.addActionListener(e -> salvarProposta());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void buscarArtistaPorCpf() {
        String cpf = txtCpfArtista.getText().trim();
        if (cpf.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o CPF para busca.");
            return;
        }
        Optional<Artista> opt = artistaService.buscarPorCpf(cpf);
        if (opt.isPresent()) {
            Artista a = opt.get();
            txtNomeArtista.setText(a.getNomeCompleto());
            txtTelefone.setText(a.getTelefone());
            txtEmail.setText(a.getEmail());
            JOptionPane.showMessageDialog(this, "Artista encontrado e preenchido!");
        } else {
            JOptionPane.showMessageDialog(this, "Artista não cadastrado. Preencha os campos para novo cadastro.");
        }
    }

    private void salvarProposta() {
        try {
            String cpf = txtCpfArtista.getText().trim();
            String nomeArtista = txtNomeArtista.getText().trim();
            if (cpf.isBlank() || nomeArtista.isBlank()) {
                throw new IllegalArgumentException("Preencha os campos obrigatórios do artista.");
            }

            // Removido o mock "Não Informado" e "LocalDate.now()"
            Artista artista = new Artista(cpf, nomeArtista, txtTelefone.getText().trim(), txtEmail.getText().trim());
            artista = artistaService.cadastrarOuObter(artista);

            String nomePeca = txtNomePeca.getText().trim();
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText().trim());
            LocalDate fim = LocalDate.parse(txtDataFim.getText().trim());
            LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim());
            LocalTime horaFim = LocalTime.parse(txtHoraFim.getText().trim());
            Turno turno = (Turno) cbTurno.getSelectedItem();

            Peca peca = new Peca(nomePeca, inicio, fim, horaInicio, horaFim, turno);
            PropostaAluguel proposta = new PropostaAluguel(LocalDate.now(), artista, peca);

            ContratoAluguel contrato = new ContratoAluguel(proposta, inicio, fim, BigDecimal.ZERO);
            contratoService.cadastrarProposta(contrato);

            JOptionPane.showMessageDialog(this, "Proposta cadastrada com sucesso!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar proposta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
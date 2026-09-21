package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.service.*;

import javax.swing.*;
import java.awt.*;

public class TelaLoginUI extends JFrame {

    private final AdministradorService adminService;
    private final RegraPrecoService regraPrecoService;
    private final ContratoAluguelService contratoAluguelService;
    private final ClienteService clienteService;
    private final ArtistaService artistaService;
    private final IngressoService ingressoService;
    private final RelatorioFinanceiroService relatorioFinanceiroService;

    private JLabel lblTitulo;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnAcaoPrincipal;
    private JButton btnEsqueciSenha;

    public TelaLoginUI(
            AdministradorService adminService,
            RegraPrecoService regraPrecoService,
            ContratoAluguelService contratoAluguelService,
            ClienteService clienteService,
            ArtistaService artistaService,
            IngressoService ingressoService,
            RelatorioFinanceiroService relatorioFinanceiroService) {

        this.adminService = adminService;
        this.regraPrecoService = regraPrecoService;
        this.contratoAluguelService = contratoAluguelService;
        this.clienteService = clienteService;
        this.artistaService = artistaService;
        this.ingressoService = ingressoService;
        this.relatorioFinanceiroService = relatorioFinanceiroService;

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Gerenciamento de Teatro - Autenticação");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Painel Principal com GridBagLayout para alinhamento limpo
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        boolean existeAdmin = adminService.existeAdministrador();

        // Título dinâmico (Requisito 1)
        lblTitulo = new JLabel(existeAdmin ? "Acesso ao Sistema" : "Cadastro do Administrador", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painel.add(lblTitulo, gbc);

        // Rótulo e Campo: E-mail
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        painel.add(new JLabel("E-mail:"), gbc);

        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        painel.add(txtEmail, gbc);

        // Rótulo e Campo: Senha
        gbc.gridy = 2;
        gbc.gridx = 0;
        painel.add(new JLabel("Senha:"), gbc);

        txtSenha = new JPasswordField(20);
        gbc.gridx = 1;
        painel.add(txtSenha, gbc);

        // Botão Principal (Login ou Cadastrar)
        btnAcaoPrincipal = new JButton(existeAdmin ? "Entrar" : "Cadastrar Administrador");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        painel.add(btnAcaoPrincipal, gbc);

        // Botão Esqueci Senha (visível apenas se já houver admin - Requisito 3)
        if (existeAdmin) {
            btnEsqueciSenha = new JButton("Esqueci minha senha");
            gbc.gridy = 4;
            painel.add(btnEsqueciSenha, gbc);

            btnEsqueciSenha.addActionListener(e -> acaoEsqueciSenha());
        }

        // Listener do Botão Principal
        btnAcaoPrincipal.addActionListener(e -> {
            if (adminService.existeAdministrador()) {
                acaoLogin();
            } else {
                acaoCadastro();
            }
        });

        add(painel);
    }

    private void acaoLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());

        try {
            boolean sucesso = adminService.realizarLogin(email, senha);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Login realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Abre a TelaPrincipalUI repassando todas as dependências
                SwingUtilities.invokeLater(() -> {
                    TelaPrincipalUI telaPrincipal = new TelaPrincipalUI(
                            regraPrecoService,
                            contratoAluguelService,
                            clienteService,
                            artistaService,
                            ingressoService,
                            relatorioFinanceiroService
                    );
                    telaPrincipal.setVisible(true);
                });

                this.dispose(); // Fecha o formulário de login
            } else {
                JOptionPane.showMessageDialog(this, "Credenciais inválidas.", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoCadastro() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());

        try {
            adminService.cadastrarAdministrador(email, senha);
            JOptionPane.showMessageDialog(this, "Administrador cadastrado com sucesso! Efetue o login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Recarrega a tela no modo Login repassando todos os serviços
            SwingUtilities.invokeLater(() -> new TelaLoginUI(
                    adminService,
                    regraPrecoService,
                    contratoAluguelService,
                    clienteService,
                    artistaService,
                    ingressoService,
                    relatorioFinanceiroService
            ).setVisible(true));

            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoEsqueciSenha() {
        String email = JOptionPane.showInputDialog(this, "Informe seu e-mail para receber o código de recuperação:");
        if (email != null && !email.isBlank()) {
            try {
                String codigo = adminService.gerarCodigoRecuperacao(email);
                JOptionPane.showMessageDialog(this, "Código enviado para o e-mail (verifique o console).", "Código Enviado", JOptionPane.INFORMATION_MESSAGE);

                String codigoInformado = JOptionPane.showInputDialog(this, "Digite o código recebido:");
                String novaSenha = JOptionPane.showInputDialog(this, "Digite a nova senha:");

                if (codigoInformado != null && novaSenha != null) {
                    adminService.alterarSenhaComCodigo(email, codigoInformado, novaSenha);
                    JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
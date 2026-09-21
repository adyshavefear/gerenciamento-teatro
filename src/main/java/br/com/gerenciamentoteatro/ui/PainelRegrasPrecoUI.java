package br.com.gerenciamentoteatro.ui;

import br.com.gerenciamentoteatro.model.RegraPreco;
import br.com.gerenciamentoteatro.model.enums.Turno;
import br.com.gerenciamentoteatro.service.RegraPrecoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;

public class PainelRegrasPrecoUI extends JPanel {

    private final RegraPrecoService regraPrecoService;
    private JTable tabelaRegras;
    private DefaultTableModel tableModel;

    public PainelRegrasPrecoUI(RegraPrecoService regraPrecoService) {
        this.regraPrecoService = regraPrecoService;
        setLayout(new BorderLayout());
        inicializarComponentes();
        atualizarTabela();
    }

    private void inicializarComponentes() {
        String[] colunas = {"ID", "Valor/Hora", "Dia da Semana", "Turno", "Mês", "Ano", "Horário Início", "Horário Fim"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaRegras = new JTable(tableModel);
        add(new JScrollPane(tabelaRegras), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovaRegra = new JButton("Nova Regra");
        JButton btnDetalhar = new JButton("Detalhar / Excluir");
        JButton btnAtualizar = new JButton("Atualizar Tabela");

        painelBotoes.add(btnNovaRegra);
        painelBotoes.add(btnDetalhar);
        painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnNovaRegra.addActionListener(e -> abrirFormularioCadastro());
        btnDetalhar.addActionListener(e -> detalharRegraSelecionada());
        btnAtualizar.addActionListener(e -> atualizarTabela());
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        for (RegraPreco r : regraPrecoService.listarRegras()) {
            tableModel.addRow(new Object[]{
                    r.getId(), "R$ " + r.getValorHora(),
                    r.getDiaSemana() != null ? r.getDiaSemana() : "Todos",
                    r.getTurno() != null ? r.getTurno() : "Todos",
                    r.getMes() != null ? r.getMes() : "Todos",
                    r.getAno() != null ? r.getAno() : "Todos",
                    r.getHorarioInicio() != null ? r.getHorarioInicio() : "-",
                    r.getHorarioFim() != null ? r.getHorarioFim() : "-"
            });
        }
    }

    private void abrirFormularioCadastro() {
        JTextField txtValor = new JTextField();
        JComboBox<DayOfWeek> cbDiaSemana = new JComboBox<>(DayOfWeek.values()); cbDiaSemana.insertItemAt(null, 0); cbDiaSemana.setSelectedIndex(0);
        JComboBox<Turno> cbTurno = new JComboBox<>(Turno.values()); cbTurno.insertItemAt(null, 0); cbTurno.setSelectedIndex(0);
        JComboBox<Month> cbMes = new JComboBox<>(Month.values()); cbMes.insertItemAt(null, 0); cbMes.setSelectedIndex(0);
        JTextField txtAno = new JTextField();
        JTextField txtHoraInicio = new JTextField();
        JTextField txtHoraFim = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Valor por Hora (R$)*:")); form.add(txtValor);
        form.add(new JLabel("Dia da Semana:")); form.add(cbDiaSemana);
        form.add(new JLabel("Turno:")); form.add(cbTurno);
        form.add(new JLabel("Mês:")); form.add(cbMes);
        form.add(new JLabel("Ano (ex: 2026):")); form.add(txtAno);
        form.add(new JLabel("Início (HH:mm):")); form.add(txtHoraInicio);
        form.add(new JLabel("Fim (HH:mm):")); form.add(txtHoraFim);

        if (JOptionPane.showConfirmDialog(this, form, "Cadastrar Regra", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                BigDecimal valor = new BigDecimal(txtValor.getText().replace(",", "."));
                DayOfWeek dia = (DayOfWeek) cbDiaSemana.getSelectedItem();
                Turno turno = (Turno) cbTurno.getSelectedItem();
                Month mes = (Month) cbMes.getSelectedItem();
                Integer ano = txtAno.getText().isBlank() ? null : Integer.parseInt(txtAno.getText().trim());
                LocalTime hInicio = txtHoraInicio.getText().isBlank() ? null : LocalTime.parse(txtHoraInicio.getText().trim());
                LocalTime hFim = txtHoraFim.getText().isBlank() ? null : LocalTime.parse(txtHoraFim.getText().trim());

                // Correção JPA: IDs não são preenchidos manualmente
                RegraPreco novaRegra = new RegraPreco(valor, dia, turno, hInicio, hFim, mes, ano);
                regraPrecoService.cadastrarRegra(novaRegra);

                JOptionPane.showMessageDialog(this, "Regra salva com sucesso!");
                atualizarTabela();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void detalharRegraSelecionada() {
        int linhaSelecionada = tabelaRegras.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma regra na tabela.");
            return;
        }
        Long id = (Long) tableModel.getValueAt(linhaSelecionada, 0);
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir a regra ID: " + id + "?", "Exclusão", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            regraPrecoService.excluirRegra(id);
            JOptionPane.showMessageDialog(this, "Regra excluída!");
            atualizarTabela();
        }
    }
}
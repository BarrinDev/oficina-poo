package br.ucb.oficina.view;

import br.ucb.oficina.controller.ServicoController;
import br.ucb.oficina.model.Servico;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Tela do CRUD de Serviço (catálogo de serviços da oficina).
 */
public class ServicoView extends JFrame {

    private final ServicoController controller = new ServicoController();

    private List<Servico> servicos = new ArrayList<>(); // mesma ordem das linhas da tabela
    private int idSelecionado = 0;                     // 0 = serviço novo

    // ----- campos do formulário -----
    private final JTextField txtNome = new JTextField();
    private final JTextField txtDescricao = new JTextField();
    private final JTextField txtPreco = new JTextField();
    private final JTextField txtTempo = new JTextField();
    private final JTextField txtGarantia = new JTextField();

    // ----- tabela (só leitura) -----
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Serviço", "Descrição", "Preço", "Tempo (min)", "Garantia (dias)"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public ServicoView() {
        setTitle("Serviços");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Formulário: rótulo, campo, rótulo, campo
        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Nome do serviço:"));           form.add(txtNome);
        form.add(new JLabel("Descrição:"));                 form.add(txtDescricao);
        form.add(new JLabel("Preço (ex.: 150,00):"));       form.add(txtPreco);
        form.add(new JLabel("Tempo estimado (min):"));      form.add(txtTempo);
        form.add(new JLabel("Garantia (dias, vazio = 90):")); form.add(txtGarantia);

        // Botões
        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JPanel botoes = new JPanel();
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnExcluir);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        // Eventos
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionar();
            }
        });

        carregarTabela();
    }

    // Busca os serviços e coloca na tabela
    private void carregarTabela() {
        try {
            servicos = controller.listar();
            modelo.setRowCount(0);
            for (Servico s : servicos) {
                modelo.addRow(new Object[] {
                    s.getIdServico(), s.getNomeServico(), s.getDescricao(), s.getPrecoTabela(),
                    s.getTempoEstimadoMinutos(), s.getGarantiaDias()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Joga o serviço da linha clicada no formulário
    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Servico s = servicos.get(linha);
        idSelecionado = s.getIdServico();
        txtNome.setText(s.getNomeServico());
        txtDescricao.setText(s.getDescricao());
        txtPreco.setText(s.getPrecoTabela().toString().replace(".", ",")); // 150.00 -> 150,00
        txtTempo.setText(String.valueOf(s.getTempoEstimadoMinutos()));
        txtGarantia.setText(String.valueOf(s.getGarantiaDias()));
    }

    // Botão Salvar
    private void salvar() {
        try {
            controller.salvar(idSelecionado, txtNome.getText(), txtDescricao.getText(),
                    txtPreco.getText(), txtTempo.getText(), txtGarantia.getText());
            JOptionPane.showMessageDialog(this, "Serviço salvo!");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Excluir
    private void excluir() {
        int resposta = JOptionPane.showConfirmDialog(this, "Excluir o serviço selecionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.remover(idSelecionado);
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Novo
    private void limpar() {
        idSelecionado = 0;
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtTempo.setText("");
        txtGarantia.setText("");
        tabela.clearSelection();
    }
}

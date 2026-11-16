package br.ucb.oficina.view;

import br.ucb.oficina.controller.FuncionarioController;
import br.ucb.oficina.model.Funcionario;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Tela do CRUD de Funcionário.
 * Datas são digitadas como dd/mm/aaaa. Desligamento vazio = funcionário ativo.
 */
public class FuncionarioView extends JFrame {

    private final FuncionarioController controller = new FuncionarioController();

    private List<Funcionario> funcionarios = new ArrayList<>(); // mesma ordem das linhas da tabela
    private int idSelecionado = 0;                             // 0 = funcionário novo

    // ----- campos do formulário -----
    private final JTextField txtNome = new JTextField();
    private final JTextField txtCpf = new JTextField();
    private final JTextField txtCargo = new JTextField();
    private final JTextField txtAdmissao = new JTextField();
    private final JTextField txtDesligamento = new JTextField();
    private final JTextField txtSalario = new JTextField();

    // ----- tabela (só leitura) -----
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Nome", "CPF", "Cargo", "Admissão", "Desligamento", "Salário"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public FuncionarioView() {
        setTitle("Funcionários");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Formulário: rótulo, campo, rótulo, campo
        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Nome:"));                        form.add(txtNome);
        form.add(new JLabel("CPF:"));                         form.add(txtCpf);
        form.add(new JLabel("Cargo:"));                       form.add(txtCargo);
        form.add(new JLabel("Salário (ex.: 4300,00):"));      form.add(txtSalario);
        form.add(new JLabel("Admissão (dd/mm/aaaa):"));       form.add(txtAdmissao);
        form.add(new JLabel("Desligamento (vazio = ativo):")); form.add(txtDesligamento);

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

    // Busca os funcionários e coloca na tabela
    private void carregarTabela() {
        try {
            funcionarios = controller.listar();
            modelo.setRowCount(0);
            for (Funcionario f : funcionarios) {
                modelo.addRow(new Object[] {
                    f.getIdFuncionario(), f.getNome(), f.getCpf(), f.getCargo(),
                    controller.formatarData(f.getDataAdmissao()),
                    controller.formatarData(f.getDataDesligamento()),
                    f.getSalario()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Joga o funcionário da linha clicada no formulário
    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Funcionario f = funcionarios.get(linha);
        idSelecionado = f.getIdFuncionario();
        txtNome.setText(f.getNome());
        txtCpf.setText(f.getCpf());
        txtCargo.setText(f.getCargo());
        txtAdmissao.setText(controller.formatarData(f.getDataAdmissao()));
        txtDesligamento.setText(controller.formatarData(f.getDataDesligamento()));
        txtSalario.setText(f.getSalario().toString().replace(".", ",")); // 4300.00 -> 4300,00
    }

    // Botão Salvar
    private void salvar() {
        try {
            controller.salvar(idSelecionado, txtNome.getText(), txtCpf.getText(), txtCargo.getText(),
                    txtAdmissao.getText(), txtDesligamento.getText(), txtSalario.getText());
            JOptionPane.showMessageDialog(this, "Funcionário salvo!");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Excluir
    private void excluir() {
        int resposta = JOptionPane.showConfirmDialog(this, "Excluir o funcionário selecionado?",
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
        txtCpf.setText("");
        txtCargo.setText("");
        txtAdmissao.setText("");
        txtDesligamento.setText("");
        txtSalario.setText("");
        tabela.clearSelection();
    }
}

package br.ucb.oficina.view;

import br.ucb.oficina.controller.ClienteController;
import br.ucb.oficina.model.Cliente;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Tela do CRUD de Cliente.
 * Em cima: formulário. No meio: tabela com os clientes. Embaixo: botões.
 * Clicar numa linha da tabela joga o cliente no formulário para alterar ou excluir.
 * Nenhuma regra aqui: a tela só lê os campos e chama o Controller.
 */
public class ClienteView extends JFrame {

    private final ClienteController controller = new ClienteController();

    private List<Cliente> clientes = new ArrayList<>(); // clientes da tabela (mesma ordem das linhas)
    private int idSelecionado = 0;                     // cliente clicado na tabela; 0 = cliente novo

    // ----- campos do formulário -----
    private final JTextField txtNome = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[] {"PF", "PJ"});
    private final JTextField txtCpfCnpj = new JTextField();
    private final JTextField txtLogradouro = new JTextField();
    private final JTextField txtNumero = new JTextField();
    private final JTextField txtComplemento = new JTextField();
    private final JTextField txtBairro = new JTextField();
    private final JTextField txtCidade = new JTextField();
    private final JTextField txtUf = new JTextField();
    private final JTextField txtCep = new JTextField();

    // ----- tabela -----
    // O DefaultTableModel guarda as linhas. isCellEditable = false deixa a tabela só para leitura.
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Nome", "Tipo", "CPF/CNPJ", "E-mail", "Cidade", "UF"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public ClienteView() {
        setTitle("Clientes");
        setSize(900, 600);
        setLocationRelativeTo(null);                // abre no centro da tela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // fecha só esta janela; o menu continua aberto

        // Formulário em grade de 4 colunas: rótulo, campo, rótulo, campo
        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Nome:"));         form.add(txtNome);
        form.add(new JLabel("E-mail:"));       form.add(txtEmail);
        form.add(new JLabel("Tipo:"));         form.add(cbTipo);
        form.add(new JLabel("CPF/CNPJ:"));     form.add(txtCpfCnpj);
        form.add(new JLabel("Logradouro:"));   form.add(txtLogradouro);
        form.add(new JLabel("Número:"));       form.add(txtNumero);
        form.add(new JLabel("Complemento:"));  form.add(txtComplemento);
        form.add(new JLabel("Bairro:"));       form.add(txtBairro);
        form.add(new JLabel("Cidade:"));       form.add(txtCidade);
        form.add(new JLabel("UF:"));           form.add(txtUf);
        form.add(new JLabel("CEP:"));          form.add(txtCep);

        // Botões
        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JPanel botoes = new JPanel();
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnExcluir);

        // Monta a janela (o JFrame já usa BorderLayout: NORTH, CENTER, SOUTH)
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        // Eventos: cada botão só chama um método desta tela
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        // Clicou numa linha -> preenche o formulário (o if evita rodar 2 vezes no mesmo clique)
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionar();
            }
        });

        carregarTabela();
    }

    // Busca os clientes no banco (via Controller) e coloca na tabela
    private void carregarTabela() {
        try {
            clientes = controller.listar();
            modelo.setRowCount(0); // apaga as linhas antigas
            for (Cliente c : clientes) {
                modelo.addRow(new Object[] {
                    c.getIdCliente(), c.getNome(), c.getTipoCliente(), c.getCpfCnpj(),
                    c.getEmail(), c.getCidade(), c.getUf()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Joga o cliente da linha clicada no formulário
    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return; // nenhuma linha selecionada
        }
        Cliente c = clientes.get(linha);
        idSelecionado = c.getIdCliente();
        txtNome.setText(c.getNome());
        txtEmail.setText(c.getEmail());          // setText(null) só deixa o campo vazio
        cbTipo.setSelectedItem(c.getTipoCliente());
        txtCpfCnpj.setText(c.getCpfCnpj());
        txtLogradouro.setText(c.getLogradouro());
        txtNumero.setText(c.getNumero());
        txtComplemento.setText(c.getComplemento());
        txtBairro.setText(c.getBairro());
        txtCidade.setText(c.getCidade());
        txtUf.setText(c.getUf());
        txtCep.setText(c.getCep());
    }

    // Botão Salvar: manda os textos para o Controller (ele decide se é INSERT ou UPDATE)
    private void salvar() {
        try {
            controller.salvar(idSelecionado,
                    txtNome.getText(), txtEmail.getText(), (String) cbTipo.getSelectedItem(),
                    txtCpfCnpj.getText(), txtLogradouro.getText(), txtNumero.getText(),
                    txtComplemento.getText(), txtBairro.getText(), txtCidade.getText(),
                    txtUf.getText(), txtCep.getText());
            JOptionPane.showMessageDialog(this, "Cliente salvo!");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Excluir: pede confirmação e chama o Controller
    private void excluir() {
        int resposta = JOptionPane.showConfirmDialog(this, "Excluir o cliente selecionado?",
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

    // Botão Novo: limpa o formulário e volta para "cliente novo"
    private void limpar() {
        idSelecionado = 0;
        txtNome.setText("");
        txtEmail.setText("");
        cbTipo.setSelectedIndex(0);
        txtCpfCnpj.setText("");
        txtLogradouro.setText("");
        txtNumero.setText("");
        txtComplemento.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtUf.setText("");
        txtCep.setText("");
        tabela.clearSelection();
    }
}

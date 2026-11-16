package br.ucb.oficina.view;

import br.ucb.oficina.controller.ClienteController;
import br.ucb.oficina.controller.VeiculoController;
import br.ucb.oficina.model.Cliente;
import br.ucb.oficina.model.Veiculo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Tela do CRUD de Veículo.
 * O dono é escolhido num combo de Clientes (por causa da FK id_cliente):
 * assim o usuário nunca digita um id de cliente que não existe.
 */
public class VeiculoView extends JFrame {

    private final VeiculoController controller = new VeiculoController();
    private final ClienteController clienteController = new ClienteController(); // só para encher o combo

    private List<Veiculo> veiculos = new ArrayList<>(); // veículos da tabela (mesma ordem das linhas)
    private int idSelecionado = 0;                     // 0 = veículo novo

    // ----- campos do formulário -----
    private final JComboBox<Cliente> cbCliente = new JComboBox<>(); // mostra o toString() do Cliente (nome)
    private final JTextField txtPlaca = new JTextField();
    private final JTextField txtMarca = new JTextField();
    private final JTextField txtModelo = new JTextField();
    private final JTextField txtAnoFabricacao = new JTextField();
    private final JTextField txtAnoModelo = new JTextField();
    private final JTextField txtCor = new JTextField();
    private final JTextField txtKm = new JTextField();

    // ----- tabela (só leitura) -----
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Placa", "Marca", "Modelo", "Ano", "Cor", "Km", "Dono"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public VeiculoView() {
        setTitle("Veículos");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Formulário: rótulo, campo, rótulo, campo
        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Dono (cliente):"));    form.add(cbCliente);
        form.add(new JLabel("Placa:"));             form.add(txtPlaca);
        form.add(new JLabel("Marca:"));             form.add(txtMarca);
        form.add(new JLabel("Modelo:"));            form.add(txtModelo);
        form.add(new JLabel("Ano fabricação:"));    form.add(txtAnoFabricacao);
        form.add(new JLabel("Ano modelo:"));        form.add(txtAnoModelo);
        form.add(new JLabel("Cor:"));               form.add(txtCor);
        form.add(new JLabel("Km atual:"));          form.add(txtKm);

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

        carregarClientes();
        carregarTabela();
    }

    // Enche o combo com os clientes do banco
    private void carregarClientes() {
        try {
            for (Cliente c : clienteController.listar()) {
                cbCliente.addItem(c);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Busca os veículos e coloca na tabela
    private void carregarTabela() {
        try {
            veiculos = controller.listar();
            modelo.setRowCount(0);
            for (Veiculo v : veiculos) {
                modelo.addRow(new Object[] {
                    v.getIdVeiculo(), v.getPlaca(), v.getMarca(), v.getModelo(),
                    v.getAnoFabricacao() + "/" + v.getAnoModelo(), v.getCor(), v.getKmAtual(),
                    v.getNomeCliente()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Joga o veículo da linha clicada no formulário
    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Veiculo v = veiculos.get(linha);
        idSelecionado = v.getIdVeiculo();

        // Procura no combo o cliente que é dono deste veículo e seleciona ele
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getIdCliente() == v.getIdCliente()) {
                cbCliente.setSelectedIndex(i);
            }
        }

        txtPlaca.setText(v.getPlaca());
        txtMarca.setText(v.getMarca());
        txtModelo.setText(v.getModelo());
        txtAnoFabricacao.setText(String.valueOf(v.getAnoFabricacao()));
        txtAnoModelo.setText(String.valueOf(v.getAnoModelo()));
        txtCor.setText(v.getCor());
        txtKm.setText(String.valueOf(v.getKmAtual()));
    }

    // Botão Salvar: manda tudo para o Controller (ele valida e decide INSERT ou UPDATE)
    private void salvar() {
        try {
            controller.salvar(idSelecionado,
                    (Cliente) cbCliente.getSelectedItem(),
                    txtPlaca.getText(), txtMarca.getText(), txtModelo.getText(),
                    txtAnoFabricacao.getText(), txtAnoModelo.getText(),
                    txtCor.getText(), txtKm.getText());
            JOptionPane.showMessageDialog(this, "Veículo salvo!");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Excluir
    private void excluir() {
        int resposta = JOptionPane.showConfirmDialog(this, "Excluir o veículo selecionado?",
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
        cbCliente.setSelectedIndex(cbCliente.getItemCount() > 0 ? 0 : -1); // -1 = nada selecionado
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtAnoFabricacao.setText("");
        txtAnoModelo.setText("");
        txtCor.setText("");
        txtKm.setText("");
        tabela.clearSelection();
    }
}

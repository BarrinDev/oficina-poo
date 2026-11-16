package br.ucb.oficina.view;

import br.ucb.oficina.controller.ItemOSController;
import br.ucb.oficina.controller.OrdemServicoController;
import br.ucb.oficina.controller.ServicoController;
import br.ucb.oficina.model.ItemOS;
import br.ucb.oficina.model.Servico;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Painel com os serviços (itens) de uma OS.
 * Não é uma janela: é um pedaço de tela (JPanel) que fica DENTRO da tela de OS.
 * A tela de OS chama carregarOS(id) toda vez que muda a OS selecionada.
 * Usa o ItemOSController do Felipe para adicionar/alterar/remover.
 */
public class ItemOSPanel extends JPanel {

    private final ItemOSController controller = new ItemOSController();
    private final ServicoController servicoController = new ServicoController(); // combo de serviços
    private final OrdemServicoController osController = new OrdemServicoController(); // cálculo do total

    private int idOs = 0;                         // OS mostrada no painel; 0 = nenhuma (OS não salva)
    private List<ItemOS> itens = new ArrayList<>(); // itens da tabela (mesma ordem das linhas)

    // ----- campos -----
    private final JComboBox<Servico> cbServico = new JComboBox<>();
    private final JTextField txtValor = new JTextField();
    private final JTextField txtTempo = new JTextField();
    private final JButton btnAdicionar = new JButton("Adicionar serviço");
    private final JButton btnAlterar = new JButton("Alterar");
    private final JButton btnRemover = new JButton("Remover");
    private final JLabel lblTotal = new JLabel("Total da OS: R$ 0,00");

    // ----- tabela (só leitura) -----
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Serviço", "Valor cobrado", "Tempo gasto (min)"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public ItemOSPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Serviços da OS"));

        // Formulário: rótulo, campo, rótulo, campo
        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        form.add(new JLabel("Serviço:"));                      form.add(cbServico);
        form.add(new JLabel("Valor cobrado (ex.: 150,00):"));  form.add(txtValor);
        form.add(new JLabel("Tempo gasto (min, vazio = não feito):")); form.add(txtTempo);

        JPanel botoes = new JPanel();
        botoes.add(btnAdicionar);
        botoes.add(btnAlterar);
        botoes.add(btnRemover);
        botoes.add(lblTotal);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Eventos
        btnAdicionar.addActionListener(e -> adicionar());
        btnAlterar.addActionListener(e -> alterar());
        btnRemover.addActionListener(e -> remover());
        // Escolheu um serviço no combo -> sugere o preço de tabela no campo Valor
        cbServico.addActionListener(e -> sugerirPreco());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionar();
            }
        });

        carregarServicos();
        carregarOS(0); // começa sem OS: botões desligados
    }

    // Chamado pela tela de OS: mostra os serviços da OS informada (0 = nenhuma OS)
    public void carregarOS(int idOs) {
        this.idOs = idOs;

        // Sem OS salva não dá para lançar serviço (o item precisa do id da OS)
        boolean temOS = idOs > 0;
        btnAdicionar.setEnabled(temOS);
        btnAlterar.setEnabled(temOS);
        btnRemover.setEnabled(temOS);

        txtTempo.setText("");
        sugerirPreco();
        carregarTabela();
    }

    // Enche o combo com os serviços do catálogo
    private void carregarServicos() {
        try {
            for (Servico s : servicoController.listar()) {
                cbServico.addItem(s);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Busca os itens da OS e coloca na tabela, atualizando o total
    private void carregarTabela() {
        modelo.setRowCount(0);
        itens = new ArrayList<>();
        if (idOs == 0) {
            lblTotal.setText("Total da OS: R$ 0,00");
            return;
        }
        try {
            itens = controller.listar(idOs);
            for (ItemOS item : itens) {
                modelo.addRow(new Object[] {
                    item.getNomeServico(),
                    item.getValorCobrado(),
                    item.getTempoGastoMinutos() // null aparece como célula vazia
                });
            }
            // %,.2f = número com 2 casas decimais e separador de milhar (ex.: 1.234,50)
            lblTotal.setText(String.format("Total da OS: R$ %,.2f", osController.calcularTotal(itens)));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Coloca o preço de tabela do serviço escolhido no campo Valor (o usuário pode mudar)
    private void sugerirPreco() {
        Servico s = (Servico) cbServico.getSelectedItem();
        if (s != null) {
            txtValor.setText(s.getPrecoTabela().toString().replace(".", ","));
        }
    }

    // Clicou num item -> joga no formulário
    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        ItemOS item = itens.get(linha);
        // Primeiro o combo (ele sugere o preço de tabela)...
        for (int i = 0; i < cbServico.getItemCount(); i++) {
            if (cbServico.getItemAt(i).getIdServico() == item.getIdServico()) {
                cbServico.setSelectedIndex(i);
            }
        }
        // ...depois o valor cobrado de verdade, por cima da sugestão
        txtValor.setText(item.getValorCobrado().toString().replace(".", ","));
        txtTempo.setText(item.getTempoGastoMinutos() == null ? "" : item.getTempoGastoMinutos().toString());
    }

    // Botão Adicionar: lança o serviço do combo nesta OS
    private void adicionar() {
        Servico s = (Servico) cbServico.getSelectedItem();
        int idServico = (s == null) ? 0 : s.getIdServico(); // 0 -> o Controller avisa que não achou
        try {
            controller.adicionar(idOs, idServico, txtValor.getText(), txtTempo.getText());
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Alterar: muda valor/tempo do item selecionado na tabela
    // (para trocar o serviço em si: remover e adicionar de novo)
    private void alterar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela.");
            return;
        }
        try {
            controller.alterar(idOs, itens.get(linha).getIdServico(), txtValor.getText(), txtTempo.getText());
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Remover: tira o item selecionado da OS
    private void remover() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Remover este serviço da OS?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.remover(idOs, itens.get(linha).getIdServico());
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}

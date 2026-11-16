package br.ucb.oficina.view;

import br.ucb.oficina.controller.FuncionarioController;
import br.ucb.oficina.controller.OrdemServicoController;
import br.ucb.oficina.controller.VeiculoController;
import br.ucb.oficina.model.Funcionario;
import br.ucb.oficina.model.OrdemServico;
import br.ucb.oficina.model.Veiculo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Tela principal do sistema: Ordem de Serviço.
 * Em cima: formulário do cabeçalho da OS + botões.
 * No meio: tabela com as OS.
 * Embaixo: painel com os serviços da OS selecionada (ItemOSPanel).
 *
 * Fluxo: Novo -> preenche -> Salvar -> a OS fica selecionada -> lança os serviços no painel.
 */
public class OrdemServicoView extends JFrame {

    private final OrdemServicoController controller = new OrdemServicoController();
    private final VeiculoController veiculoController = new VeiculoController();             // combo de veículo
    private final FuncionarioController funcionarioController = new FuncionarioController(); // combo de responsável

    private List<OrdemServico> ordens = new ArrayList<>(); // OS da tabela (mesma ordem das linhas)
    private OrdemServico selecionada = null;               // OS clicada na tabela; null = OS nova

    // ----- campos do formulário -----
    private final JLabel lblNumero = new JLabel("OS nova");
    private final JComboBox<Veiculo> cbVeiculo = new JComboBox<>();
    private final JComboBox<Funcionario> cbFuncionario = new JComboBox<>();
    private final JComboBox<String> cbStatus = new JComboBox<>(
            new String[] {"aberta", "em_execucao", "concluida", "cancelada"});
    private final JTextField txtPrevista = new JTextField();
    private final JTextField txtKm = new JTextField();
    private final JTextField txtObservacoes = new JTextField();

    // Painel dos serviços da OS (classe separada, fica encaixado nesta tela)
    private final ItemOSPanel painelItens = new ItemOSPanel();

    // ----- tabela de OS (só leitura) -----
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Nº", "Abertura", "Status", "Placa", "Responsável", "Previsão"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public OrdemServicoView() {
        setTitle("Ordens de Serviço");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Formulário do cabeçalho: rótulo, campo, rótulo, campo
        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        form.add(new JLabel("Número:"));                    form.add(lblNumero);
        form.add(new JLabel("Status:"));                    form.add(cbStatus);
        form.add(new JLabel("Veículo:"));                   form.add(cbVeiculo);
        form.add(new JLabel("Responsável:"));               form.add(cbFuncionario);
        form.add(new JLabel("Km de entrada:"));             form.add(txtKm);
        form.add(new JLabel("Previsão (dd/mm/aaaa):"));     form.add(txtPrevista);
        form.add(new JLabel("Observações:"));               form.add(txtObservacoes);

        // Botões
        JButton btnNovo = new JButton("Nova OS");
        JButton btnSalvar = new JButton("Salvar OS");
        JButton btnExcluir = new JButton("Excluir OS");
        JPanel botoes = new JPanel();
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnExcluir);

        // Topo = formulário + botões
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        // Centro = tabela de OS em cima e painel de serviços embaixo (2 linhas iguais)
        JPanel centro = new JPanel(new GridLayout(2, 1, 5, 5));
        centro.add(new JScrollPane(tabela));
        centro.add(painelItens);

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        // Eventos
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionar();
            }
        });

        carregarCombos();
        carregarTabela();
    }

    // Enche os combos de veículo e de funcionário
    private void carregarCombos() {
        try {
            for (Veiculo v : veiculoController.listar()) {
                cbVeiculo.addItem(v);
            }
            for (Funcionario f : funcionarioController.listar()) {
                cbFuncionario.addItem(f);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Busca as OS e coloca na tabela
    private void carregarTabela() {
        try {
            ordens = controller.listar();
            modelo.setRowCount(0);
            for (OrdemServico os : ordens) {
                modelo.addRow(new Object[] {
                    os.getIdOs(),
                    controller.formatarDataHora(os.getDataAbertura()),
                    os.getStatusAtual(),
                    os.getPlaca(),
                    os.getNomeFuncionario(),
                    controller.formatarData(os.getDataConclusaoPrevista())
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Joga a OS clicada no formulário e mostra os serviços dela no painel
    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        selecionada = ordens.get(linha);

        lblNumero.setText(selecionada.getIdOs() + "  (aberta em "
                + controller.formatarDataHora(selecionada.getDataAbertura()) + ")");
        cbStatus.setSelectedItem(selecionada.getStatusAtual());

        // Procura nos combos o veículo e o funcionário desta OS
        for (int i = 0; i < cbVeiculo.getItemCount(); i++) {
            if (cbVeiculo.getItemAt(i).getIdVeiculo() == selecionada.getIdVeiculo()) {
                cbVeiculo.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < cbFuncionario.getItemCount(); i++) {
            if (cbFuncionario.getItemAt(i).getIdFuncionario() == selecionada.getIdFuncionarioResponsavel()) {
                cbFuncionario.setSelectedIndex(i);
            }
        }

        txtKm.setText(String.valueOf(selecionada.getKmEntrada()));
        txtPrevista.setText(controller.formatarData(selecionada.getDataConclusaoPrevista()));
        txtObservacoes.setText(selecionada.getObservacoesGerais());

        painelItens.carregarOS(selecionada.getIdOs()); // encaixe do painel de itens
    }

    // Botão Salvar OS: o Controller valida, grava e devolve o número da OS
    private void salvar() {
        try {
            int id = controller.salvar(selecionada,
                    (Veiculo) cbVeiculo.getSelectedItem(),
                    (Funcionario) cbFuncionario.getSelectedItem(),
                    (String) cbStatus.getSelectedItem(),
                    txtPrevista.getText(), txtKm.getText(), txtObservacoes.getText());
            JOptionPane.showMessageDialog(this, "OS nº " + id + " salva! Lance os serviços no painel de baixo.");
            carregarTabela();
            selecionarNaTabela(id); // deixa a OS selecionada para já lançar os serviços
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Excluir OS
    private void excluir() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir a OS selecionada e todos os serviços dela?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.remover(selecionada);
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Botão Nova OS: limpa o formulário e o painel de serviços
    private void limpar() {
        selecionada = null;
        lblNumero.setText("OS nova");
        cbStatus.setSelectedItem("aberta");
        cbVeiculo.setSelectedIndex(cbVeiculo.getItemCount() > 0 ? 0 : -1);
        cbFuncionario.setSelectedIndex(cbFuncionario.getItemCount() > 0 ? 0 : -1);
        txtKm.setText("");
        txtPrevista.setText("");
        txtObservacoes.setText("");
        tabela.clearSelection();
        painelItens.carregarOS(0); // sem OS salva: painel fica vazio e com botões desligados
    }

    // Seleciona na tabela a linha da OS com este número (dispara o selecionar())
    private void selecionarNaTabela(int idOs) {
        for (int i = 0; i < ordens.size(); i++) {
            if (ordens.get(i).getIdOs() == idOs) {
                tabela.setRowSelectionInterval(i, i);
            }
        }
    }
}

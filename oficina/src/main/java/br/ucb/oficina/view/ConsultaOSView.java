package br.ucb.oficina.view;

import br.ucb.oficina.controller.ConsultaController;
import br.ucb.oficina.controller.ItemOSController;
import br.ucb.oficina.model.ItemOS;
import br.ucb.oficina.model.OrdemServico;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Tela "Consultar OS" (só leitura).
 * Em cima: filtro por status. Tabela de cima: OS com veículo, cliente e responsável (JOIN - consulta C01).
 * Tabela de baixo: serviços da OS clicada (consulta C02, reaproveitando o ItemOSController).
 */
public class ConsultaOSView extends JFrame {

    private final ConsultaController controller = new ConsultaController();
    private final ItemOSController itemController = new ItemOSController();

    private List<OrdemServico> ordens = new ArrayList<>(); // mesma ordem das linhas da tabela

    private final JComboBox<String> cbStatus = new JComboBox<>(
            new String[] {"todos", "aberta", "em_execucao", "concluida", "cancelada"});

    // Tabela das OS (C01)
    private final DefaultTableModel modeloOS = new DefaultTableModel(
            new String[] {"Nº", "Abertura", "Status", "Placa", "Modelo", "Cliente", "Responsável"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabelaOS = new JTable(modeloOS);

    // Tabela dos serviços da OS selecionada (C02)
    private final DefaultTableModel modeloItens = new DefaultTableModel(
            new String[] {"Serviço", "Valor cobrado", "Tempo gasto (min)"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabelaItens = new JTable(modeloItens);

    public ConsultaOSView() {
        setTitle("Consultar OS");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Filtro
        JButton btnConsultar = new JButton("Consultar");
        JPanel filtro = new JPanel();
        filtro.add(new JLabel("Status:"));
        filtro.add(cbStatus);
        filtro.add(btnConsultar);

        // Painel de baixo com título
        JScrollPane rolagemItens = new JScrollPane(tabelaItens);
        rolagemItens.setBorder(BorderFactory.createTitledBorder("Serviços da OS selecionada"));

        // Centro: as duas tabelas, uma em cima da outra
        JPanel centro = new JPanel(new GridLayout(2, 1, 5, 5));
        centro.add(new JScrollPane(tabelaOS));
        centro.add(rolagemItens);

        add(filtro, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        // Eventos
        btnConsultar.addActionListener(e -> consultar());
        tabelaOS.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarItens();
            }
        });

        consultar(); // já abre mostrando todas
    }

    // Botão Consultar: busca as OS do status escolhido
    private void consultar() {
        try {
            ordens = controller.consultarOS((String) cbStatus.getSelectedItem());
            modeloOS.setRowCount(0);
            modeloItens.setRowCount(0);
            for (OrdemServico os : ordens) {
                modeloOS.addRow(new Object[] {
                    os.getIdOs(),
                    controller.formatarDataHora(os.getDataAbertura()),
                    os.getStatusAtual(),
                    os.getPlaca(),
                    os.getModeloVeiculo(),
                    os.getNomeCliente(),
                    os.getNomeFuncionario()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Clicou numa OS -> mostra os serviços dela na tabela de baixo
    private void mostrarItens() {
        int linha = tabelaOS.getSelectedRow();
        if (linha < 0) {
            return;
        }
        try {
            modeloItens.setRowCount(0);
            for (ItemOS item : itemController.listar(ordens.get(linha).getIdOs())) {
                modeloItens.addRow(new Object[] {
                    item.getNomeServico(), item.getValorCobrado(), item.getTempoGastoMinutos()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}

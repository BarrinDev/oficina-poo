package br.ucb.oficina.view;

import br.ucb.oficina.controller.ConsultaController;
import br.ucb.oficina.model.OrdemServico;
import br.ucb.oficina.model.ResumoStatus;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de Relatório por período.
 * Tabela de cima: OS abertas no período com o valor de cada uma (R01).
 * Tabela de baixo: resumo por status no mesmo período (R02).
 * Rodapé: total faturado (sem as canceladas).
 */
public class RelatorioView extends JFrame {

    private final ConsultaController controller = new ConsultaController();

    private final JTextField txtInicio = new JTextField(10);
    private final JTextField txtFim = new JTextField(10);
    private final JLabel lblTotal = new JLabel(" ");

    // Tabela das OS do período (R01)
    private final DefaultTableModel modeloOS = new DefaultTableModel(
            new String[] {"Nº", "Abertura", "Status", "Cliente", "Placa", "Responsável", "Valor total"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    // Tabela do resumo por status (R02)
    private final DefaultTableModel modeloResumo = new DefaultTableModel(
            new String[] {"Status", "Quantidade de OS", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public RelatorioView() {
        setTitle("Relatório de OS por período");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Já abre com o mês atual (do dia 1 até hoje)
        txtInicio.setText(controller.primeiroDiaDoMes());
        txtFim.setText(controller.hoje());

        // Filtro de período
        JButton btnGerar = new JButton("Gerar relatório");
        JPanel filtro = new JPanel();
        filtro.add(new JLabel("De (dd/mm/aaaa):"));
        filtro.add(txtInicio);
        filtro.add(new JLabel("Até:"));
        filtro.add(txtFim);
        filtro.add(btnGerar);

        // Tabela de baixo com título
        JScrollPane rolagemResumo = new JScrollPane(new JTable(modeloResumo));
        rolagemResumo.setBorder(BorderFactory.createTitledBorder("Resumo por status"));

        // Centro: as duas tabelas, uma em cima da outra
        JPanel centro = new JPanel(new GridLayout(2, 1, 5, 5));
        centro.add(new JScrollPane(new JTable(modeloOS)));
        centro.add(rolagemResumo);

        // Rodapé com o total
        JPanel rodape = new JPanel();
        rodape.add(lblTotal);

        add(filtro, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        btnGerar.addActionListener(e -> gerar());

        gerar(); // já abre mostrando o mês atual
    }

    // Botão Gerar: busca as duas partes do relatório e o total
    private void gerar() {
        try {
            List<OrdemServico> ordens = controller.relatorioPeriodo(txtInicio.getText(), txtFim.getText());
            List<ResumoStatus> resumo = controller.resumoPorStatus(txtInicio.getText(), txtFim.getText());

            modeloOS.setRowCount(0);
            for (OrdemServico os : ordens) {
                modeloOS.addRow(new Object[] {
                    os.getIdOs(),
                    controller.formatarDataHora(os.getDataAbertura()),
                    os.getStatusAtual(),
                    os.getNomeCliente(),
                    os.getPlaca(),
                    os.getNomeFuncionario(),
                    os.getValorTotal()
                });
            }

            modeloResumo.setRowCount(0);
            for (ResumoStatus r : resumo) {
                modeloResumo.addRow(new Object[] { r.getStatus(), r.getQuantidade(), r.getTotal() });
            }

            // %,.2f = 2 casas decimais com separador de milhar (ex.: 1.234,50)
            lblTotal.setText(String.format("%d OS no período  |  Total faturado (sem canceladas): R$ %,.2f",
                    ordens.size(), controller.totalFaturado(ordens)));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}

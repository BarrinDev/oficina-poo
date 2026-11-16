package br.ucb.oficina.view;

import java.awt.GridLayout;
import javax.swing.*;

/**
 * Menu principal: primeira janela do sistema.
 * Cada botão abre uma tela. Fechar o menu fecha o sistema inteiro.
 */
public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Oficina - Menu principal");
        setSize(350, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // aqui sim: fechou o menu, encerra o programa

        // Botões um embaixo do outro (0 linhas = quantas precisar, 1 coluna)
        JPanel painel = new JPanel(new GridLayout(0, 1, 8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton btnClientes = new JButton("Clientes");
        JButton btnVeiculos = new JButton("Veículos");
        JButton btnFuncionarios = new JButton("Funcionários");
        JButton btnServicos = new JButton("Serviços");
        JButton btnOS = new JButton("Ordens de Serviço");
        JButton btnConsulta = new JButton("Consultar OS");
        JButton btnRelatorio = new JButton("Relatório por período");
        JButton btnSair = new JButton("Sair");

        painel.add(btnClientes);
        painel.add(btnVeiculos);
        painel.add(btnFuncionarios);
        painel.add(btnServicos);
        painel.add(btnOS);
        painel.add(btnConsulta);
        painel.add(btnRelatorio);
        painel.add(btnSair);
        add(painel);

        // Cada botão cria a tela e mostra (a tela já carrega os dados sozinha)
        btnClientes.addActionListener(e -> new ClienteView().setVisible(true));
        btnVeiculos.addActionListener(e -> new VeiculoView().setVisible(true));
        btnFuncionarios.addActionListener(e -> new FuncionarioView().setVisible(true));
        btnServicos.addActionListener(e -> new ServicoView().setVisible(true));
        btnOS.addActionListener(e -> new OrdemServicoView().setVisible(true));
        btnConsulta.addActionListener(e -> new ConsultaOSView().setVisible(true));
        btnRelatorio.addActionListener(e -> new RelatorioView().setVisible(true));
        btnSair.addActionListener(e -> System.exit(0));
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package br.ucb.oficina;

import br.ucb.oficina.view.MenuPrincipal;
import java.awt.EventQueue;

public class Oficina {

    public static void main(String[] args) {
        // Abre o menu principal; toda a navegação do sistema sai dele.
        // invokeLater = cria a tela na thread do Swing (é o jeito recomendado de abrir janela)
        EventQueue.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }

}

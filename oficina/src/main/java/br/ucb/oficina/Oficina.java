/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package br.ucb.oficina;

import br.ucb.oficina.dao.Conexao;
import java.sql.Connection;
import java.sql.SQLException;

public class Oficina {

    public static void main(String[] args) {
        // teste temporário da conexão
        try (Connection c = Conexao.getConexao()) {
            System.out.println("Conectou: " + !c.isClosed());
        } catch (SQLException e) {
            System.out.println("Erro " + e.getErrorCode() + ": " + e.getMessage());
        }
    }
    
}

package br.ucb.oficina.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
           
public class Conexao {
    private static final String URL     = "jdbc:mysql://localhost:3306/ucb_turbo2";
    private static final String USUARIO = "oficina_app";
    private static final String SENHA   = "Oficina@2026";   // cada um ajusta localmente

    // impede instanciar: uso é sempre Conexao.getConexao()
    private Conexao() { }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ucb.oficina.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
           
public class Conexao {
    private static final String URL     = "jdbc:mysql://localhost:3306/ucb_turbo2";
    private static final String USUARIO = "root";
    private static final String SENHA   = "sua_senha_aqui";   // cada um ajusta localmente

    // impede instanciar: uso é sempre Conexao.getConexao()
    private Conexao() { }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}

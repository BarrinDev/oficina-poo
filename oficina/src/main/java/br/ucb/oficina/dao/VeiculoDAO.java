package br.ucb.oficina.dao;

import br.ucb.oficina.model.Veiculo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela veiculo. Aqui só tem SQL, nenhuma regra.
 * Erros comuns que o Controller trata:
 *   1062 = placa já cadastrada
 *   1451 = veículo tem OS (FK), então não pode ser excluído
 *   1452 = cliente (dono) não existe
 */
public class VeiculoDAO {

    // Grava um veículo novo (o id o banco gera sozinho)
    public void inserir(Veiculo v) throws SQLException {
        String sql = "INSERT INTO veiculo (id_cliente, placa, marca, modelo, ano_fabricacao, "
                   + "ano_modelo, cor, km_atual) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, v); // parâmetros 1 a 8
            ps.executeUpdate();
        }
    }

    // Lista os veículos já com o nome do dono (JOIN com cliente)
    public List<Veiculo> listar() throws SQLException {
        String sql = "SELECT v.*, c.nome AS nome_cliente "
                   + "FROM veiculo v JOIN cliente c ON c.id_cliente = v.id_cliente "
                   + "ORDER BY v.placa";
        List<Veiculo> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Veiculo v = new Veiculo();
                v.setIdVeiculo(rs.getInt("id_veiculo"));
                v.setIdCliente(rs.getInt("id_cliente"));
                v.setNomeCliente(rs.getString("nome_cliente"));
                v.setPlaca(rs.getString("placa"));
                v.setMarca(rs.getString("marca"));
                v.setModelo(rs.getString("modelo"));
                v.setAnoFabricacao(rs.getInt("ano_fabricacao"));
                v.setAnoModelo(rs.getInt("ano_modelo"));
                v.setCor(rs.getString("cor"));
                v.setKmAtual(rs.getInt("km_atual"));
                lista.add(v);
            }
        }
        return lista;
    }

    // Altera um veículo que já existe
    public void alterar(Veiculo v) throws SQLException {
        String sql = "UPDATE veiculo SET id_cliente = ?, placa = ?, marca = ?, modelo = ?, "
                   + "ano_fabricacao = ?, ano_modelo = ?, cor = ?, km_atual = ? "
                   + "WHERE id_veiculo = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, v);          // parâmetros 1 a 8
            ps.setInt(9, v.getIdVeiculo());  // parâmetro 9 = WHERE
            ps.executeUpdate();
        }
    }

    // Exclui o veículo (o banco bloqueia se ele tiver OS: erro 1451)
    public void remover(int idVeiculo) throws SQLException {
        String sql = "DELETE FROM veiculo WHERE id_veiculo = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVeiculo);
            ps.executeUpdate();
        }
    }

    // Coloca os 8 campos no SQL, na MESMA ordem do INSERT e do UPDATE
    private void preencherCampos(PreparedStatement ps, Veiculo v) throws SQLException {
        ps.setInt(1, v.getIdCliente());
        ps.setString(2, v.getPlaca());
        ps.setString(3, v.getMarca());
        ps.setString(4, v.getModelo());
        ps.setInt(5, v.getAnoFabricacao());
        ps.setInt(6, v.getAnoModelo());
        ps.setString(7, v.getCor());      // null = sem cor
        ps.setInt(8, v.getKmAtual());
    }
}

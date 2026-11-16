package br.ucb.oficina.dao;

import br.ucb.oficina.model.Servico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela servico. Aqui só tem SQL, nenhuma regra.
 * Erros comuns que o Controller trata:
 *   1062 = já existe serviço com esse nome
 *   1451 = serviço já foi usado em alguma OS (FK), então não pode ser excluído
 */
public class ServicoDAO {

    // Grava um serviço novo
    public void inserir(Servico s) throws SQLException {
        String sql = "INSERT INTO servico (nome_servico, descricao, preco_tabela, "
                   + "tempo_estimado_minutos, garantia_dias) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, s); // parâmetros 1 a 5
            ps.executeUpdate();
        }
    }

    // Lista todos os serviços em ordem alfabética
    public List<Servico> listar() throws SQLException {
        String sql = "SELECT * FROM servico ORDER BY nome_servico";
        List<Servico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Servico s = new Servico();
                s.setIdServico(rs.getInt("id_servico"));
                s.setNomeServico(rs.getString("nome_servico"));
                s.setDescricao(rs.getString("descricao"));
                s.setPrecoTabela(rs.getBigDecimal("preco_tabela"));
                s.setTempoEstimadoMinutos(rs.getInt("tempo_estimado_minutos"));
                s.setGarantiaDias(rs.getInt("garantia_dias"));
                lista.add(s);
            }
        }
        return lista;
    }

    // Altera um serviço que já existe
    public void alterar(Servico s) throws SQLException {
        String sql = "UPDATE servico SET nome_servico = ?, descricao = ?, preco_tabela = ?, "
                   + "tempo_estimado_minutos = ?, garantia_dias = ? WHERE id_servico = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, s);          // parâmetros 1 a 5
            ps.setInt(6, s.getIdServico());  // parâmetro 6 = WHERE
            ps.executeUpdate();
        }
    }

    // Exclui o serviço (o banco bloqueia se ele já estiver em alguma OS: erro 1451)
    public void remover(int idServico) throws SQLException {
        String sql = "DELETE FROM servico WHERE id_servico = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idServico);
            ps.executeUpdate();
        }
    }

    // Coloca os 5 campos no SQL, na MESMA ordem do INSERT e do UPDATE
    private void preencherCampos(PreparedStatement ps, Servico s) throws SQLException {
        ps.setString(1, s.getNomeServico());
        ps.setString(2, s.getDescricao());   // null = sem descrição
        ps.setBigDecimal(3, s.getPrecoTabela());
        ps.setInt(4, s.getTempoEstimadoMinutos());
        ps.setInt(5, s.getGarantiaDias());
    }
}

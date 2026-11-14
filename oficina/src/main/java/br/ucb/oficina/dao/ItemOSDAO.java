package br.ucb.oficina.dao;

import br.ucb.oficina.model.ItemOS;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela os_servico (itens/serviços de uma OS).
 * Erros comuns que o Controller deve tratar:
 *   1062 = serviço já existe nesta OS
 *   1452 = OS ou serviço inexistente (FK)
 */
public class ItemOSDAO {
    
    // Adiciona um serviço a uma OS já salva (precisa de idOs válido)
     public void inserir(ItemOS item) throws SQLException {
        String sql = "INSERT INTO os_servico (id_os, id_servico, valor_cobrado, tempo_gasto_minutos) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, item.getIdOs());
            ps.setInt(2, item.getIdServico());
            ps.setBigDecimal(3, item.getValorCobrado());
            ps.setObject(4, item.getTempoGastoMinutos(), Types.SMALLINT); // setObject aceita null
            ps.executeUpdate();
        }
    }
    
    // Lista os serviços de uma OS, já com o nome do serviço (JOIN = consulta C02)
    public List<ItemOS> listarPorOs(int idOs) throws SQLException {
        String sql = "SELECT os.id_os, os.id_servico, s.nome_servico, os.valor_cobrado, os.tempo_gasto_minutos "
                   + "FROM os_servico os JOIN servico s ON s.id_servico = os.id_servico "
                   + "WHERE os.id_os = ?";
        List<ItemOS> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idOs);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemOS item = new ItemOS(
                        rs.getInt("id_os"),
                        rs.getInt("id_servico"),
                        rs.getBigDecimal("valor_cobrado"),
                        rs.getObject("tempo_gasto_minutos", Integer.class) // getInt transformaria NULL em 0
                    );
                    item.setNomeServico(rs.getString("nome_servico"));
                    lista.add(item);
                }
            }
        }
        return lista; // lista vazia se a OS não tiver itens (nunca null)
    }
    
    // Altera valor e tempo de um item (PK composta: precisa de idOs + idServico)
    public void alterar(ItemOS item) throws SQLException {
        String sql = "UPDATE os_servico SET valor_cobrado = ?, tempo_gasto_minutos = ? "
                   + "WHERE id_os = ? AND id_servico = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, item.getValorCobrado());
            ps.setObject(2, item.getTempoGastoMinutos(), Types.SMALLINT);
            ps.setInt(3, item.getIdOs());
            ps.setInt(4, item.getIdServico());
            ps.executeUpdate();
        }
    }
    
    // Remove um serviço da OS (PK composta: precisa dos dois IDs)
    public void remover(int idOs, int idServico) throws SQLException {
        String sql = "DELETE FROM os_servico WHERE id_os = ? AND id_servico = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idOs);
            ps.setInt(2, idServico);
            ps.executeUpdate();
        }
    }
}
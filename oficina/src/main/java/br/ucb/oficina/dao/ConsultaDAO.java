package br.ucb.oficina.dao;

import br.ucb.oficina.model.OrdemServico;
import br.ucb.oficina.model.ResumoStatus;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas e relatórios (só leitura, nada é gravado aqui).
 * Os SQLs são os de sql/03_consultas.sql, trocando os valores fixos por "?".
 */
public class ConsultaDAO {

    // C01 - OS com veículo, cliente e responsável (JOIN de 4 tabelas)
    // status = null -> traz todas; senão filtra pelo status
    public List<OrdemServico> consultarOS(String status) throws SQLException {
        String sql = "SELECT o.id_os, o.data_abertura, o.status_atual, v.placa, v.modelo, "
                   + "c.nome AS nome_cliente, f.nome AS nome_funcionario "
                   + "FROM ordem_servico o "
                   + "JOIN veiculo v ON v.id_veiculo = o.id_veiculo "
                   + "JOIN cliente c ON c.id_cliente = v.id_cliente "
                   + "JOIN funcionario f ON f.id_funcionario = o.id_funcionario_responsavel ";
        if (status != null) {
            sql += "WHERE o.status_atual = ? "; // só coloca o filtro se veio um status
        }
        sql += "ORDER BY o.data_abertura DESC";

        List<OrdemServico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (status != null) {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdemServico os = new OrdemServico();
                    os.setIdOs(rs.getInt("id_os"));
                    os.setDataAbertura(rs.getObject("data_abertura", LocalDateTime.class));
                    os.setStatusAtual(rs.getString("status_atual"));
                    os.setPlaca(rs.getString("placa"));
                    os.setModeloVeiculo(rs.getString("modelo"));
                    os.setNomeCliente(rs.getString("nome_cliente"));
                    os.setNomeFuncionario(rs.getString("nome_funcionario"));
                    lista.add(os);
                }
            }
        }
        return lista;
    }

    // R01 - OS abertas no período, com valor total (usa a view vw_os_resumo do 01_ddl.sql)
    // fimExclusivo = dia SEGUINTE ao último dia (assim pega o último dia inteiro, até 23:59)
    public List<OrdemServico> relatorioPeriodo(LocalDate inicio, LocalDate fimExclusivo) throws SQLException {
        String sql = "SELECT * FROM vw_os_resumo "
                   + "WHERE data_abertura >= ? AND data_abertura < ? "
                   + "ORDER BY data_abertura";
        List<OrdemServico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, inicio);
            ps.setObject(2, fimExclusivo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdemServico os = new OrdemServico();
                    os.setIdOs(rs.getInt("id_os"));
                    os.setDataAbertura(rs.getObject("data_abertura", LocalDateTime.class));
                    os.setStatusAtual(rs.getString("status_atual"));
                    os.setNomeCliente(rs.getString("cliente"));
                    os.setPlaca(rs.getString("placa"));
                    os.setNomeFuncionario(rs.getString("responsavel"));
                    os.setValorTotal(rs.getBigDecimal("valor_total"));
                    lista.add(os);
                }
            }
        }
        return lista;
    }

    // R02 - Quantidade de OS e total por status, no mesmo período do R01
    // OS cancelada entra com total 0 (valor orçado não é faturamento)
    public List<ResumoStatus> resumoPorStatus(LocalDate inicio, LocalDate fimExclusivo) throws SQLException {
        String sql = "SELECT o.status_atual, "
                   + "COUNT(DISTINCT o.id_os) AS qtd_os, "
                   + "COALESCE(SUM(CASE WHEN o.status_atual = 'cancelada' THEN 0 "
                   + "ELSE os.valor_cobrado END), 0) AS total "
                   + "FROM ordem_servico o "
                   + "LEFT JOIN os_servico os ON os.id_os = o.id_os "
                   + "WHERE o.data_abertura >= ? AND o.data_abertura < ? "
                   + "GROUP BY o.status_atual";
        List<ResumoStatus> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, inicio);
            ps.setObject(2, fimExclusivo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ResumoStatus(
                        rs.getString("status_atual"),
                        rs.getInt("qtd_os"),
                        rs.getBigDecimal("total")
                    ));
                }
            }
        }
        return lista;
    }
}

package br.ucb.oficina.dao;

import br.ucb.oficina.model.OrdemServico;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela ordem_servico (cabeçalho da OS). Aqui só tem SQL, nenhuma regra.
 * Os itens (serviços) da OS ficam no ItemOSDAO.
 * Erros comuns que o Controller trata:
 *   1452 = veículo ou funcionário inexistente (FK)
 *   3819 = alguma CHECK do banco falhou (ex.: data prevista antes da abertura)
 */
public class OrdemServicoDAO {

    // Grava uma OS nova e DEVOLVE o id gerado pelo banco
    // (a tela precisa do id para já poder lançar os serviços da OS)
    // data_abertura não entra no INSERT: o banco coloca a data/hora atual sozinho
    public int inserir(OrdemServico os) throws SQLException {
        String sql = "INSERT INTO ordem_servico (id_veiculo, id_funcionario_responsavel, "
                   + "data_conclusao_prevista, data_conclusao_real, km_entrada, status_atual, "
                   + "observacoes_gerais) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             // RETURN_GENERATED_KEYS = pede para o banco devolver o id criado (AUTO_INCREMENT)
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherCampos(ps, os); // parâmetros 1 a 7
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Lista as OS (mais nova primeiro) já com a placa e o nome do responsável (JOIN)
    public List<OrdemServico> listar() throws SQLException {
        String sql = "SELECT o.*, v.placa, f.nome AS nome_funcionario "
                   + "FROM ordem_servico o "
                   + "JOIN veiculo v ON v.id_veiculo = o.id_veiculo "
                   + "JOIN funcionario f ON f.id_funcionario = o.id_funcionario_responsavel "
                   + "ORDER BY o.id_os DESC";
        List<OrdemServico> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrdemServico os = new OrdemServico();
                os.setIdOs(rs.getInt("id_os"));
                os.setIdVeiculo(rs.getInt("id_veiculo"));
                os.setIdFuncionarioResponsavel(rs.getInt("id_funcionario_responsavel"));
                os.setDataAbertura(rs.getObject("data_abertura", LocalDateTime.class));
                os.setDataConclusaoPrevista(rs.getObject("data_conclusao_prevista", LocalDate.class));
                os.setDataConclusaoReal(rs.getObject("data_conclusao_real", LocalDateTime.class));
                os.setKmEntrada(rs.getInt("km_entrada"));
                os.setStatusAtual(rs.getString("status_atual"));
                os.setObservacoesGerais(rs.getString("observacoes_gerais"));
                os.setPlaca(rs.getString("placa"));
                os.setNomeFuncionario(rs.getString("nome_funcionario"));
                lista.add(os);
            }
        }
        return lista;
    }

    // Altera uma OS que já existe (data_abertura nunca muda)
    public void alterar(OrdemServico os) throws SQLException {
        String sql = "UPDATE ordem_servico SET id_veiculo = ?, id_funcionario_responsavel = ?, "
                   + "data_conclusao_prevista = ?, data_conclusao_real = ?, km_entrada = ?, "
                   + "status_atual = ?, observacoes_gerais = ? WHERE id_os = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, os);     // parâmetros 1 a 7
            ps.setInt(8, os.getIdOs());  // parâmetro 8 = WHERE
            ps.executeUpdate();
        }
    }

    // Exclui a OS. Os itens dela somem junto (ON DELETE CASCADE no os_servico)
    public void remover(int idOs) throws SQLException {
        String sql = "DELETE FROM ordem_servico WHERE id_os = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idOs);
            ps.executeUpdate();
        }
    }

    // Coloca os 7 campos no SQL, na MESMA ordem do INSERT e do UPDATE
    private void preencherCampos(PreparedStatement ps, OrdemServico os) throws SQLException {
        ps.setInt(1, os.getIdVeiculo());
        ps.setInt(2, os.getIdFuncionarioResponsavel());
        ps.setObject(3, os.getDataConclusaoPrevista(), Types.DATE);   // setObject aceita null
        ps.setObject(4, os.getDataConclusaoReal(), Types.TIMESTAMP);  // null = ainda não concluída
        ps.setInt(5, os.getKmEntrada());
        ps.setString(6, os.getStatusAtual());
        ps.setString(7, os.getObservacoesGerais());
    }
}

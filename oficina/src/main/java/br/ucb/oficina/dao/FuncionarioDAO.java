package br.ucb.oficina.dao;

import br.ucb.oficina.model.Funcionario;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela funcionario. Aqui só tem SQL, nenhuma regra.
 * Erros comuns que o Controller trata:
 *   1062 = CPF já cadastrado
 *   1451 = funcionário é responsável por alguma OS (FK), então não pode ser excluído
 */
public class FuncionarioDAO {

    // Grava um funcionário novo
    public void inserir(Funcionario f) throws SQLException {
        String sql = "INSERT INTO funcionario (nome, cpf, cargo, data_admissao, data_desligamento, salario) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, f); // parâmetros 1 a 6
            ps.executeUpdate();
        }
    }

    // Lista todos os funcionários em ordem alfabética
    public List<Funcionario> listar() throws SQLException {
        String sql = "SELECT * FROM funcionario ORDER BY nome";
        List<Funcionario> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setIdFuncionario(rs.getInt("id_funcionario"));
                f.setNome(rs.getString("nome"));
                f.setCpf(rs.getString("cpf"));
                f.setCargo(rs.getString("cargo"));
                // getObject(..., LocalDate.class) devolve null quando a data é NULL no banco
                f.setDataAdmissao(rs.getObject("data_admissao", LocalDate.class));
                f.setDataDesligamento(rs.getObject("data_desligamento", LocalDate.class));
                f.setSalario(rs.getBigDecimal("salario"));
                lista.add(f);
            }
        }
        return lista;
    }

    // Altera um funcionário que já existe
    public void alterar(Funcionario f) throws SQLException {
        String sql = "UPDATE funcionario SET nome = ?, cpf = ?, cargo = ?, data_admissao = ?, "
                   + "data_desligamento = ?, salario = ? WHERE id_funcionario = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, f);              // parâmetros 1 a 6
            ps.setInt(7, f.getIdFuncionario());  // parâmetro 7 = WHERE
            ps.executeUpdate();
        }
    }

    // Exclui o funcionário (o banco bloqueia se ele tiver OS: erro 1451)
    public void remover(int idFuncionario) throws SQLException {
        String sql = "DELETE FROM funcionario WHERE id_funcionario = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFuncionario);
            ps.executeUpdate();
        }
    }

    // Coloca os 6 campos no SQL, na MESMA ordem do INSERT e do UPDATE
    private void preencherCampos(PreparedStatement ps, Funcionario f) throws SQLException {
        ps.setString(1, f.getNome());
        ps.setString(2, f.getCpf());
        ps.setString(3, f.getCargo());
        ps.setObject(4, f.getDataAdmissao(), Types.DATE);
        ps.setObject(5, f.getDataDesligamento(), Types.DATE); // setObject aceita null (= funcionário ativo)
        ps.setBigDecimal(6, f.getSalario());
    }
}

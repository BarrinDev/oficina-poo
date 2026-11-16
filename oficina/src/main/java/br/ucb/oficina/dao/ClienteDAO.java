package br.ucb.oficina.dao;

import br.ucb.oficina.model.Cliente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso à tabela cliente. Aqui só tem SQL, nenhuma regra.
 * Erros comuns que o Controller trata:
 *   1062 = e-mail ou CPF/CNPJ já cadastrado
 *   1451 = cliente tem veículo cadastrado (FK), então não pode ser excluído
 */
public class ClienteDAO {

    // Grava um cliente novo (id_cliente e data_cadastro o banco preenche sozinho)
    public void inserir(Cliente cli) throws SQLException {
        String sql = "INSERT INTO cliente (nome, email, tipo_cliente, cpf_cnpj, logradouro, numero, "
                   + "complemento, bairro, cidade, uf, cep) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, cli); // parâmetros 1 a 11
            ps.executeUpdate();
        }
    }

    // Lista todos os clientes em ordem alfabética
    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // cada linha do resultado vira um objeto Cliente
            while (rs.next()) {
                Cliente cli = new Cliente();
                cli.setIdCliente(rs.getInt("id_cliente"));
                cli.setNome(rs.getString("nome"));
                cli.setEmail(rs.getString("email"));             // NULL no banco vira null no Java
                cli.setTipoCliente(rs.getString("tipo_cliente"));
                cli.setCpfCnpj(rs.getString("cpf_cnpj"));
                cli.setLogradouro(rs.getString("logradouro"));
                cli.setNumero(rs.getString("numero"));
                cli.setComplemento(rs.getString("complemento"));
                cli.setBairro(rs.getString("bairro"));
                cli.setCidade(rs.getString("cidade"));
                cli.setUf(rs.getString("uf"));
                cli.setCep(rs.getString("cep"));
                cli.setDataCadastro(rs.getObject("data_cadastro", LocalDate.class));
                lista.add(cli);
            }
        }
        return lista; // lista vazia se não tiver clientes (nunca null)
    }

    // Altera um cliente que já existe (mesmos campos do INSERT + o id no WHERE)
    public void alterar(Cliente cli) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, email = ?, tipo_cliente = ?, cpf_cnpj = ?, "
                   + "logradouro = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, uf = ?, cep = ? "
                   + "WHERE id_cliente = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencherCampos(ps, cli);            // parâmetros 1 a 11
            ps.setInt(12, cli.getIdCliente());   // parâmetro 12 = WHERE
            ps.executeUpdate();
        }
    }

    // Exclui o cliente (o banco bloqueia se ele tiver veículo: erro 1451)
    public void remover(int idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    // Coloca os 11 campos no SQL, na MESMA ordem do INSERT e do UPDATE
    // (setString aceita null: o campo fica NULL no banco)
    private void preencherCampos(PreparedStatement ps, Cliente cli) throws SQLException {
        ps.setString(1, cli.getNome());
        ps.setString(2, cli.getEmail());
        ps.setString(3, cli.getTipoCliente());
        ps.setString(4, cli.getCpfCnpj());
        ps.setString(5, cli.getLogradouro());
        ps.setString(6, cli.getNumero());
        ps.setString(7, cli.getComplemento());
        ps.setString(8, cli.getBairro());
        ps.setString(9, cli.getCidade());
        ps.setString(10, cli.getUf());
        ps.setString(11, cli.getCep());
    }
}

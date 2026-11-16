package br.ucb.oficina.controller;

import br.ucb.oficina.dao.ClienteDAO;
import br.ucb.oficina.model.Cliente;
import java.sql.SQLException;
import java.util.List;

/**
 * Regras do cadastro de clientes.
 * Recebe o texto dos campos da View, limpa/valida e chama o DAO.
 * Todo erro sai como Exception com mensagem pronta:
 * a View só faz JOptionPane.showMessageDialog(this, e.getMessage()).
 */
public class ClienteController {

    private final ClienteDAO dao = new ClienteDAO();

    // Lista todos os clientes (também usado no combo de dono da tela de Veículo)
    public List<Cliente> listar() throws Exception {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Valida e salva. id = 0 -> cliente novo (INSERT); id > 0 -> alteração (UPDATE)
    public void salvar(int id, String nome, String email, String tipo, String cpfCnpj,
                       String logradouro, String numero, String complemento,
                       String bairro, String cidade, String uf, String cep) throws Exception {
        Cliente cli = new Cliente();
        cli.setIdCliente(id);
        cli.setNome(obrigatorio(nome, "Nome"));
        cli.setTipoCliente(tipo); // vem de um combo (PF/PJ), então sempre é válido

        // CPF/CNPJ: aceita digitado com ponto e traço, mas grava só os números
        String documento = soNumeros(cpfCnpj);
        if (tipo.equals("PF") && documento.length() != 11) {
            throw new Exception("CPF deve ter 11 números.");
        }
        if (tipo.equals("PJ") && documento.length() != 14) {
            throw new Exception("CNPJ deve ter 14 números.");
        }
        cli.setCpfCnpj(documento);

        // E-mail é opcional, mas se for preenchido precisa ter o formato algo@algo.algo
        String mail = opcional(email);
        if (mail != null && !mail.matches(".+@.+\\..+")) {
            throw new Exception("E-mail inválido.");
        }
        cli.setEmail(mail);

        cli.setLogradouro(obrigatorio(logradouro, "Logradouro"));
        cli.setNumero(obrigatorio(numero, "Número"));
        cli.setComplemento(opcional(complemento));
        cli.setBairro(obrigatorio(bairro, "Bairro"));
        cli.setCidade(obrigatorio(cidade, "Cidade"));

        // UF: 2 letras, sempre em maiúsculas (o banco tem CHECK para isso)
        String sigla = obrigatorio(uf, "UF").toUpperCase();
        if (!sigla.matches("[A-Z]{2}")) {
            throw new Exception("UF deve ter 2 letras (ex.: DF).");
        }
        cli.setUf(sigla);

        // CEP: grava só os números (8 dígitos)
        String numerosCep = soNumeros(cep);
        if (numerosCep.length() != 8) {
            throw new Exception("CEP deve ter 8 números.");
        }
        cli.setCep(numerosCep);

        try {
            if (id == 0) {
                dao.inserir(cli);
            } else {
                dao.alterar(cli);
            }
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Exclui o cliente selecionado (a View pede confirmação antes de chamar)
    public void remover(int id) throws Exception {
        if (id == 0) {
            throw new Exception("Selecione um cliente na tabela.");
        }
        try {
            dao.remover(id);
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // ----- métodos de apoio -----

    // Campo obrigatório: tira os espaços das pontas; se ficar vazio -> erro
    private String obrigatorio(String texto, String campo) throws Exception {
        if (texto == null || texto.isBlank()) {
            throw new Exception("Preencha o campo " + campo + ".");
        }
        return texto.trim();
    }

    // Campo opcional: vazio vira null (no banco fica NULL)
    private String opcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }

    // Deixa só os números: "123.456.789-09" -> "12345678909"
    private String soNumeros(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replaceAll("[^0-9]", "");
    }

    // Converte o código de erro do MySQL em mensagem para o usuário
    private String traduzirErro(SQLException e) {
        switch (e.getErrorCode()) {
            case 1062:
                // a mensagem do MySQL diz qual UNIQUE quebrou (uq_cliente_email ou uq_cliente_cpf_cnpj)
                if (e.getMessage().contains("email")) {
                    return "Já existe um cliente com este e-mail.";
                }
                return "Já existe um cliente com este CPF/CNPJ.";
            case 1451: return "Este cliente tem veículo cadastrado e não pode ser excluído.";
            case 1406: return "Algum campo passou do tamanho máximo.";
            default:   return "Erro no banco (" + e.getErrorCode() + "): " + e.getMessage();
        }
    }
}

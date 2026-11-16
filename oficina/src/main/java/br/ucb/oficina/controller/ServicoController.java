package br.ucb.oficina.controller;

import br.ucb.oficina.dao.ServicoDAO;
import br.ucb.oficina.model.Servico;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Regras do catálogo de serviços.
 * Recebe o texto dos campos da View, converte/valida e chama o DAO.
 */
public class ServicoController {

    private final ServicoDAO dao = new ServicoDAO();

    // Lista todos os serviços (também usado no combo do painel de itens da OS)
    public List<Servico> listar() throws Exception {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Valida e salva. id = 0 -> serviço novo (INSERT); id > 0 -> alteração (UPDATE)
    public void salvar(int id, String nome, String descricao, String preco,
                       String tempo, String garantia) throws Exception {
        Servico s = new Servico();
        s.setIdServico(id);
        s.setNomeServico(obrigatorio(nome, "Nome"));

        // Descrição é opcional: vazio vira null
        if (descricao == null || descricao.isBlank()) {
            s.setDescricao(null);
        } else {
            s.setDescricao(descricao.trim());
        }

        // Preço: "150,00" ou "150.00"; precisa ser maior que zero
        BigDecimal valor;
        try {
            valor = new BigDecimal(obrigatorio(preco, "Preço").replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Preço inválido. Use o formato 150,00.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) { // BigDecimal não aceita "valor <= 0"
            throw new Exception("O preço deve ser maior que zero.");
        }
        s.setPrecoTabela(valor);

        // Tempo estimado em minutos: maior que zero
        int minutos = converterInteiro(tempo, "Tempo estimado");
        if (minutos <= 0) {
            throw new Exception("O tempo estimado deve ser maior que zero.");
        }
        s.setTempoEstimadoMinutos(minutos);

        // Garantia: vazio = 90 dias (padrão do banco); máximo 730 dias (2 anos)
        int dias = 90;
        if (garantia != null && !garantia.isBlank()) {
            dias = converterInteiro(garantia, "Garantia");
        }
        if (dias < 0 || dias > 730) {
            throw new Exception("A garantia deve ser de 0 a 730 dias.");
        }
        s.setGarantiaDias(dias);

        try {
            if (id == 0) {
                dao.inserir(s);
            } else {
                dao.alterar(s);
            }
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Exclui o serviço selecionado (a View pede confirmação antes de chamar)
    public void remover(int id) throws Exception {
        if (id == 0) {
            throw new Exception("Selecione um serviço na tabela.");
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

    // Texto -> número inteiro; vazio ou com letra -> erro
    private int converterInteiro(String texto, String campo) throws Exception {
        try {
            return Integer.parseInt(obrigatorio(texto, campo));
        } catch (NumberFormatException e) {
            throw new Exception(campo + " deve ser um número inteiro.");
        }
    }

    // Converte o código de erro do MySQL em mensagem para o usuário
    private String traduzirErro(SQLException e) {
        switch (e.getErrorCode()) {
            case 1062: return "Já existe um serviço com este nome.";
            case 1451: return "Este serviço já foi usado em OS e não pode ser excluído.";
            case 1406: return "Algum campo passou do tamanho máximo.";
            case 1264: return "Algum número passou do limite permitido.";
            default:   return "Erro no banco (" + e.getErrorCode() + "): " + e.getMessage();
        }
    }
}

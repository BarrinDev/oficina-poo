package br.ucb.oficina.controller;

import br.ucb.oficina.dao.ItemOSDAO;
import br.ucb.oficina.model.ItemOS;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Regras dos itens da OS (os_servico).
 * Recebe texto da View, converte/valida e chama o DAO.
 * Todo erro sai como Exception com mensagem pronta:
 * a View só faz JOptionPane.showMessageDialog(this, e.getMessage()).
 */
public class ItemOSController {
    
    private final ItemOSDAO dao = new ItemOSDAO();

    // Lista os serviços da OS (lista vazia se não houver itens)
    public List<ItemOS> listar(int idOs) throws Exception {
        try {
            return dao.listarPorOs(idOs);
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Valida e adiciona um serviço à OS (a OS precisa já estar salva)
    public void adicionar(int idOs, int idServico, String valorTexto, String tempoTexto) throws Exception {
        BigDecimal valor = converterValor(valorTexto);
        Integer tempo = converterTempo(tempoTexto);
        try {
            dao.inserir(new ItemOS(idOs, idServico, valor, tempo));
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Valida e altera valor/tempo de um item; trocar o serviço = remover + adicionar
    public void alterar(int idOs, int idServico, String valorTexto, String tempoTexto) throws Exception {
        BigDecimal valor = converterValor(valorTexto);
        Integer tempo = converterTempo(tempoTexto);
        try {
            dao.alterar(new ItemOS(idOs, idServico, valor, tempo));
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }
    
    // Remove o item da OS (a View deve pedir confirmação antes de chamar)
    public void remover(int idOs, int idServico) throws Exception {
        try {
            dao.remover(idOs, idServico);
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // "150,00" ou "150.00" -> BigDecimal; vazio ou negativo -> erro
    private BigDecimal converterValor(String texto) throws Exception {
        if (texto == null || texto.isBlank()) {
            throw new Exception("Informe o valor cobrado.");
        }
        try {
            BigDecimal v = new BigDecimal(texto.trim().replace(",", "."));
            if (v.compareTo(BigDecimal.ZERO) < 0) { // BigDecimal não aceita "v < 0"
                throw new Exception("O valor não pode ser negativo.");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new Exception("Valor inválido. Use o formato 150,00.");
        }
    }

    // Vazio -> null (serviço ainda não executado); inválido ou negativo -> erro
    private Integer converterTempo(String texto) throws Exception {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        int t;
        try {
            t = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Tempo inválido. Informe os minutos (ex.: 45).");
        }
        if (t < 0) {
            throw new Exception("O tempo não pode ser negativo.");
        }
        return t;
    }

    // Converte o código de erro do MySQL em mensagem para o usuário
    private String traduzirErro(SQLException e) {
        switch (e.getErrorCode()) {
            case 1062: return "Este serviço já foi adicionado nesta OS.";
            case 1452: return "OS ou serviço não encontrado. Atualize a tela e tente novamente.";
            default:   return "Erro no banco (" + e.getErrorCode() + "): " + e.getMessage();
        }
    }
}
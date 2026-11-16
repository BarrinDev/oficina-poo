package br.ucb.oficina.controller;

import br.ucb.oficina.dao.OrdemServicoDAO;
import br.ucb.oficina.model.Funcionario;
import br.ucb.oficina.model.ItemOS;
import br.ucb.oficina.model.OrdemServico;
import br.ucb.oficina.model.Veiculo;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Regras da Ordem de Serviço (cabeçalho).
 * Os serviços da OS são tratados no ItemOSController.
 *
 * Regras de negócio:
 *  - OS nova sempre começa com status "aberta"
 *  - funcionário desligado não pode abrir OS nova
 *  - data prevista de uma OS nova não pode ser antes de hoje
 *  - status "concluida" grava a data/hora de conclusão; os outros status deixam ela vazia
 *  - OS concluída não pode ser excluída (é histórico do que foi faturado)
 */
public class OrdemServicoController {

    private final OrdemServicoDAO dao = new OrdemServicoDAO();

    // Formatos de data usados nas telas
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Lista todas as OS (mais nova primeiro)
    public List<OrdemServico> listar() throws Exception {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Valida e salva a OS. Devolve o número (id) da OS salva.
    // atual = OS selecionada na tabela (null = OS nova)
    // veiculo e responsavel = itens escolhidos nos combos (null se nada escolhido)
    public int salvar(OrdemServico atual, Veiculo veiculo, Funcionario responsavel, String status,
                      String prevista, String km, String observacoes) throws Exception {
        boolean nova = (atual == null);

        if (veiculo == null) {
            throw new Exception("Selecione o veículo.");
        }
        if (responsavel == null) {
            throw new Exception("Selecione o funcionário responsável.");
        }
        if (nova && responsavel.getDataDesligamento() != null) {
            throw new Exception("Funcionário desligado não pode abrir OS.");
        }
        if (nova && !status.equals("aberta")) {
            throw new Exception("Uma OS nova sempre começa com status 'aberta'.");
        }

        // Data prevista: opcional, formato dd/mm/aaaa
        LocalDate dataPrevista = null;
        if (prevista != null && !prevista.isBlank()) {
            try {
                dataPrevista = LocalDate.parse(prevista.trim(), DATA);
            } catch (DateTimeParseException e) {
                throw new Exception("Data prevista inválida. Use dd/mm/aaaa.");
            }
        }
        if (nova && dataPrevista != null && dataPrevista.isBefore(LocalDate.now())) {
            throw new Exception("A data prevista não pode ser antes de hoje.");
        }

        // Km de entrada: obrigatório, número inteiro, não negativo
        int kmEntrada;
        try {
            kmEntrada = Integer.parseInt(km.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Km de entrada deve ser um número inteiro.");
        }
        if (kmEntrada < 0) {
            throw new Exception("Km de entrada não pode ser negativo.");
        }

        // Monta o objeto que vai para o DAO
        OrdemServico os = new OrdemServico();
        os.setIdVeiculo(veiculo.getIdVeiculo());
        os.setIdFuncionarioResponsavel(responsavel.getIdFuncionario());
        os.setDataConclusaoPrevista(dataPrevista);
        os.setKmEntrada(kmEntrada);
        os.setStatusAtual(status);
        if (observacoes == null || observacoes.isBlank()) {
            os.setObservacoesGerais(null);
        } else {
            os.setObservacoesGerais(observacoes.trim());
        }

        // Status "concluida" precisa da data de conclusão (o banco tem CHECK para isso)
        if (status.equals("concluida")) {
            if (!nova && atual.getDataConclusaoReal() != null) {
                os.setDataConclusaoReal(atual.getDataConclusaoReal()); // já estava concluída: mantém a data
            } else {
                os.setDataConclusaoReal(LocalDateTime.now().withNano(0)); // concluindo agora
            }
        }
        // qualquer outro status: data de conclusão fica null

        try {
            if (nova) {
                return dao.inserir(os); // devolve o id que o banco gerou
            }
            os.setIdOs(atual.getIdOs());
            dao.alterar(os);
            return atual.getIdOs();
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Exclui a OS selecionada e os serviços dela (a View pede confirmação antes)
    public void remover(OrdemServico os) throws Exception {
        if (os == null) {
            throw new Exception("Selecione uma OS na tabela.");
        }
        if (os.getStatusAtual().equals("concluida")) {
            throw new Exception("OS concluída não pode ser excluída.");
        }
        try {
            dao.remover(os.getIdOs());
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Total da OS = soma do valor cobrado de cada serviço
    public BigDecimal calcularTotal(List<ItemOS> itens) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemOS item : itens) {
            total = total.add(item.getValorCobrado()); // BigDecimal soma com add(), não com +
        }
        return total;
    }

    // LocalDate -> "dd/mm/aaaa" (null vira texto vazio)
    public String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DATA);
    }

    // LocalDateTime -> "dd/mm/aaaa hh:mm" (null vira texto vazio)
    public String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return dataHora.format(DATA_HORA);
    }

    // Converte o código de erro do MySQL em mensagem para o usuário
    private String traduzirErro(SQLException e) {
        switch (e.getErrorCode()) {
            case 1452: return "Veículo ou funcionário não encontrado. Atualize a tela e tente de novo.";
            case 3819:
                // CHECK que falhou; a mensagem do MySQL traz o nome da constraint
                if (e.getMessage().contains("prevista")) {
                    return "A data prevista não pode ser antes da abertura da OS.";
                }
                return "Dados inválidos: " + e.getMessage();
            case 1406: return "As observações passaram do tamanho máximo (500 letras).";
            case 1264: return "Km passou do limite permitido.";
            default:   return "Erro no banco (" + e.getErrorCode() + "): " + e.getMessage();
        }
    }
}

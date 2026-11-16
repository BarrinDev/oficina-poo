package br.ucb.oficina.controller;

import br.ucb.oficina.dao.FuncionarioDAO;
import br.ucb.oficina.model.Funcionario;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Regras do cadastro de funcionários.
 * Recebe o texto dos campos da View, converte/valida e chama o DAO.
 * Datas chegam como texto "dd/mm/aaaa"; salário como "4300,00".
 */
public class FuncionarioController {

    private final FuncionarioDAO dao = new FuncionarioDAO();

    // Formato de data usado nas telas
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Lista todos os funcionários (também usado no combo de responsável da tela de OS)
    public List<Funcionario> listar() throws Exception {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Valida e salva. id = 0 -> funcionário novo (INSERT); id > 0 -> alteração (UPDATE)
    // desligamento vazio = funcionário ativo
    public void salvar(int id, String nome, String cpf, String cargo, String admissao,
                       String desligamento, String salario) throws Exception {
        Funcionario f = new Funcionario();
        f.setIdFuncionario(id);
        f.setNome(obrigatorio(nome, "Nome"));

        // CPF: aceita com ponto e traço, grava só os 11 números
        String numerosCpf = (cpf == null ? "" : cpf).replaceAll("[^0-9]", "");
        if (numerosCpf.length() != 11) {
            throw new Exception("CPF deve ter 11 números.");
        }
        f.setCpf(numerosCpf);

        f.setCargo(obrigatorio(cargo, "Cargo"));

        // Admissão é obrigatória
        LocalDate dataAdmissao = converterData(admissao, "Data de admissão");
        if (dataAdmissao == null) {
            throw new Exception("Preencha a data de admissão.");
        }
        f.setDataAdmissao(dataAdmissao);

        // Desligamento é opcional, mas não pode ser antes da admissão
        LocalDate dataDesligamento = converterData(desligamento, "Data de desligamento");
        if (dataDesligamento != null && dataDesligamento.isBefore(dataAdmissao)) {
            throw new Exception("O desligamento não pode ser antes da admissão.");
        }
        f.setDataDesligamento(dataDesligamento);

        // Salário: "4300,00" ou "4300.00"; precisa ser maior que zero
        BigDecimal valor;
        try {
            valor = new BigDecimal(obrigatorio(salario, "Salário").replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Salário inválido. Use o formato 4300,00.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) { // BigDecimal não aceita "valor <= 0"
            throw new Exception("O salário deve ser maior que zero.");
        }
        f.setSalario(valor);

        try {
            if (id == 0) {
                dao.inserir(f);
            } else {
                dao.alterar(f);
            }
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Exclui o funcionário selecionado (a View pede confirmação antes de chamar)
    public void remover(int id) throws Exception {
        if (id == 0) {
            throw new Exception("Selecione um funcionário na tabela.");
        }
        try {
            dao.remover(id);
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // LocalDate -> "dd/mm/aaaa" para mostrar na tela (null vira texto vazio)
    public String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DATA);
    }

    // ----- métodos de apoio -----

    // Campo obrigatório: tira os espaços das pontas; se ficar vazio -> erro
    private String obrigatorio(String texto, String campo) throws Exception {
        if (texto == null || texto.isBlank()) {
            throw new Exception("Preencha o campo " + campo + ".");
        }
        return texto.trim();
    }

    // "dd/mm/aaaa" -> LocalDate; vazio -> null; formato errado -> erro
    private LocalDate converterData(String texto, String campo) throws Exception {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new Exception(campo + " inválida. Use dd/mm/aaaa.");
        }
    }

    // Converte o código de erro do MySQL em mensagem para o usuário
    private String traduzirErro(SQLException e) {
        switch (e.getErrorCode()) {
            case 1062: return "Já existe um funcionário com este CPF.";
            case 1451: return "Este funcionário é responsável por OS e não pode ser excluído. "
                            + "Preencha a data de desligamento.";
            case 1406: return "Algum campo passou do tamanho máximo.";
            case 1264: return "Algum número passou do limite permitido.";
            default:   return "Erro no banco (" + e.getErrorCode() + "): " + e.getMessage();
        }
    }
}

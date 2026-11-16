package br.ucb.oficina.controller;

import br.ucb.oficina.dao.ConsultaDAO;
import br.ucb.oficina.model.OrdemServico;
import br.ucb.oficina.model.ResumoStatus;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Regras da tela de consulta (JOIN) e do relatório por período.
 * Recebe texto da View (status, datas), valida e chama o ConsultaDAO.
 */
public class ConsultaController {

    private final ConsultaDAO dao = new ConsultaDAO();

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Consulta C01. status vem do combo; "todos" = sem filtro
    public List<OrdemServico> consultarOS(String status) throws Exception {
        String filtro = status.equals("todos") ? null : status; // null = o DAO não filtra
        try {
            return dao.consultarOS(filtro);
        } catch (SQLException e) {
            throw new Exception("Erro no banco (" + e.getErrorCode() + "): " + e.getMessage());
        }
    }

    // Relatório R01: OS abertas entre as duas datas (as duas entram no período)
    public List<OrdemServico> relatorioPeriodo(String inicio, String fim) throws Exception {
        LocalDate dataInicio = converterData(inicio, "Data inicial");
        LocalDate dataFim = converterData(fim, "Data final");
        validarPeriodo(dataInicio, dataFim);
        try {
            // fim + 1 dia porque o SQL usa "< fim": assim o último dia entra inteiro
            return dao.relatorioPeriodo(dataInicio, dataFim.plusDays(1));
        } catch (SQLException e) {
            throw new Exception("Erro no banco (" + e.getErrorCode() + "): " + e.getMessage());
        }
    }

    // Relatório R02: quantidade e total por status no mesmo período
    public List<ResumoStatus> resumoPorStatus(String inicio, String fim) throws Exception {
        LocalDate dataInicio = converterData(inicio, "Data inicial");
        LocalDate dataFim = converterData(fim, "Data final");
        validarPeriodo(dataInicio, dataFim);
        try {
            return dao.resumoPorStatus(dataInicio, dataFim.plusDays(1));
        } catch (SQLException e) {
            throw new Exception("Erro no banco (" + e.getErrorCode() + "): " + e.getMessage());
        }
    }

    // Total faturado do relatório: soma o valor das OS, MENOS as canceladas
    public BigDecimal totalFaturado(List<OrdemServico> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrdemServico os : lista) {
            if (!os.getStatusAtual().equals("cancelada")) {
                total = total.add(os.getValorTotal());
            }
        }
        return total;
    }

    // Primeiro dia do mês atual, para já vir preenchido na tela de relatório
    public String primeiroDiaDoMes() {
        return LocalDate.now().withDayOfMonth(1).format(DATA);
    }

    // Data de hoje, para já vir preenchida na tela de relatório
    public String hoje() {
        return LocalDate.now().format(DATA);
    }

    // LocalDateTime -> "dd/mm/aaaa hh:mm" (null vira texto vazio)
    public String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return dataHora.format(DATA_HORA);
    }

    // ----- métodos de apoio -----

    // "dd/mm/aaaa" -> LocalDate; vazio ou formato errado -> erro
    private LocalDate converterData(String texto, String campo) throws Exception {
        if (texto == null || texto.isBlank()) {
            throw new Exception("Preencha a " + campo.toLowerCase() + ".");
        }
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new Exception(campo + " inválida. Use dd/mm/aaaa.");
        }
    }

    // A data final não pode ser antes da inicial
    private void validarPeriodo(LocalDate inicio, LocalDate fim) throws Exception {
        if (fim.isBefore(inicio)) {
            throw new Exception("A data final não pode ser antes da data inicial.");
        }
    }
}

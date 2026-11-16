package br.ucb.oficina.model;

import java.math.BigDecimal;

/**
 * Uma linha do relatório "resumo por status" (consulta R02):
 * quantas OS tem em cada status e quanto elas somam.
 * Não é uma tabela do banco, é só o resultado de um SELECT com GROUP BY.
 */
public class ResumoStatus {
    private String status;
    private int quantidade;
    private BigDecimal total;

    public ResumoStatus(String status, int quantidade, BigDecimal total) {
        this.status = status;
        this.quantidade = quantidade;
        this.total = total;
    }

    public String getStatus() { return status; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getTotal() { return total; }
}

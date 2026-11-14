package br.ucb.oficina.model;

import java.math.BigDecimal;

/**
 * Item de uma Ordem de Serviço (tabela os_servico).
 * Identificado por idOs + idServico: o mesmo serviço não pode repetir na mesma OS.
 */
public class ItemOS {
    private int idOs;
    private int idServico;
    private String nomeServico;            // só leitura: preenchido pelo DAO (JOIN), não é gravado
    private BigDecimal valorCobrado;       // BigDecimal, não double (dinheiro)
    private Integer tempoGastoMinutos;     // null = serviço ainda não executado

    public ItemOS() { }
    
    // usar para criar um item novo (nomeServico não entra: vem do banco)
    public ItemOS(int idOs, int idServico, BigDecimal valorCobrado, Integer tempoGastoMinutos) {
        this.idOs = idOs;
        this.idServico = idServico;
        this.valorCobrado = valorCobrado;
        this.tempoGastoMinutos = tempoGastoMinutos;
    }

    public int getIdOs() {
        return idOs;
    }

    public void setIdOs(int idOs) {
        this.idOs = idOs;
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public BigDecimal getValorCobrado() {
        return valorCobrado;
    }   

    public void setValorCobrado(BigDecimal valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public Integer getTempoGastoMinutos() {
        return tempoGastoMinutos;
    }

    public void setTempoGastoMinutos(Integer tempoGastoMinutos) {
        this.tempoGastoMinutos = tempoGastoMinutos;
    }
    
}

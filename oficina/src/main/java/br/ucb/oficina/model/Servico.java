package br.ucb.oficina.model;

import java.math.BigDecimal;

/**
 * Serviço do catálogo da oficina (tabela servico).
 * O preço aqui é só o sugerido; o valor cobrado de verdade fica no item da OS (ItemOS).
 */
public class Servico {
    private int idServico;               // 0 = serviço novo, ainda não salvo
    private String nomeServico;
    private String descricao;            // opcional
    private BigDecimal precoTabela;      // BigDecimal, não double (dinheiro)
    private int tempoEstimadoMinutos;
    private int garantiaDias;            // de 0 a 730

    // ----- getters e setters -----
    public int getIdServico() { return idServico; }
    public void setIdServico(int idServico) { this.idServico = idServico; }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPrecoTabela() { return precoTabela; }
    public void setPrecoTabela(BigDecimal precoTabela) { this.precoTabela = precoTabela; }

    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public void setTempoEstimadoMinutos(int tempoEstimadoMinutos) { this.tempoEstimadoMinutos = tempoEstimadoMinutos; }

    public int getGarantiaDias() { return garantiaDias; }
    public void setGarantiaDias(int garantiaDias) { this.garantiaDias = garantiaDias; }

    // Texto do combo de serviço no painel de itens da OS
    @Override
    public String toString() {
        return nomeServico;
    }
}

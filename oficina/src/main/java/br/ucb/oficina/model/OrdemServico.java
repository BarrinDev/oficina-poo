package br.ucb.oficina.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ordem de Serviço - o "cabeçalho" da OS (tabela ordem_servico).
 * Os serviços da OS ficam em outra tabela (os_servico) -> classe ItemOS.
 * Status possíveis: aberta, em_execucao, concluida, cancelada.
 */
public class OrdemServico {
    private int idOs;                          // 0 = OS nova, ainda não salva
    private int idVeiculo;                     // FK para veiculo
    private int idFuncionarioResponsavel;      // FK para funcionario
    private LocalDateTime dataAbertura;        // só leitura: o banco preenche na hora do INSERT
    private LocalDate dataConclusaoPrevista;   // opcional
    private LocalDateTime dataConclusaoReal;   // preenchida quando a OS vira "concluida"
    private int kmEntrada;                     // km do veículo quando chegou na oficina
    private String statusAtual;
    private String observacoesGerais;          // opcional

    // Campos só de leitura: o DAO preenche com JOIN para mostrar nas tabelas (não são gravados)
    private String placa;
    private String modeloVeiculo;
    private String nomeCliente;
    private String nomeFuncionario;
    private BigDecimal valorTotal;             // soma dos itens (usado no relatório)

    // ----- getters e setters -----
    public int getIdOs() { return idOs; }
    public void setIdOs(int idOs) { this.idOs = idOs; }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public int getIdFuncionarioResponsavel() { return idFuncionarioResponsavel; }
    public void setIdFuncionarioResponsavel(int id) { this.idFuncionarioResponsavel = id; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDate getDataConclusaoPrevista() { return dataConclusaoPrevista; }
    public void setDataConclusaoPrevista(LocalDate data) { this.dataConclusaoPrevista = data; }

    public LocalDateTime getDataConclusaoReal() { return dataConclusaoReal; }
    public void setDataConclusaoReal(LocalDateTime data) { this.dataConclusaoReal = data; }

    public int getKmEntrada() { return kmEntrada; }
    public void setKmEntrada(int kmEntrada) { this.kmEntrada = kmEntrada; }

    public String getStatusAtual() { return statusAtual; }
    public void setStatusAtual(String statusAtual) { this.statusAtual = statusAtual; }

    public String getObservacoesGerais() { return observacoesGerais; }
    public void setObservacoesGerais(String obs) { this.observacoesGerais = obs; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModeloVeiculo() { return modeloVeiculo; }
    public void setModeloVeiculo(String modeloVeiculo) { this.modeloVeiculo = modeloVeiculo; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}

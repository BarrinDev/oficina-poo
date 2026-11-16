package br.ucb.oficina.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Funcionário da oficina (tabela funcionario).
 * É ele quem fica responsável pelas ordens de serviço.
 */
public class Funcionario {
    private int idFuncionario;           // 0 = funcionário novo, ainda não salvo
    private String nome;
    private String cpf;                  // só números (11)
    private String cargo;
    private LocalDate dataAdmissao;
    private LocalDate dataDesligamento;  // null = funcionário ativo
    private BigDecimal salario;          // BigDecimal, não double (dinheiro)

    // ----- getters e setters -----
    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }

    public LocalDate getDataDesligamento() { return dataDesligamento; }
    public void setDataDesligamento(LocalDate dataDesligamento) { this.dataDesligamento = dataDesligamento; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    // Texto do combo de responsável na tela de OS. Ex.: "Marcos Vieira Santos (desligado)"
    @Override
    public String toString() {
        if (dataDesligamento != null) {
            return nome + " (desligado)";
        }
        return nome;
    }
}

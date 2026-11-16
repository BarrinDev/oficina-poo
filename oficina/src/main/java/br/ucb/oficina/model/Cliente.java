package br.ucb.oficina.model;

import java.time.LocalDate;

/**
 * Cliente da oficina (tabela cliente).
 * Pode ser PF (CPF com 11 números) ou PJ (CNPJ com 14 números).
 * Model é só uma "caixinha de dados": atributos + getters/setters, sem regra nenhuma.
 */
public class Cliente {
    private int idCliente;           // 0 = cliente novo, ainda não salvo no banco
    private String nome;
    private String email;            // opcional (null = sem e-mail)
    private String tipoCliente;      // "PF" ou "PJ"
    private String cpfCnpj;          // só números
    private String logradouro;
    private String numero;
    private String complemento;      // opcional
    private String bairro;
    private String cidade;
    private String uf;               // 2 letras maiúsculas (ex.: DF)
    private String cep;              // 8 números
    private LocalDate dataCadastro;  // só leitura: o próprio banco preenche (DEFAULT CURRENT_DATE)

    // ----- getters e setters -----
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    // O JComboBox mostra na tela o que o toString() devolve (combo de dono na tela de Veículo)
    @Override
    public String toString() {
        return nome;
    }
}

package br.ucb.oficina.model;

/**
 * Veículo atendido pela oficina (tabela veiculo).
 * Todo veículo pertence a um cliente (FK id_cliente).
 */
public class Veiculo {
    private int idVeiculo;          // 0 = veículo novo, ainda não salvo
    private int idCliente;          // dono do veículo (FK para cliente)
    private String nomeCliente;     // só leitura: o DAO preenche com JOIN, não é gravado
    private String placa;           // ABC1234 (antiga) ou ABC1D23 (Mercosul), maiúsculas
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private int anoModelo;          // igual ao ano de fabricação ou +1
    private String cor;             // opcional
    private int kmAtual;

    // ----- getters e setters -----
    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(int anoFabricacao) { this.anoFabricacao = anoFabricacao; }

    public int getAnoModelo() { return anoModelo; }
    public void setAnoModelo(int anoModelo) { this.anoModelo = anoModelo; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public int getKmAtual() { return kmAtual; }
    public void setKmAtual(int kmAtual) { this.kmAtual = kmAtual; }

    // Texto que aparece no combo de veículo da tela de OS. Ex.: "JKL4A21 - Argo (Maria Silva Souza)"
    @Override
    public String toString() {
        return placa + " - " + modelo + " (" + nomeCliente + ")";
    }
}

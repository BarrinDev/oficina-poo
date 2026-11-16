package br.ucb.oficina.controller;

import br.ucb.oficina.dao.VeiculoDAO;
import br.ucb.oficina.model.Cliente;
import br.ucb.oficina.model.Veiculo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras do cadastro de veículos.
 * Recebe o texto dos campos da View, converte/valida e chama o DAO.
 * Todo erro sai como Exception com mensagem pronta para o JOptionPane.
 */
public class VeiculoController {

    private final VeiculoDAO dao = new VeiculoDAO();

    // Lista todos os veículos (também usado no combo de veículo da tela de OS)
    public List<Veiculo> listar() throws Exception {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Valida e salva. id = 0 -> veículo novo (INSERT); id > 0 -> alteração (UPDATE)
    // "dono" é o Cliente escolhido no combo (null se nada foi escolhido)
    public void salvar(int id, Cliente dono, String placa, String marca, String modelo,
                       String anoFabricacao, String anoModelo, String cor, String km) throws Exception {
        if (dono == null) {
            throw new Exception("Selecione o dono do veículo.");
        }

        // Placa: tira traço e espaço, deixa maiúscula e confere o formato
        // Antiga: ABC1234 | Mercosul: ABC1D23
        String p = (placa == null ? "" : placa).replace("-", "").replace(" ", "").toUpperCase();
        if (!p.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
            throw new Exception("Placa inválida. Use ABC1234 ou ABC1D23.");
        }

        // Anos: fabricação entre 1950 e o ano que vem; modelo = fabricação ou fabricação + 1
        int fab = converterInteiro(anoFabricacao, "Ano de fabricação");
        int anoQueVem = LocalDate.now().getYear() + 1;
        if (fab < 1950 || fab > anoQueVem) {
            throw new Exception("Ano de fabricação deve ser entre 1950 e " + anoQueVem + ".");
        }
        int mod = converterInteiro(anoModelo, "Ano do modelo");
        if (mod != fab && mod != fab + 1) {
            throw new Exception("Ano do modelo deve ser igual ao de fabricação ou 1 a mais.");
        }

        // Km: vazio vira 0; não pode ser negativo
        int quilometragem = 0;
        if (km != null && !km.isBlank()) {
            quilometragem = converterInteiro(km, "Km atual");
        }
        if (quilometragem < 0) {
            throw new Exception("Km não pode ser negativo.");
        }

        Veiculo v = new Veiculo();
        v.setIdVeiculo(id);
        v.setIdCliente(dono.getIdCliente());
        v.setPlaca(p);
        v.setMarca(obrigatorio(marca, "Marca"));
        v.setModelo(obrigatorio(modelo, "Modelo"));
        v.setAnoFabricacao(fab);
        v.setAnoModelo(mod);
        v.setCor(opcional(cor));
        v.setKmAtual(quilometragem);

        try {
            if (id == 0) {
                dao.inserir(v);
            } else {
                dao.alterar(v);
            }
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // Exclui o veículo selecionado (a View pede confirmação antes de chamar)
    public void remover(int id) throws Exception {
        if (id == 0) {
            throw new Exception("Selecione um veículo na tabela.");
        }
        try {
            dao.remover(id);
        } catch (SQLException e) {
            throw new Exception(traduzirErro(e));
        }
    }

    // ----- métodos de apoio -----

    // Campo obrigatório: tira os espaços das pontas; se ficar vazio -> erro
    private String obrigatorio(String texto, String campo) throws Exception {
        if (texto == null || texto.isBlank()) {
            throw new Exception("Preencha o campo " + campo + ".");
        }
        return texto.trim();
    }

    // Campo opcional: vazio vira null (no banco fica NULL)
    private String opcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }

    // Texto -> número inteiro; vazio ou com letra -> erro
    private int converterInteiro(String texto, String campo) throws Exception {
        try {
            return Integer.parseInt(obrigatorio(texto, campo));
        } catch (NumberFormatException e) {
            throw new Exception(campo + " deve ser um número inteiro.");
        }
    }

    // Converte o código de erro do MySQL em mensagem para o usuário
    private String traduzirErro(SQLException e) {
        switch (e.getErrorCode()) {
            case 1062: return "Já existe um veículo com esta placa.";
            case 1451: return "Este veículo tem ordem de serviço e não pode ser excluído.";
            case 1452: return "Cliente não encontrado. Atualize a tela e tente de novo.";
            case 1406: return "Algum campo passou do tamanho máximo.";
            case 1264: return "Algum número passou do limite permitido.";
            default:   return "Erro no banco (" + e.getErrorCode() + "): " + e.getMessage();
        }
    }
}

package br.com.gerenciamentoteatro.repository;

import br.com.gerenciamentoteatro.dao.ContratoAluguelDAO;
import br.com.gerenciamentoteatro.model.ContratoAluguel;

import java.util.List;
import java.util.Optional;

public class ContratoAluguelRepository {

    private final ContratoAluguelDAO dao = new ContratoAluguelDAO();

    public void salvar(ContratoAluguel contrato) {
        if (contrato.getId() == null) {
            dao.salvar(contrato);
        } else {
            dao.atualizar(contrato);
        }
    }

    public List<ContratoAluguel> listarTodos() {
        return dao.listarTodos();
    }

    public Optional<ContratoAluguel> buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }
}
package br.com.gerenciamentoteatro.repository;

import br.com.gerenciamentoteatro.dao.PecaDAO;
import br.com.gerenciamentoteatro.model.Peca;

import java.util.List;
import java.util.Optional;

public class PecaRepository {

    private final PecaDAO dao = new PecaDAO();

    public void salvar(Peca peca) {
        if (peca.getId() == null) {
            dao.salvar(peca);
        } else {
            dao.atualizar(peca);
        }
    }

    public List<Peca> listarTodas() {
        return dao.listarTodos();
    }

    public Optional<Peca> buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }
}
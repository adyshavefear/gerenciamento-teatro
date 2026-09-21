package br.com.gerenciamentoteatro.repository;

import br.com.gerenciamentoteatro.dao.RegraPrecoDAO;
import br.com.gerenciamentoteatro.model.RegraPreco;

import java.util.List;
import java.util.Optional;

public class RegraPrecoRepository {

    private final RegraPrecoDAO dao = new RegraPrecoDAO();

    public void salvar(RegraPreco regra) {
        if (regra.getId() == null) {
            dao.salvar(regra);
        } else {
            dao.atualizar(regra);
        }
    }

    public List<RegraPreco> listarTodas() {
        return dao.listarTodos();
    }

    public List<RegraPreco> listarTodos() {
        return dao.listarTodos();
    }

    public Optional<RegraPreco> buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public void excluir(Long id) {
        dao.excluir(id);
    }
}
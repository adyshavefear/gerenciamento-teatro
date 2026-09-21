package br.com.gerenciamentoteatro.repository;

import br.com.gerenciamentoteatro.dao.ArtistaDAO;
import br.com.gerenciamentoteatro.model.Artista;

import java.util.List;
import java.util.Optional;

public class ArtistaRepository {

    private final ArtistaDAO dao = new ArtistaDAO();

    public Artista salvar(Artista artista) {
        if (artista.getId() == null) {
            return dao.salvar(artista);
        } else {
            return dao.atualizar(artista);
        }
    }

    public Optional<Artista> buscarPorCpf(String cpf) {
        return dao.buscarPorCpf(cpf);
    }

    public List<Artista> listarTodos() {
        return dao.listarTodos();
    }
}
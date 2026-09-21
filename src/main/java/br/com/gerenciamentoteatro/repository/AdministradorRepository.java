package br.com.gerenciamentoteatro.repository;

import br.com.gerenciamentoteatro.dao.AdministradorDAO;
import br.com.gerenciamentoteatro.model.Administrador;

import java.util.Optional;

public class AdministradorRepository {

    private final AdministradorDAO dao = new AdministradorDAO();

    public void salvar(Administrador admin) {
        if (admin.getId() == null) {
            dao.salvar(admin);
        } else {
            dao.atualizar(admin);
        }
    }

    public Optional<Administrador> buscarPorEmail(String email) {
        return dao.buscarPorEmail(email);
    }

    public Optional<Administrador> buscarUnico() {
        return dao.listarTodos().stream().findFirst();
    }

    public boolean existeAdministrador() {
        return dao.existeAdministrador();
    }
}
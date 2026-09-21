package br.com.gerenciamentoteatro.repository;

import br.com.gerenciamentoteatro.dao.ClienteDAO;
import br.com.gerenciamentoteatro.model.Cliente;

import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    private final ClienteDAO dao = new ClienteDAO();

    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            return dao.salvar(cliente);
        } else {
            return dao.atualizar(cliente);
        }
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return dao.buscarPorCpf(cpf);
    }

    public List<Cliente> listarTodos() {
        return dao.listarTodos();
    }
}
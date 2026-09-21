package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.Cliente;
import br.com.gerenciamentoteatro.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente cadastrarOuObter(Cliente cliente) {
        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }

        Optional<Cliente> existente = repository.buscarPorCpf(cliente.getCpf());
        if (existente.isPresent()) {
            return existente.get();
        }

        if (cliente.getNomeCompleto() == null || cliente.getNomeCompleto().isBlank()) {
            throw new IllegalArgumentException("Nome completo é obrigatório para novos clientes.");
        }

        repository.salvar(cliente);
        return cliente;
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public List<Cliente> listarTodos() {
        return repository.listarTodos();
    }
}
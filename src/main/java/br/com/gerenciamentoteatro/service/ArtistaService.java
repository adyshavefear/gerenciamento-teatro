package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.Artista;
import br.com.gerenciamentoteatro.repository.ArtistaRepository;

import java.util.List;
import java.util.Optional;

public class ArtistaService {

    private final ArtistaRepository repository;

    public ArtistaService(ArtistaRepository repository) {
        this.repository = repository;
    }

    public Artista cadastrarOuObter(Artista artista) {
        if (artista.getCpf() == null || artista.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }

        Optional<Artista> existente = repository.buscarPorCpf(artista.getCpf());
        if (existente.isPresent()) {
            return existente.get();
        }

        if (artista.getNomeCompleto() == null || artista.getNomeCompleto().isBlank()) {
            throw new IllegalArgumentException("Nome completo é obrigatório para novos artistas.");
        }

        repository.salvar(artista);
        return artista;
    }

    public Optional<Artista> buscarPorCpf(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public List<Artista> listarTodos() {
        return repository.listarTodos();
    }
}
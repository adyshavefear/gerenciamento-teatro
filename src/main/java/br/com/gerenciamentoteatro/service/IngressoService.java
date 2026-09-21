package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.model.Cliente;
import br.com.gerenciamentoteatro.model.Ingresso;
import br.com.gerenciamentoteatro.model.ItemListaPresenca;
import br.com.gerenciamentoteatro.repository.IngressoRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngressoService {

    private final IngressoRepository repository;

    public IngressoService(IngressoRepository repository) {
        this.repository = repository;
    }

    public void registrarVenda(Ingresso ingresso) {
        if (ingresso.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade de ingressos deve ser maior que zero.");
        }
        repository.salvar(ingresso);
    }

    /**
     * Requisito 12: Gera a lista de presença para uma peça com filtragem por dia (opcional).
     * Consolida a quantidade de ingressos comprados por cada cliente.
     */
    public List<ItemListaPresenca> gerarListaPresenca(Long pecaId, LocalDate dataFiltro) {
        List<Ingresso> ingressos = repository.listarTodos();
        Map<String, ItemListaPresencaHelper> consolidado = new HashMap<>();

        for (Ingresso ing : ingressos) {
            // Filtra por peça
            boolean mesmaPeca = ing.getPeca().getId().equals(pecaId);
            // Filtra por data (se o filtro for informado)
            boolean mesmaData = (dataFiltro == null) || ing.getDataExibicao().equals(dataFiltro);

            if (mesmaPeca && mesmaData) {
                Cliente cliente = ing.getCliente();
                String cpf = cliente.getCpf();

                if (consolidado.containsKey(cpf)) {
                    consolidado.get(cpf).adicionarIngressos(ing.getQuantidade());
                } else {
                    consolidado.put(cpf, new ItemListaPresencaHelper(cliente, ing.getQuantidade()));
                }
            }
        }

        List<ItemListaPresenca> resultado = new ArrayList<>();
        for (ItemListaPresencaHelper helper : consolidado.values()) {
            resultado.add(new ItemListaPresenca(
                    helper.cliente.getNomeCompleto(),
                    helper.cliente.getCpf(),
                    helper.cliente.getEmail(),
                    helper.totalIngressos
            ));
        }

        return resultado;
    }

    // Classe auxiliar interna para contagem acumulativa
    private static class ItemListaPresencaHelper {
        Cliente cliente;
        int totalIngressos;

        ItemListaPresencaHelper(Cliente cliente, int totalIngressos) {
            this.cliente = cliente;
            this.totalIngressos = totalIngressos;
        }

        void adicionarIngressos(int qtd) {
            this.totalIngressos += qtd;
        }
    }
}
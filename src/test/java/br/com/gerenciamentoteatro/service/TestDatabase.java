package br.com.gerenciamentoteatro.service;

import br.com.gerenciamentoteatro.repository.JPAUtil;

final class TestDatabase {
    static void reset() {
        try (var em = JPAUtil.getEntityManager()) {
            String url = (String) em.getEntityManagerFactory().getProperties().get("jakarta.persistence.jdbc.url");
            if (url == null || !url.startsWith("jdbc:h2:mem:")) {
                throw new IllegalStateException("Tests require an isolated in-memory database.");
            }
            em.getTransaction().begin();
            for (String entity : new String[]{"Ingresso", "ContratoAluguel", "PropostaAluguel", "Peca",
                    "Artista", "Cliente", "Administrador", "RegraPreco"}) {
                em.createQuery("DELETE FROM " + entity).executeUpdate();
            }
            em.getTransaction().commit();
        }
    }
}

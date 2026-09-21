package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.Artista;
import br.com.gerenciamentoteatro.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class ArtistaDAO extends BaseJPADAO<Artista, Long> {

    public ArtistaDAO() {
        super(Artista.class);
    }

    public Optional<Artista> buscarPorCpf(String cpf) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Artista artista = em.createQuery(
                            "SELECT a FROM Artista a WHERE a.cpf = :cpf", Artista.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
            return Optional.of(artista);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
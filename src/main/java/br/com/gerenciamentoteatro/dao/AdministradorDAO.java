package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.Administrador;
import br.com.gerenciamentoteatro.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class AdministradorDAO extends BaseJPADAO<Administrador, Long> {

    public AdministradorDAO() {
        super(Administrador.class);
    }

    public Optional<Administrador> buscarPorEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Administrador admin = em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.email = :email", Administrador.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(admin);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public boolean existeAdministrador() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(a) FROM Administrador a", Long.class)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
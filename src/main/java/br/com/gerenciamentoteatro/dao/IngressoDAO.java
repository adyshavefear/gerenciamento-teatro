package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.Ingresso;
import br.com.gerenciamentoteatro.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class IngressoDAO {

    public void salvar(Ingresso ingresso) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ingresso);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Ingresso> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Garante que busca todos os ingressos gravados no banco
            return em.createQuery("SELECT i FROM Ingresso i", Ingresso.class).getResultList();
        } finally {
            em.close();
        }
    }
}
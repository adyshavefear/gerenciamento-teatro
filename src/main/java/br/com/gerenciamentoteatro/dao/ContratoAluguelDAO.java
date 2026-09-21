package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.ContratoAluguel;
import br.com.gerenciamentoteatro.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ContratoAluguelDAO {

    public void salvar(ContratoAluguel contrato) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(contrato);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void atualizar(ContratoAluguel contrato) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(contrato);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<ContratoAluguel> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            ContratoAluguel contrato = em.find(ContratoAluguel.class, id);
            return Optional.ofNullable(contrato);
        } finally {
            em.close();
        }
    }

    public List<ContratoAluguel> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Busca todos os contratos cadastrados no banco H2
            return em.createQuery("SELECT c FROM ContratoAluguel c", ContratoAluguel.class).getResultList();
        } finally {
            em.close();
        }
    }
}
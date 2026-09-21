package br.com.gerenciamentoteatro.dao;

import br.com.gerenciamentoteatro.model.Cliente;
import br.com.gerenciamentoteatro.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class ClienteDAO extends BaseJPADAO<Cliente, Long> {

    public ClienteDAO() {
        super(Cliente.class);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Cliente cliente = em.createQuery(
                            "SELECT c FROM Cliente c WHERE c.cpf = :cpf", Cliente.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
            return Optional.of(cliente);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
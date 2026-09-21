package br.com.gerenciamentoteatro.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory("TeatroPU");

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }

    public static void fecharFactory() {
        if (FACTORY != null && FACTORY.isOpen()) {
            FACTORY.close();
        }
    }
}
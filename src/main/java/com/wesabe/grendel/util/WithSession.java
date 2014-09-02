package com.wesabe.grendel.util;

import org.hibernate.Session;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManager;

/**
 * 4/5/14 Created by Jonathan Garay
 */
public class WithSession<T> {
    private final JpaTransactionManager transactionManager;

    public WithSession(JpaTransactionManager manager) {
        this.transactionManager = manager;
    }

    public T transaction(Seasonable<T> statement) {
        EntityManager entityManager = transactionManager
                .getEntityManagerFactory()
                .createEntityManager();

        Session session = entityManager.unwrap(Session.class);
        session.beginTransaction();
        T result = statement.doStatement(entityManager);
        session.getTransaction().commit();
        return result;
    }

    public interface Seasonable<T> {
        public T doStatement(EntityManager entityManager);
    }
}
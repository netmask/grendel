package com.wesabe.grendel.entities.dao;

import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.util.WithSession;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
public class UserRepository {

    @Inject
    JpaTransactionManager transactionManager;

    /**
     * Returns {@code true} if a user already exists with the given id.
     */
    public boolean contains(String id) {
        return new WithSession<Boolean>(transactionManager).transaction(entityManager ->
                entityManager.createNamedQuery("com.wesabe.grendel.entities.User.Exists", User.class)
                        .setParameter("id", id)
                        .getSingleResult() != null);

    }

    /**
     * Returns the {@link User} with the given id, or {@code null} if the user
     * does not exist.
     */
    public User findById(String id) {
        return new WithSession<User>(transactionManager).transaction(entityManager ->
                entityManager.find(User.class, id));
    }

    /**
     * Returns a list of all {@link User}s.
     */
    public List<User> findAll() {
        return new WithSession<List<User>>(transactionManager).transaction(entityManager ->
                entityManager.createNamedQuery("com.wesabe.grendel.entities.User.All", User.class)
                        .getResultList());
    }

    /**
     * Writes the {@link User} to the database.
     */
    public User saveOrUpdate(User user) {
        return new WithSession<User>(transactionManager).transaction(entityManager -> {
            entityManager.persist(user);
            return user;
        });
    }

    /**
     * Deletes the {@link User} from the database.
     */
    public void delete(User user) {
        new WithSession<User>(transactionManager).transaction(entityManager -> {
            entityManager.remove(user);
            return user;
        });
    }
}

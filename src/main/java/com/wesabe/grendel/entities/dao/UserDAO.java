package com.wesabe.grendel.entities.dao;

import com.wesabe.grendel.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class UserDAO {
    @PersistenceContext(unitName = "grendelPU")
    private EntityManager entityManager;

    /**
     * Returns {@code true} if a user already exists with the given id.
     */
    public boolean contains(String id) {
        return entityManager.createNamedQuery("com.wesabe.grendel.entities.User.Exists", User.class)
                .setParameter("id", id)
                .getSingleResult() != null;
    }

    /**
     * Returns the {@link User} with the given id, or {@code null} if the user
     * does not exist.
     */
    public User findById(String id) {
        return entityManager.find(User.class, id);
    }

    /**
     * Returns a list of all {@link User}s.
     */
    public List<User> findAll() {
        return entityManager.createNamedQuery("com.wesabe.grendel.entities.User.All", User.class)
                .getResultList();
    }

    /**
     * Writes the {@link User} to the database.
     *
     */
    public User saveOrUpdate(User user) {
        entityManager.persist(user);
        return user;
    }

    /**
     * Deletes the {@link User} from the database.
     */
    public void delete(User user) {
        entityManager.remove(user);
    }
}

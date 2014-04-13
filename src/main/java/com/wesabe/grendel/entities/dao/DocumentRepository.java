package com.wesabe.grendel.entities.dao;

import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.util.WithSession;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class DocumentRepository {

    @Inject
    JpaTransactionManager transactionManager;

    /**
     * Returns a new {@link Document} with the provided owner, name, and
     * content-type.
     */
    public Document newDocument(User owner, String name, MediaType contentType) {
        return new Document(owner, name, contentType);
    }

    /**
     * Finds a {@link Document} instance with a given owner and name. Returns
     * {@code null} if the {@link Document} does not exist.
     */
    public Document findByOwnerAndName(User owner, String name) {
        return new WithSession<Document>(transactionManager).transaction(entityManager1 ->
                entityManager1.createNamedQuery("com.wesabe.grendel.entities.Document.ByOwnerAndName", Document.class)
                .setParameter("owner", owner)
                .setParameter("name", name)
                .getSingleResult());

    }

    /**
     * Writes the {@link Document} to the database.
     *
     */
    public Document saveOrUpdate(Document doc) {
        return new WithSession<Document>(transactionManager).transaction(entityManager -> {
            entityManager.persist(doc);
            return doc;
        });
    }

    /**
     * Deletes the {@link Document} from the database.
     */
    public void delete(Document doc) {
         new WithSession<Boolean>(transactionManager).transaction(entityManager1 -> {
             entityManager1.remove(entityManager1.merge(doc));
             return true;
         });

    }

    public List<Document> getUserDocuments(User user){
        return new WithSession<List<Document>>(transactionManager).transaction(entityManager1 ->
                 entityManager1.createNamedQuery("com.wesabe.grendel.entities.Document.ByOwner", Document.class)
                 .setParameter("owner", user)
                 .getResultList());
    }
}

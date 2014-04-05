package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.CryptographicException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;

/**
 * A class which exposes a linked {@link Document} as a resource.
 *
 * @author coda
 */
@Path("/users/{user_id}/linked-documents/{owner_id}/{name}")
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class LinkedDocumentResource {
    private static final CacheControl CACHE_SETTINGS;

    static {
        CACHE_SETTINGS = new CacheControl();
        CACHE_SETTINGS.setNoCache(true);
        CACHE_SETTINGS.setNoStore(true);
        CACHE_SETTINGS.setPrivate(true);
    }

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Inject
    public LinkedDocumentResource(UserRepository userRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    /**
     * Responds to a {@link GET} request by decrypting the {@link Document} body
     * and returning it.
     * <p>
     * <strong>N.B.:</strong> Requires Basic authentication.
     */
    @GET
    public Response show(@Context Credentials credentials,
                         @PathParam("user_id") String userId, @PathParam("owner_id") String ownerId,
                         @PathParam("name") String name) {

        final Session session = credentials.buildSession(userRepository, userId);
        final User owner = findUser(ownerId);
        final Document doc = findDocument(owner, name);

        checkLinkage(doc, session.getUser());

        try {
            final byte[] body = doc.decryptBody(session.getKeySet());
            return ok()
                    .entity(body)
                    .type(doc.getContentType())
                    .cacheControl(CACHE_SETTINGS)
                    .lastModified(doc.getModifiedAt().toDate())
                    .build();
        } catch (CryptographicException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Responds to a {@link DELETE} request by deleting the {@link User}'s
     * access to this {@link Document}. This does <strong>not</strong> delete
     * the document itself, nor does it re-encrypt the document.
     * <p>
     * <strong>N.B.:</strong> Requires Basic authentication.
     */
    @DELETE
    public Response delete(@Context Credentials credentials,
                           @PathParam("user_id") String userId, @PathParam("owner_id") String ownerId,
                           @PathParam("name") String name) {

        final Session session = credentials.buildSession(userRepository, userId);
        final User owner = findUser(ownerId);
        final Document doc = findDocument(owner, name);

        checkLinkage(doc, session.getUser());

        doc.unlinkUser(session.getUser());
        documentRepository.saveOrUpdate(doc);

        return noContent().build();
    }

    private Document findDocument(User owner, String name) {
        final Document doc = documentRepository.findByOwnerAndName(owner, name);
        if (doc == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return doc;
    }

    private void checkLinkage(Document doc, User user) {
        if (!doc.isLinked(user)) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
    }

    private User findUser(String id) {
        final User user = userRepository.findById(id);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        return user;
    }
}

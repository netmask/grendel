package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.security.SecureRandom;

import static javax.ws.rs.core.Response.noContent;

@Path("/users/{user_id}/documents/{name}/links/{reader_id}")
public class LinkResource {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final Provider<SecureRandom> randomProvider;

    @Inject
    public LinkResource(UserRepository userRepository, DocumentRepository documentRepository, Provider<SecureRandom> randomProvider) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.randomProvider = randomProvider;
    }

    @PUT
    public Response createLink(@PathParam("user_id") String userId, @PathParam("name") String name,
                               @PathParam("reader_id") String readerId) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session)authenticationToken.getPrincipal();
        final User reader = findUser(readerId);
        final Document doc = findDocument(session.getUser(), name);

        doc.linkUser(reader);
        reEncrypt(doc, session.getKeySet());

        documentRepository.saveOrUpdate(doc);

        return noContent().build();
    }

    @DELETE
    public Response deleteLink(@PathParam("user_id") String userId,
            @PathParam("name") String name,
            @PathParam("reader_id") String readerId) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session)authenticationToken.getPrincipal();

        final User reader = findUser(readerId);
        final Document doc = findDocument(session.getUser(), name);

        doc.unlinkUser(reader);
        reEncrypt(doc, session.getKeySet());

        documentRepository.saveOrUpdate(doc);

        return noContent().build();
    }

    private void reEncrypt(Document doc, UnlockedKeySet ownerKeySet) {
        try {
            final byte[] body = doc.decryptBody(ownerKeySet);
            doc.encryptAndSetBody(
                    ownerKeySet,
                    randomProvider.get(),
                    body
            );
        } catch (CryptographicException e) {
            throw new RuntimeException(e);
        }
    }

    private Document findDocument(User owner, String name) {
        final Document doc = documentRepository.findByOwnerAndName(owner, name);
        if (doc == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return doc;
    }

    private User findUser(String id) {
        final User reader = userRepository.findById(id);
        if (reader == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return reader;
    }
}

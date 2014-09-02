package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.CryptographicException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.security.SecureRandom;

import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;

/**
 * A class which exposes {@link Document} as a resource.
 *
 * @author coda
 */
@Path("/users/{user_id}/documents/{name}")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class DocumentResource {
    private static final CacheControl CACHE_SETTINGS;
    private final Provider<SecureRandom> randomProvider;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Inject
    private JpaTransactionManager transactionManager;

    static {
        CACHE_SETTINGS = new CacheControl();
        CACHE_SETTINGS.setNoCache(true);
        CACHE_SETTINGS.setNoStore(true);
        CACHE_SETTINGS.setPrivate(true);
    }

    @Inject
    public DocumentResource(Provider<SecureRandom> randomProvider,
                            UserRepository userRepository,
                            DocumentRepository documentRepository) {

        this.randomProvider = randomProvider;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    /**
     * Responds to a {@link GET} request by decrypting the {@link Document} body
     * and returning it.
     * <p>
     * <strong>N.B.:</strong> Requires Basic authentication.
     *
     * @throws CryptographicException
     */
    @GET
    @Transactional
    public Response show(
            @Context Request request,
            @PathParam("user_id") String userId,
            @PathParam("name") String name) throws CryptographicException {


        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session) authenticationToken.getPrincipal();


        final Document doc = documentRepository.findByOwnerAndName(session.getUser(), name);


        if (doc == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        checkPreconditions(request, doc);

        final byte[] body = doc.decryptBody(session.getKeySet());


        return ok()
                .entity(body)
                .type(doc.getContentType())
                .cacheControl(CACHE_SETTINGS)
                .lastModified(doc.getModifiedAt().toDate())
                .tag(doc.getETag())
                .build();
    }

    /**
     * Responds to a {@link DELETE} request by deleting the {@link Document}.
     * <p>
     * <strong>N.B.:</strong> Requires Basic authentication.
     */
    @DELETE
    @PreAuthorize("hasAuthority('DOCUMENT_ACTIONS')")
    @Transactional
    public Response delete(@Context Request request,
                           @PathParam("user_id") String userId, @PathParam("name") String name) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session) authenticationToken.getPrincipal();

        final Document doc = documentRepository.findByOwnerAndName(session.getUser(), name);
        if (doc == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        checkPreconditions(request, doc);

        documentRepository.delete(doc);
        return noContent().build();
    }

    /**
     * Responds to a {@link PUT} request by replacing the specified
     * {@link Document} with the request entity.
     * <p>
     * <strong>N.B.:</strong> Requires Basic authentication.
     *
     * @throws CryptographicException
     */
    @PUT
    @PreAuthorize("hasAuthority('DOCUMENT_ACTIONS')")
    @Transactional
    public Response store(@Context Request request, @Context HttpHeaders headers,
                          @PathParam("user_id") String userId,
                          @PathParam("name") String name, byte[] body) throws CryptographicException {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session) authenticationToken.getPrincipal();

        Document doc;

        try {
            doc = documentRepository.findByOwnerAndName(session.getUser(), name);
            checkPreconditions(request, doc);
        } catch (NoResultException noResultException) {
            doc = documentRepository.newDocument(session.getUser(), name, headers.getMediaType());
        }

        doc.setModifiedAt(new DateTime(DateTimeZone.UTC));

        doc.encryptAndSetBody(
                session.getKeySet(),
                randomProvider.get(),
                body
        );

        documentRepository.saveOrUpdate(doc);

        return noContent()
                .tag(doc.getETag())
                .build();
    }

    /**
     * If the request has {@code If-Modified-Since} or {@code If-None-Match}
     * headers, and the resource has a matching {@link Document#getModifiedAt()}
     * or {@link Document#getETag()}, returns a {@code 304 Unmodified},
     * indicating the client has the most recent version of the resource.
     * <p>
     * If the request has a {@code If-Unmodified-Since} or {@code If-Match}
     * headers, and the resource has a more recent
     * {@link Document#getModifiedAt()} or {@link Document#getETag()}, returns
     * a {@code 412 Precondition Failed}, indicating the client should re-read
     * the resource before overwriting it.
     */
    private void checkPreconditions(Request request, Document document) {
        final EntityTag eTag = new EntityTag(document.getETag());
        final ResponseBuilder response = request.evaluatePreconditions(document.getModifiedAt().toDate(), eTag);
        if (response != null) {
            throw new WebApplicationException(response.build());
        }
    }

}

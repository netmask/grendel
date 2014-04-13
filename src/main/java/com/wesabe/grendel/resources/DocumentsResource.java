package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.decorators.DocumentListRepresentation;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * A class which exposes a list of {@link Document}s as a resource.
 *
 * @author coda
 */
@Path("/users/{id}/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentsResource {

    private final DocumentRepository documentRepository;

    @Inject
    public DocumentsResource(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GET
    public DocumentListRepresentation listDocuments(
            @Context UriInfo uriInfo) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session) authenticationToken.getPrincipal();

        return new DocumentListRepresentation(uriInfo,
                documentRepository.getUserDocuments(session.getUser()));
    }
}

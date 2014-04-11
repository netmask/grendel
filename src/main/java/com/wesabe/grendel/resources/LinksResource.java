package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.decorators.LinkListRepresentation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * A class which exposes a list of {@link User}s linked to a particular
 * {@link Document} as a resource.
 *
 * @author coda
 */
@Path("/users/{user_id}/documents/{name}/links/")
@Produces(MediaType.APPLICATION_JSON)
public class LinksResource {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Inject
    public LinksResource(UserRepository userRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    @GET
    public LinkListRepresentation listLinks(@Context UriInfo uriInfo,
                                            @PathParam("user_id") String userId,
                                            @PathParam("name") String documentName) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session)authenticationToken.getPrincipal();
        final Document doc = documentRepository.findByOwnerAndName(session.getUser(), documentName);
        if (doc == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        return new LinkListRepresentation(uriInfo, doc);
    }
}

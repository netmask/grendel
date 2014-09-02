package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.decorators.LinkedDocumentListRepresentation;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * A class which exposes a list of linked {@link Document}s as a resource.
 *
 * @author coda
 */
@Path("/users/{id}/linked-documents")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class LinkedDocumentsResource {
    private final UserRepository userRepository;

    @Inject
    public LinkedDocumentsResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GET
    public LinkedDocumentListRepresentation listDocuments(@Context UriInfo uriInfo,
                                                          @PathParam("id") String id) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session) authenticationToken.getPrincipal();

        User user = session.getUser();

        return new LinkedDocumentListRepresentation(uriInfo, user, user.getLinkedDocuments());
    }
}

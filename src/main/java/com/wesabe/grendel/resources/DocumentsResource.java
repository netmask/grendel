package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.decorators.DocumentListRepresentation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    final UserRepository userRepository;

    @Inject
    public DocumentsResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GET
    public DocumentListRepresentation listDocuments(
            @Context UriInfo uriInfo,
            @Context Credentials credentials,
            @PathParam("id") String id) {

        final Session session = credentials.buildSession(userRepository, id);
        return new DocumentListRepresentation(uriInfo, session.getUser().getDocuments());
    }
}

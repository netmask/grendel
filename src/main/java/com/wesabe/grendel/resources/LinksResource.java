package com.wesabe.grendel.resources;

import com.google.inject.Inject;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentDAO;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.representations.LinkListRepresentation;

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
	private final UserDAO userDAO;
	private final DocumentDAO documentDAO;
	
	@Inject
	public LinksResource(UserDAO userDAO, DocumentDAO documentDAO) {
		this.userDAO = userDAO;
		this.documentDAO = documentDAO;
	}
	
	@GET
	public LinkListRepresentation listLinks(@Context UriInfo uriInfo,
		@Context Credentials credentials, @PathParam("user_id") String userId,
		@PathParam("name") String documentName) {
		
		final Session session = credentials.buildSession(userDAO, userId);
		
		final Document doc = documentDAO.findByOwnerAndName(session.getUser(), documentName);
		if (doc == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return new LinkListRepresentation(uriInfo, doc);
	}
}

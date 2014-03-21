package com.wesabe.grendel.resources;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.representations.UpdateUserRepresentation;
import com.wesabe.grendel.representations.UserInfoRepresentation;
import com.wideplay.warp.persist.Transactional;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.security.SecureRandom;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;

/**
 * A resource for managing individual {@link User}s.
 * 
 * @author coda
 */
@Path("/users/{id}")
public class UserResource {
	private final UserDAO userDAO;
	private final Provider<SecureRandom> randomProvider;

	@Inject
	public UserResource(UserDAO userDAO, Provider<SecureRandom> randomProvider) {
		this.userDAO = userDAO;
		this.randomProvider = randomProvider;
	}
	
	/**
	 * Responds to a {@link GET} request with information about the specified
	 * user.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response show(@Context Request request, @Context UriInfo uriInfo,
		@PathParam("id") String id) {
		
		final User user = userDAO.findById(id);
		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		checkPreconditions(request, user);
		
		return ok(new UserInfoRepresentation(uriInfo, user))
						.tag(user.getEtag())
						.lastModified(user.getModifiedAt().toDate())
						.build();
	}
	
	/**
	 * Responds to a {@link PUT} request by changing the user's password.
	 * <p>
	 * <strong>N.B.:</strong> Requires Basic authentication.
	 * @throws CryptographicException
	 */
	@PUT
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context Request request,@Context Credentials credentials,
		@PathParam("id") String id, UpdateUserRepresentation entity) throws CryptographicException {
		
		entity.validate();
		
		final Session session = credentials.buildSession(userDAO, id);
		
		checkPreconditions(request, session.getUser());
		
		final User user = session.getUser();
		final UnlockedKeySet keySet = session.getKeySet();
		
		user.setKeySet(
			keySet.relock(
				credentials.getPassword().toCharArray(),
				entity.getPassword(),
				randomProvider.get()
			)
		);
		
		user.setModifiedAt(new DateTime());
		userDAO.saveOrUpdate(user);
		
		return noContent().build();
	}
	
	/**
	 * Responds to a {@link DELETE} request by deleting the user <strong>and
	 * all their {@link Document}s.</strong>
	 */
	@DELETE
	@Transactional
	public Response delete(@Context Request request,@Context UriInfo uriInfo, @PathParam("id") String id) {
		final User user = userDAO.findById(id);
		
		checkPreconditions(request, user);
		
		userDAO.delete(user);
		return noContent().build();
	}
	
	/**
	 * If the request has {@code If-Modified-Since} or {@code If-None-Match}
	 * headers, and the resource has a matching {@link User#getModifiedAt()}
	 * or {@link User#getEtag()}, returns a {@code 304 Unmodified},
	 * indicating the client has the most recent version of the resource.
	 * 
	 * If the request has a {@code If-Unmodified-Since} or {@code If-Match}
	 * headers, and the resource has a more recent
	 * {@link User#getModifiedAt()} or {@link User#getEtag()}, returns
	 * a {@code 412 Precondition Failed}, indicating the client should re-read
	 * the resource before overwriting it.
	 */
	private void checkPreconditions(Request request, User user) {
		final EntityTag eTag = new EntityTag(user.getEtag());
		final ResponseBuilder response = request.evaluatePreconditions(user.getModifiedAt().toDate(), eTag);
		if (response != null) {
			throw new WebApplicationException(response.build());
		}
	}
}

package com.wesabe.grendel.resources;

import com.google.inject.Inject;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.KeySetGenerator;
import com.wesabe.grendel.representations.CreateUserRepresentation;
import com.wesabe.grendel.representations.UserListRepresentation;
import com.wesabe.grendel.representations.ValidationException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.Response.created;

/**
 * A resource for managing the collection of registered {@link User}s.
 * 
 * @author coda
 */
@Path("/users/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {
	private final KeySetGenerator generator;
	private final UserDAO userDAO;
	
	@Inject
	public UsersResource(KeySetGenerator generator, UserDAO userDAO) {
		this.generator = generator;
		this.userDAO = userDAO;
	}
	
	/**
	 * Responds to a {@link GET} request with a list of all the registered
	 * users.
	 * 
	 * @see UserListRepresentation
	 */
	@GET
	public UserListRepresentation list(@Context UriInfo uriInfo) {
		final List<User> users = userDAO.findAll();
		return new UserListRepresentation(uriInfo, users);
	}
	
	/**
	 * Responds to a {@link POST} request by generating a new {@link KeySet},
	 * creating a new {@link User}, and returning the user's info URI.
	 * 
	 * @throws CryptographicException
	 *             if there is an error generating the {@link KeySet}
	 * @see UserResource
	 */
	@POST
	public Response create(@Context UriInfo uriInfo, CreateUserRepresentation request) throws CryptographicException {
		request.validate();
		
		if (userDAO.contains(request.getId())) {
			final ValidationException e = new ValidationException();
			e.addReason("username is already taken");
			throw e;
		}

		final KeySet keySet = generator.generate(request.getId(), request.getPassword());
		final User user = userDAO.saveOrUpdate(new User(keySet));
		
		request.sanitize();
		
		return created(
			uriInfo.getBaseUriBuilder()
						.path(UserResource.class)
						.build(user)
		).build();
	}
}

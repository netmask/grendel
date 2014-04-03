package com.wesabe.grendel.resources;

import com.wesabe.grendel.decorators.UserCreatedDecorator;
import com.wesabe.grendel.decorators.UserListRepresentation;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.KeySetGenerator;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
     * @throws CryptographicException if there is an error generating the {@link KeySet}
     * @see UserResource
     */
    @POST
    public Response create(@Context UriInfo uriInfo, UserCreatedDecorator request) throws CryptographicException {

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

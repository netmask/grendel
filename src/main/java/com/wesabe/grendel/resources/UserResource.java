package com.wesabe.grendel.resources;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.decorators.UpdateUserRepresentation;
import com.wesabe.grendel.decorators.UserInfoRepresentation;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import org.joda.time.DateTime;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.inject.Provider;
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

    @Inject
    private UserRepository userRepository;

    @Inject
    private Provider<SecureRandom> randomProvider;

    /**
     * Responds to a {@link GET} request with information about the specified
     * user.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response show(@Context Request request, @Context UriInfo uriInfo,
                         @PathParam("id") String id) {

        final User user = userRepository.findById(id);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        checkPreconditions(request, user);

        return ok(new UserInfoRepresentation(uriInfo, user))
                .tag(user.getETag())
                .lastModified(user.getModifiedAt().toDate())
                .build();
    }

    /**
     * Responds to a {@link PUT} request by changing the user's password.
     * <p>
     * <strong>N.B.:</strong> Requires Basic authentication.
     *
     * @throws CryptographicException
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@Context Request request,
                           @PathParam("id") String id,
                           UpdateUserRepresentation entity) throws CryptographicException {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        Session session = (Session)authenticationToken.getPrincipal();

        checkPreconditions(request, session.getUser());


        final User user = session.getUser();
        final UnlockedKeySet keySet = session.getKeySet();

        user.setKeySet(
                keySet.relock(
                        (char[]) authenticationToken.getCredentials(),
                        entity.getPassword(),
                        randomProvider.get()
                )
        );

        user.setModifiedAt(new DateTime());
        userRepository.saveOrUpdate(user);

        return noContent().build();
    }

    /**
     * Responds to a {@link DELETE} request by deleting the user <strong>and
     * all their {@link Document}s.</strong>
     */
    @DELETE
    public Response delete(@Context Request request,
                           @Context UriInfo uriInfo,
                           @PathParam("id") String id) {
        final User user = userRepository.findById(id);

        checkPreconditions(request, user);

        userRepository.delete(user);
        return noContent().build();
    }

    /**
     * If the request has {@code If-Modified-Since} or {@code If-None-Match}
     * headers, and the resource has a matching {@link User#getModifiedAt()}
     * or {@link User#getETag()}, returns a {@code 304 Unmodified},
     * indicating the client has the most recent version of the resource.
     * <p>
     * If the request has a {@code If-Unmodified-Since} or {@code If-Match}
     * headers, and the resource has a more recent
     * {@link User#getModifiedAt()} or {@link User#getETag()}, returns
     * a {@code 412 Precondition Failed}, indicating the client should re-read
     * the resource before overwriting it.
     */
    private void checkPreconditions(Request request, User user) {
        final EntityTag eTag = new EntityTag(user.getETag());
        final ResponseBuilder response = request.evaluatePreconditions(user.getModifiedAt().toDate(), eTag);
        if (response != null) {
            throw new WebApplicationException(response.build());
        }
    }
}

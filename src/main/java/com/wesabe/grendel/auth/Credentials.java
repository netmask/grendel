package com.wesabe.grendel.auth;

import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.status;

/**
 * A set of Basic authentication credentials.
 *
 * @author coda
 * @see Session
 */
public class Credentials {
    /**
     * An authentication challenge {@link Response}. Use this when a client's
     * provided credentials are invalid.
     */
    public static final Response CHALLENGE =
            status(Status.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Grendel\"")
                    .build();

    private final String username;
    private final String password;

    /**
     * Creates a new set of credentials.
     *
     * @param username the client's provided username
     * @param password the client's provided password
     */
    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the client's provided username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the client's provided password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Given a {@link UserRepository}, finds the associated {@link User} and returns a
     * {@link Session}.
     *
     * @param userRepository a {@link UserRepository}
     * @throws WebApplicationException if the user can't be found, or if the user's password is
     *                                 incorrect
     */
    public Session buildSession(UserRepository userRepository) throws WebApplicationException {
        final User user = userRepository.findById(username);
        if (user != null) {
            try {
                final UnlockedKeySet keySet = user.getKeySet().unlock(password.toCharArray());
                return new Session(user, keySet);
            } catch (CryptographicException e) {
                throw new WebApplicationException(CHALLENGE);
            }
        }

        throw new WebApplicationException(CHALLENGE);
    }

    /**
     * Given a {@link UserRepository} and an allowed {@link User} id, finds the
     * associated {@link User} and returns a {@link Session}.
     *
     * @param userRepository   a {@link UserRepository}
     * @param allowedId the id of the only {@link User} which should be allowed access
     *                  to session context
     * @throws WebApplicationException if the user can't be found, or if the user's password is
     *                                 incorrect
     */
    public Session buildSession(UserRepository userRepository, String allowedId) {
        final Session session = buildSession(userRepository);
        if (session.getUser().getId().equals(allowedId)) {
            return session;
        }

        throw new WebApplicationException(Status.FORBIDDEN);
    }
}

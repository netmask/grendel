package com.wesabe.grendel.decorators;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wesabe.grendel.entities.User;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.joda.time.format.ISODateTimeFormat.basicDateTimeNoMillis;

/**
 * A representation of a response containing information about a user.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "id":"codahale",
 *   "created-at":"20091228T234341Z",
 *   "modified-at":"20091228T234341Z",
 *   "keys":"[2048-RSA/38CDB097, 2048-RSA/204DB69D]"
 * }
 * </pre>
 *
 * @author coda
 */
public class UserInfoRepresentation {
    private static final DateTimeFormatter ISO_DATETIME = basicDateTimeNoMillis();
    private final User user;
    private final UriInfo uriInfo;

    public UserInfoRepresentation(UriInfo uriInfo, User user) {
        this.user = user;
        this.uriInfo = uriInfo;
    }

    @JsonGetter("id")
    public String getId() {
        return user.getId();
    }

    @JsonGetter("created-at")
    public String getCreatedAt() {
        return ISO_DATETIME.print(user.getCreatedAt());
    }

    @JsonGetter("modified-at")
    public String getModifiedAt() {
        return ISO_DATETIME.print(user.getModifiedAt());
    }

    @JsonGetter("keys")
    public String getKeys() {
        return user.getKeySet().toString();
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public static class DocumentLink {
        private final String name;
        private final URI uri;

        public DocumentLink(String name, URI uri) {
            this.name = name;
            this.uri = uri;
        }

        @JsonGetter("name")
        public String getName() {
            return name;
        }

        @JsonGetter("uri")
        public String getUri() {
            return uri.toASCIIString();
        }

    }
}

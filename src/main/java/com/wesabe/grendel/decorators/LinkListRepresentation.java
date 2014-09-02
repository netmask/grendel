package com.wesabe.grendel.decorators;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wesabe.grendel.decorators.UserListRepresentation.UserListItem;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.resources.LinkResource;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * A list of a {@link Document}'s links.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "links":[
 *     {
 *       "user":{
 *         "id":"precipice",
 *         "uri":"http://example.com/users/precipice"
 *       },
 *       "uri":"http://example.com/users/codahale/documents/document1.txt/links/precipice"
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author coda
 */
public class LinkListRepresentation {
    private final UriInfo uriInfo;
    private final Document document;

    public LinkListRepresentation(UriInfo uriInfo, Document document) {
        this.uriInfo = uriInfo;
        this.document = document;
    }

    @JsonIgnore
    public Document getDocument() {
        return document;
    }

    @JsonIgnore
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @JsonGetter("links")
    public List<LinkListItem> getLinks() {
        final List<LinkListItem> links = newArrayList();
        links.addAll(document.getLinkedUsers()
                .stream()
                .map(user -> new LinkListItem(uriInfo, document, user))
                .collect(Collectors.toList()));

        return links;
    }

    public static class LinkListItem {
        private final UriInfo uriInfo;
        private final Document document;
        private final User user;

        public LinkListItem(UriInfo uriInfo, Document document, User user) {
            this.uriInfo = uriInfo;
            this.document = document;
            this.user = user;
        }

        @JsonGetter("user")
        public UserListItem getUser() {
            return new UserListItem(uriInfo, user);
        }

        @JsonGetter("uri")
        public String getUri() {
            return uriInfo.getBaseUriBuilder()
                    .path(LinkResource.class)
                    .build(document.getOwner(), document, user)
                    .toASCIIString();
        }

    }
}

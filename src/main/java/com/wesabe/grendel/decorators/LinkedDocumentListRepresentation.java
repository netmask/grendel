package com.wesabe.grendel.decorators;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wesabe.grendel.decorators.UserListRepresentation.UserListItem;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.resources.LinkedDocumentResource;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class LinkedDocumentListRepresentation {
    private final UriInfo uriInfo;
    private final User user;
    private final Set<Document> documentSet;

    public LinkedDocumentListRepresentation(UriInfo uriInfo, User user, Set<Document> documentSet) {
        this.uriInfo = uriInfo;
        this.user = user;
        this.documentSet = documentSet;
    }


    @JsonGetter("linked-documents")
    public List<DocumentListItem> listDocuments() {
        final List<DocumentListItem> items = newArrayList();

        items.addAll(documentSet.stream()
                .map(doc -> new DocumentListItem(uriInfo, user, doc))
                .collect(Collectors.toList()));
        return items;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public static class DocumentListItem {
        private final UriInfo uriInfo;
        private final User user;
        private final Document document;

        public DocumentListItem(UriInfo uriInfo, User user, Document document) {
            this.uriInfo = uriInfo;
            this.user = user;
            this.document = document;
        }

        @JsonGetter("name")
        public String getName() {
            return document.getName();
        }

        @JsonGetter("owner")
        public UserListItem getOwner() {
            return new UserListItem(uriInfo, document.getOwner());
        }

        @JsonGetter("uri")
        public String getURI() {
            return uriInfo.getBaseUriBuilder()
                    .path(LinkedDocumentResource.class)
                    .build(user, document.getOwner(), document)
                    .toASCIIString();
        }
    }
}

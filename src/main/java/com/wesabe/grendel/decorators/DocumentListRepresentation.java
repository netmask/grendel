package com.wesabe.grendel.decorators;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.resources.DocumentResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayListWithCapacity;

public class DocumentListRepresentation {
    private UriInfo uriInfo;
    private List<Document> documents;

    public DocumentListRepresentation(UriInfo uriInfo, List<Document> documents) {
        this.uriInfo = uriInfo;
        this.documents = documents;
    }

    @JsonGetter("documents")
    public List<DocumentListItem> listDocuments() {

        final List<DocumentListItem> items = newArrayListWithCapacity(documents.size());

        items.addAll(documents.stream()
                .map(doc -> new DocumentListItem(uriInfo, doc))
                .collect(Collectors.toList()));

        return items;
    }

    @JsonIgnore
    public List<Document> getDocuments() {
        return documents;
    }

    @JsonIgnore
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public static class DocumentListItem {
        private final UriInfo uriInfo;
        private final Document document;

        public DocumentListItem(UriInfo uriInfo, Document document) {
            this.uriInfo = uriInfo;
            this.document = document;
        }

        @JsonGetter("name")
        public String getName() {
            return document.getName();
        }

        @JsonGetter("uri")
        public String getURI() {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            Session session = (Session) authenticationToken.getPrincipal();

            return uriInfo.getBaseUriBuilder()
                    .path(DocumentResource.class)
                    .build(session.getUser(), document)
                    .toASCIIString();
        }
    }
}

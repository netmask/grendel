package com.wesabe.grendel.resources.tests;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.decorators.LinkListRepresentation;
import com.wesabe.grendel.resources.LinksResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class LinksResourceTest {
	public static class Listing_Links {
		protected Document document;
		protected Credentials credentials;
		protected User user;
		protected Session session;
		protected UserRepository userRepository;
		protected DocumentRepository documentRepository;
		protected UriInfo uriInfo;
		protected LinksResource resource;
		
		@Before
		public void setup() throws Exception {
			this.document = mock(Document.class);
			
			this.user = mock(User.class);
			
			this.session = mock(Session.class);
			when(session.getUser()).thenReturn(user);
			
			this.userRepository = mock(UserRepository.class);
			
			this.documentRepository = mock(DocumentRepository.class);
			when(documentRepository.findByOwnerAndName(user, "document1.txt")).thenReturn(document);
			
			this.resource = new LinksResource(userRepository, documentRepository);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userRepository, "bob")).thenReturn(session);
			
			this.uriInfo = mock(UriInfo.class);
		}
		
		@Test
		public void itThrowsA404IfTheDocumentDoesNotExist() throws Exception {
			when(documentRepository.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			try {
				resource.listLinks(uriInfo, credentials, "bob", "document1.txt");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsADocumentList() throws Exception {
			final LinkListRepresentation docs = resource.listLinks(uriInfo, credentials, "bob", "document1.txt");
			
			assertThat(docs.getDocument()).isEqualTo(document);
			assertThat(docs.getUriInfo()).isEqualTo(uriInfo);
		}
	}
}

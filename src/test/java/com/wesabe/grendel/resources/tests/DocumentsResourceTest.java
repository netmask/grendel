package com.wesabe.grendel.resources.tests;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.collect.ImmutableSet.of;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserDAO;
import com.wesabe.grendel.decorators.DocumentListRepresentation;
import com.wesabe.grendel.resources.DocumentsResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.ws.rs.core.UriInfo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class DocumentsResourceTest {
	public static class Listing_A_Users_Documents {
		protected Document document;
		protected Credentials credentials;
		protected User user;
		protected Session session;
		protected UserDAO userDAO;
		protected UriInfo uriInfo;
		protected DocumentsResource resource;
		
		@Before
		public void setup() throws Exception {
			this.document = mock(Document.class);
			
			this.user = mock(User.class);
			when(user.getDocuments()).thenReturn(of(document));
			
			this.session = mock(Session.class);
			when(session.getUser()).thenReturn(user);
			
			this.userDAO = mock(UserDAO.class);
			
			this.resource = new DocumentsResource(userDAO);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userDAO, "bob")).thenReturn(session);
			
			this.uriInfo = mock(UriInfo.class);
		}
		
		@Test
		public void itReturnsADocumentListIfValid() throws Exception {
			final DocumentListRepresentation docs = resource.listDocuments(uriInfo, credentials, "bob");
			
			assertThat(docs.getDocuments()).isEqualTo(of(document));
			assertThat(docs.getUriInfo()).isEqualTo(uriInfo);
		}
	}
}

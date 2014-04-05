package com.wesabe.grendel.resources.tests;

import static com.google.common.collect.ImmutableSet.of;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.decorators.LinkedDocumentListRepresentation;
import com.wesabe.grendel.resources.LinkedDocumentsResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.ws.rs.core.UriInfo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class LinkedDocumentsResourceTest {
	public static class Listing_A_Users_Linked_Documents {
		protected Document document;
		protected Credentials credentials;
		protected User user;
		protected Session session;
		protected UserRepository userRepository;
		protected UriInfo uriInfo;
		protected LinkedDocumentsResource resource;
		
		@Before
		public void setup() throws Exception {
			this.document = mock(Document.class);
			
			this.user = mock(User.class);
			when(user.getLinkedDocuments()).thenReturn(of(document));
			
			this.session = mock(Session.class);
			when(session.getUser()).thenReturn(user);
			
			this.userRepository = mock(UserRepository.class);
			
			this.resource = new LinkedDocumentsResource(userRepository);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userRepository, "bob")).thenReturn(session);
			
			this.uriInfo = mock(UriInfo.class);
		}
		
		@Test
		public void itReturnsADocumentList() throws Exception {
			final LinkedDocumentListRepresentation docs = resource.listDocuments(uriInfo, credentials, "bob");
			
			assertThat(docs.getUser()).isEqualTo(user);
			assertThat(docs.getUriInfo()).isEqualTo(uriInfo);
		}
	}
}

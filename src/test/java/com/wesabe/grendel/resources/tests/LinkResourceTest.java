package com.wesabe.grendel.resources.tests;

import com.google.inject.Provider;
import com.wesabe.grendel.auth.Credentials;
import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.resources.LinkResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.security.SecureRandom;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class LinkResourceTest {
	private static abstract class Context {
		protected UserRepository userRepository;
		protected DocumentRepository documentRepository;
		protected SecureRandom random;
		protected Credentials credentials;
		protected Session session;
		protected User user, reader;
		protected UnlockedKeySet keySet;
		protected Document document;
		protected byte[] body;
		protected LinkResource resource;
		
		public void setup() throws Exception {
			this.body = "one two three four five".getBytes();
			
			this.user = mock(User.class);
			
			this.reader = mock(User.class);
			
			this.keySet = mock(UnlockedKeySet.class);
			
			this.document = mock(Document.class);
			when(document.decryptBody(keySet)).thenReturn(body);
			
			this.session = mock(Session.class);
			when(session.getUser()).thenReturn(user);
			when(session.getKeySet()).thenReturn(keySet);
			
			this.userRepository = mock(UserRepository.class);
			when(userRepository.findById("frank")).thenReturn(reader);
			
			this.documentRepository = mock(DocumentRepository.class);
			when(documentRepository.findByOwnerAndName(user, "document1.txt")).thenReturn(document);
			
			this.random = mock(SecureRandom.class);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userRepository, "bob")).thenReturn(session);
			
			
			this.resource = new LinkResource(userRepository, documentRepository, new Provider<SecureRandom>() {
				@Override
				public SecureRandom get() {
					return random;
				}
			});
		}
	}
	
	public static class Creating_A_Link extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itThrowsA404IfTheReaderDoesNotExist() throws Exception {
			when(userRepository.findById("frank")).thenReturn(null);
			
			try {
				resource.createLink(credentials, "bob", "document1.txt", "frank");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itThrowsA404IfTheDocumentDoesNotExist() throws Exception {
			when(documentRepository.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			try {
				resource.createLink(credentials, "bob", "document1.txt", "frank");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itLinksTheUserReEncryptsTheDocumentAndSavesIt() throws Exception {
			resource.createLink(credentials, "bob", "document1.txt", "frank");
			
			final InOrder inOrder = inOrder(document, documentRepository);
			inOrder.verify(document).linkUser(reader);
			inOrder.verify(document).encryptAndSetBody(keySet, random, body);
			inOrder.verify(documentRepository).saveOrUpdate(document);
		}
		
		@Test
		public void itReturnsA204NoContent() throws Exception {
			final Response r = resource.createLink(credentials, "bob", "document1.txt", "frank");
			
			assertThat(r.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
		}
	}
	
	public static class Deleting_A_Link extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itThrowsA404IfTheReaderDoesNotExist() throws Exception {
			when(userRepository.findById("frank")).thenReturn(null);
			
			try {
				resource.deleteLink(credentials, "bob", "document1.txt", "frank");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itThrowsA404IfTheDocumentDoesNotExist() throws Exception {
			when(documentRepository.findByOwnerAndName(user, "document1.txt")).thenReturn(null);
			
			try {
				resource.deleteLink(credentials, "bob", "document1.txt", "frank");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itUnlinksTheUserReEncryptsTheDocumentAndSavesIt() throws Exception {
			resource.deleteLink(credentials, "bob", "document1.txt", "frank");
			
			final InOrder inOrder = inOrder(document, documentRepository);
			inOrder.verify(document).unlinkUser(reader);
			inOrder.verify(document).encryptAndSetBody(keySet, random, body);
			inOrder.verify(documentRepository).saveOrUpdate(document);
		}
		
		@Test
		public void itReturnsA204NoContent() throws Exception {
			final Response r = resource.deleteLink(credentials, "bob", "document1.txt", "frank");
			
			assertThat(r.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
		}
	}
}

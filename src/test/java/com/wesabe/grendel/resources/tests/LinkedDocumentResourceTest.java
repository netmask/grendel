package com.wesabe.grendel.resources.tests;

import com.wesabe.grendel.auth.Session;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import com.wesabe.grendel.resources.LinkedDocumentResource;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static java.text.DateFormat.getDateTimeInstance;
import java.text.SimpleDateFormat;

import static java.util.TimeZone.getTimeZone;
import static javax.ws.rs.core.MediaType.valueOf;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class LinkedDocumentResourceTest {
	private static abstract class Context {
		protected UserRepository userRepository;
		protected DocumentRepository documentRepository;
		protected Credentials credentials;
		protected User owner, user;
		protected UnlockedKeySet keySet;
		protected Session session;
		protected Document document;
		protected LinkedDocumentResource resource;
		
		public void setup() throws Exception {
			this.owner = mock(User.class);
			this.user = mock(User.class);
			
			this.userRepository = mock(UserRepository.class);
			when(userRepository.findById("frank")).thenReturn(owner);
			
			this.keySet = mock(UnlockedKeySet.class);
			
			this.document = mock(Document.class);
			when(document.getName()).thenReturn("document1.txt");
			when(document.getContentType()).thenReturn(MediaType.TEXT_PLAIN_TYPE);
			when(document.getModifiedAt()).thenReturn(new DateTime(2009, 12, 29, 8, 42, 32, 00, DateTimeZone.UTC));
			when(document.decryptBody(keySet)).thenReturn("yay for everyone".getBytes());
			when(document.isLinked(user)).thenReturn(true);
			
			this.documentRepository = mock(DocumentRepository.class);
			when(documentRepository.findByOwnerAndName(owner, "document1.txt")).thenReturn(document);
			
			this.session = new Session(user, keySet);
			
			this.credentials = mock(Credentials.class);
			when(credentials.buildSession(userRepository, "bob")).thenReturn(session);
			
			this.resource = new LinkedDocumentResource(userRepository, documentRepository);
		}
	}
	
	public static class Showing_A_Linked_Document extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itThrowsA404IfTheOwnerDoesNotExist() throws Exception {
			when(userRepository.findById("frank")).thenReturn(null);
			
			try {
				resource.show(credentials, "bob", "frank", "document1.txt");
				fail("should have returned a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itThrowsA404IfTheDocumentDoesNotExist() throws Exception {
			when(documentRepository.findByOwnerAndName(owner, "document1.txt")).thenReturn(null);
			
			try {
				resource.show(credentials, "bob", "frank", "document1.txt");
				fail("should have returned a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itThrowsA404IfTheDocumentIsNotLinkedToTheUser() throws Exception {
			when(document.isLinked(user)).thenReturn(false);
			
			try {
				resource.show(credentials, "bob", "frank", "document1.txt");
				fail("should have returned a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itReturnsTheDecryptedDocument() throws Exception {
			final Response r = resource.show(credentials, "bob", "frank", "document1.txt");
			
			SimpleDateFormat formatter = (SimpleDateFormat) getDateTimeInstance();
			formatter.setTimeZone(getTimeZone("UTC"));
			formatter.applyPattern("EEE MMM dd HH:mm:ss z yyyy");
			
			assertThat(r.getStatus()).isEqualTo(Status.OK.getStatusCode());
			assertThat(r.getMetadata().getFirst("Content-Type")).isEqualTo(valueOf("text/plain"));
			assertThat(r.getMetadata().getFirst("Cache-Control").toString()).isEqualTo("private, no-cache, no-store, no-transform");
			assertThat(formatter.format(r.getMetadata().getFirst("Last-Modified"))).isEqualTo("Tue Dec 29 08:42:32 UTC 2009");
			assertThat((byte[]) r.getEntity()).isEqualTo("yay for everyone".getBytes());
		}
	}
	
	public static class Deleting_A_Linked_Document extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itThrowsA404IfTheOwnerDoesNotExist() throws Exception {
			when(userRepository.findById("frank")).thenReturn(null);
			
			try {
				resource.delete(credentials, "bob", "frank", "document1.txt");
				fail("should have returned a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itThrowsA404IfTheDocumentDoesNotExist() throws Exception {
			when(documentRepository.findByOwnerAndName(owner, "document1.txt")).thenReturn(null);
			
			try {
				resource.delete(credentials, "bob", "frank", "document1.txt");
				fail("should have returned a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itThrowsA404IfTheDocumentIsNotLinkedToTheUser() throws Exception {
			when(document.isLinked(user)).thenReturn(false);
			
			try {
				resource.delete(credentials, "bob", "frank", "document1.txt");
				fail("should have returned a 404 but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
		
		@Test
		public void itUnlinksTheUserFromTheDocument() throws Exception {
			resource.delete(credentials, "bob", "frank", "document1.txt");
			
			final InOrder inOrder = inOrder(document, documentRepository);
			inOrder.verify(document).unlinkUser(user);
			inOrder.verify(documentRepository).saveOrUpdate(document);
		}
		
		@Test
		public void itReturnsNoContent() throws Exception {
			final Response r = resource.delete(credentials, "bob", "frank", "document1.txt");
			
			assertThat(r.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
		}
	}
}

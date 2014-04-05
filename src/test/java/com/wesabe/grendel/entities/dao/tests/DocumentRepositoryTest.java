package com.wesabe.grendel.entities.dao.tests;

import com.google.inject.Provider;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.DocumentRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import javax.ws.rs.core.MediaType;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class DocumentRepositoryTest {
	private static abstract class Context {
		protected Session session;
		protected DocumentRepository dao;
		
		public void setup() throws Exception {
			this.session = mock(Session.class);
			this.dao = new DocumentRepository(new Provider<Session>() {
				@Override
				public Session get() {
					return session;
				}
			});
		}
	}
	
	public static class Finding_A_Document_By_Owner_And_Name extends Context {
		private Query query;
		private Document doc;
		private User owner;
		private String name;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.doc = mock(Document.class);
			this.owner = mock(User.class);
			this.name = "woohoo.txt";
			
			this.query = mock(Query.class);
			when(query.setString(anyString(), anyString())).thenReturn(query);
			when(query.setParameter(anyString(), anyObject())).thenReturn(query);
			when(query.uniqueResult()).thenReturn(doc);
			
			when(session.getNamedQuery(anyString())).thenReturn(query);
		}
		
		@Test
		public void itCreatesANamedQueryAndParameterizesIt() throws Exception {
			dao.findByOwnerAndName(owner, name);
			
			final InOrder inOrder = inOrder(session, query);
			inOrder.verify(session).getNamedQuery("com.wesabe.grendel.entities.Document.ByOwnerAndName");
			inOrder.verify(query).setParameter("owner", owner)
;			inOrder.verify(query).setString("name", name);
		}
		
		@Test
		public void itReturnsTheDocument() throws Exception {
			assertThat(dao.findByOwnerAndName(owner, name)).isEqualTo(doc);
		}
	}
	
	public static class Deleting_A_Document extends Context {
		private Document doc;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.doc = mock(Document.class);
		}
		
		@Test
		public void itDeletesTheDocument() throws Exception {
			dao.delete(doc);
			
			verify(session).delete(doc);
		}
	}
	
	public static class Saving_Or_Creating_A_Document extends Context {
		private Document doc;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.doc = mock(Document.class);
		}
		
		@Test
		public void itReturnsTheDocument() throws Exception {
			assertThat(dao.saveOrUpdate(doc)).isEqualTo(doc);
		}
		
		@Test
		public void itCreatesADatabaseEntry() throws Exception {
			dao.saveOrUpdate(doc);
			
			verify(session).saveOrUpdate(doc);
		}
	}
	
	public static class Building_A_New_Document extends Context {
		private User user;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
		}
		
		@Test
		public void itReturnsADocumentWithTheOwner() throws Exception {
			assertThat(dao.newDocument(user, "document1.txt", MediaType.APPLICATION_OCTET_STREAM_TYPE).getOwner()).isEqualTo(user);
		}
		
		@Test
		public void itReturnsADocumentWithTheName() throws Exception {
			assertThat(dao.newDocument(user, "document1.txt", MediaType.APPLICATION_OCTET_STREAM_TYPE).getName()).isEqualTo("document1.txt");
		}
		
		@Test
		public void itReturnsADocumentWithTheOnwerr() throws Exception {
			assertThat(dao.newDocument(user, "document1.txt", MediaType.APPLICATION_OCTET_STREAM_TYPE).getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_TYPE);
		}
	}
}

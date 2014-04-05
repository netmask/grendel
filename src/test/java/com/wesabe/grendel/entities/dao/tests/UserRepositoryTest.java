package com.wesabe.grendel.entities.dao.tests;

import static com.google.common.collect.ImmutableList.of;
import com.google.inject.Provider;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import java.io.Serializable;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class UserRepositoryTest {
	private static abstract class Context {
		protected Session session;
		protected UserRepository dao;
		
		public void setup() throws Exception {
			this.session = mock(Session.class);
			this.dao = new UserRepository(new Provider<Session>() {
				@Override
				public Session get() {
					return session;
				}
			});
		}
	}
	
	public static class Checking_For_A_Users_Existence extends Context {
		private Query query;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.query = mock(Query.class);
			when(query.setString(anyString(), anyString())).thenReturn(query);
			
			when(session.getNamedQuery(anyString())).thenReturn(query);
		}
		
		@Test
		public void itCreatesANamedQueryAndParameterizesIt() throws Exception {
			dao.contains("woo");
			
			final InOrder inOrder = inOrder(session, query);
			inOrder.verify(session).getNamedQuery("com.wesabe.grendel.entities.User.Exists");
			inOrder.verify(query).setString("id", "woo");
		}
		
		@Test
		public void itReturnsTrueIfTheUserWasFound() throws Exception {
			when(query.uniqueResult()).thenReturn("woo");
			
			assertThat(dao.contains("woo")).isTrue();
		}
		
		@Test
		public void itReturnsFalseIfTheUserWasNotFound() throws Exception {
			when(query.uniqueResult()).thenReturn(null);
			
			assertThat(dao.contains("woo")).isFalse();
		}
	}
	
	public static class Finding_A_User_By_Id extends Context {
		private User user;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
		}
		
		@Test
		public void itReturnsTheUser() throws Exception {
			when(session.get(any(Class.class), any(Serializable.class))).thenReturn(user);
			
			assertThat(dao.findById("woo")).isEqualTo(user);
		}
		
		@Test
		public void itScopesTheQueryToTheClassAndId() throws Exception {
			dao.findById("woo");
			
			verify(session).get(User.class, "woo");
		}
	}
	
	public static class Finding_All_Users extends Context {
		private User user;
		private Query query;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
			this.query = mock(Query.class);
			
			when(session.getNamedQuery(anyString())).thenReturn(query);
		}
		
		@Test
		public void itCreatesANamedQueryAndParameterizesIt() throws Exception {
			dao.findAll();
			
			verify(session).getNamedQuery("com.wesabe.grendel.entities.User.All");
		}
		
		@Test
		public void itReturnsTheUsers() throws Exception {
			when(query.list()).thenReturn(of(user));

			assertThat(dao.findAll()).containsOnly(user);
		}
	}
	
	public static class Saving_Or_Creating_A_User extends Context {
		private User user;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
		}
		
		@Test
		public void itReturnsTheUser() throws Exception {
			assertThat(dao.saveOrUpdate(user)).isEqualTo(user);
		}
		
		@Test
		public void itCreatesADatabaseEntry() throws Exception {
			dao.saveOrUpdate(user);
			
			verify(session).saveOrUpdate(user);
		}
	}
	
	public static class Deleting_A_User extends Context {
		private User user;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.user = mock(User.class);
		}
		
		@Test
		public void itDeletesTheUser() throws Exception {
			dao.delete(user);
			
			verify(session).delete(user);
		}
	}
}

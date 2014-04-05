package com.wesabe.grendel.resources.tests;

import static com.google.common.collect.ImmutableList.of;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.openpgp.KeySetGenerator;
import com.wesabe.grendel.decorators.CreateUserRepresentation;
import com.wesabe.grendel.decorators.UserListRepresentation.UserListItem;
import com.wesabe.grendel.resources.UsersResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static java.net.URI.create;
import java.util.List;
import static javax.ws.rs.core.UriBuilder.fromUri;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class UsersResourceTest {
	private static abstract class Context {
		protected KeySetGenerator generator;
		protected UserRepository userRepository;
		protected UsersResource resource;
		
		public void setup() throws Exception {
			this.generator = mock(KeySetGenerator.class);
			this.userRepository = mock(UserRepository.class);
			
			this.resource = new UsersResource(generator, userRepository);
		}
	}
	
	public static class Listing_All_Users extends Context {
		private User user;
		private UriInfo uriInfo;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return fromUri("http://example.com");
				}
			});
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("mrpeeper");
			when(user.toString()).thenReturn("mrpeeper");
			
			when(userRepository.findAll()).thenReturn(of(user));
		}
		
		@Test
		public void itFindsAllUsers() throws Exception {
			resource.list(uriInfo);
			
			verify(userRepository).findAll();
		}
		
		@Test
		public void itReturnsAListOfAllUsers() throws Exception {
			final List<UserListItem> list = resource.list(uriInfo).getUsers();
			
			assertThat(list).hasSize(1);
			
			assertThat(list.get(0).getId()).isEqualTo("mrpeeper");
			assertThat(list.get(0).getUri()).isEqualTo("http://example.com/users/mrpeeper");
		}
	}
	
	public static class Creating_A_New_User extends Context {
		private UriInfo uriInfo;
		private CreateUserRepresentation request;
		private KeySet keySet;
		private User user;
		private UriBuilder uriBuilder;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			
			this.request = mock(CreateUserRepresentation.class);
			when(request.getId()).thenReturn("username");
			when(request.getPassword()).thenReturn("password".toCharArray());
			
			this.keySet = mock(KeySet.class);
			when(keySet.getEncoded()).thenReturn(new byte[] { 1, 2, 3 });
			when(keySet.getUserID()).thenReturn("username");

			this.user = mock(User.class);
			
			when(generator.generate(anyString(), any(char[].class))).thenReturn(keySet);
			
			when(userRepository.contains(anyString())).thenReturn(false);
			when(userRepository.saveOrUpdate(any(User.class))).thenReturn(user);
			
			this.uriBuilder = mock(UriBuilder.class);
			when(uriBuilder.path(any(Class.class))).thenReturn(uriBuilder);
			when(uriBuilder.build(anyVararg())).thenReturn(create("http://example.com/woot/"));
			when(uriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
		}
		
		@Test
		public void itValidatesTheRequest() throws Exception {
			resource.create(uriInfo, request);
			
			verify(request).validate();
		}
		
		@Test
		public void itSanitizesTheRequest() throws Exception {
			resource.create(uriInfo, request);
			
			verify(request).sanitize();
		}
		
		@Test
		public void itChecksToSeeIfTheUsernameIsTaken() throws Exception {
			resource.create(uriInfo, request);
			
			verify(userRepository).contains("username");
		}
		
		@Test
		public void itGeneratesAKeySet() throws Exception {
			resource.create(uriInfo, request);
			
			final ArgumentCaptor<char[]> password = forClass(char[].class);
			verify(generator).generate(eq("username"), password.capture());
			assertThat(password.getValue()).isEqualTo("password".toCharArray());
		}
		
		@Test
		public void itCreatesANewUser() throws Exception {
			resource.create(uriInfo, request);
			
			final ArgumentCaptor<User> userCaptor = forClass(User.class);
			verify(userRepository).saveOrUpdate(userCaptor.capture());
			
			assertThat(userCaptor.getValue().getKeySet()).isSameAs(keySet);
		}
		
		@Test
		public void itReturnsA201CreatedWithTheUsersLocation() throws Exception {
			final Response r = resource.create(uriInfo, request);
			
			assertThat(r.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
			assertThat(r.getMetadata().getFirst("Location")).isEqualTo(create("http://example.com/woot/"));
		}
	}
	
	public static class Creating_A_New_User_With_A_Conflicting_Username extends Context {
		private UriInfo uriInfo;
		private CreateUserRepresentation request;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.uriInfo = mock(UriInfo.class);
			
			this.request = mock(CreateUserRepresentation.class);
			when(request.getId()).thenReturn("username");
			when(request.getPassword()).thenReturn("password".toCharArray());
			
			when(userRepository.contains(anyString())).thenReturn(true);
		}
		
		@Test
		public void itThrowsAValidationException() throws Exception {
			try {
				resource.create(uriInfo, request);
			} catch (ValidationException e) {
				final String msg = (String) e.getResponse().getEntity();
				
				assertThat(msg).isEqualTo(
					"Grendel was unable to process your request for the following reason(s):\n" +
					"\n" +
					"* username is already taken\n"
				);
			}
		}
	}
}

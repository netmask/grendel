package com.wesabe.grendel.decorators.tests;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.decorators.UserListRepresentation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import static javax.ws.rs.core.UriBuilder.fromUri;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class UserListRepresentationTest {
	public static class Serializing_A_User_List {
		private UriInfo uriInfo;
		private UserListRepresentation rep;
		private User user;
		
		@Before
		public void setup() throws Exception {
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return fromUri("http://example.com");
				}
			});
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("mrpeepers");
			when(user.toString()).thenReturn("mrpeepers");
			
			this.rep = new UserListRepresentation(uriInfo, of(user));
		}
		
		@Test
		public void itSerializesIntoJSON() throws Exception {
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writeValueAsString(rep);
			
			final ObjectNode entity = mapper.readValue(json, ObjectNode.class);
			final List<JsonNode> users = copyOf(entity.get("users").getElements());
			
			assertThat(users).hasSize(1);
			
			final JsonNode user = users.get(0);
			assertThat(user.get("id").getTextValue()).isEqualTo("mrpeepers");
			assertThat(user.get("uri").getTextValue()).isEqualTo("http://example.com/users/mrpeepers");
		}
	}
}

package com.wesabe.grendel.representations.tests;

import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.openpgp.KeySet;
import com.wesabe.grendel.representations.UserInfoRepresentation;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.UriBuilder;
import static javax.ws.rs.core.UriBuilder.fromUri;
import javax.ws.rs.core.UriInfo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class UserInfoRepresentationTest {
	public static class Serializing_User_Info {
		private UriInfo uriInfo;
		private UserInfoRepresentation rep;
		private KeySet keySet;
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
			
			this.keySet = mock(KeySet.class);
			when(keySet.toString()).thenReturn("[2048-RSA 5F2910, 2048-RSA 23B19D3]");
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("mrpeepers");
			when(user.getKeySet()).thenReturn(keySet);
			when(user.getCreatedAt()).thenReturn(new DateTime(2009, 12, 22, 4, 9, 00, 00, DateTimeZone.UTC));
			when(user.getModifiedAt()).thenReturn(new DateTime(2009, 12, 28, 14, 23, 00, 00, DateTimeZone.UTC));
			
			this.rep = new UserInfoRepresentation(uriInfo, user);
		}
		
		@Test
		public void itSerializesIntoJSON() throws Exception {
			final ObjectMapper mapper = new ObjectMapper();
			
			final String json = mapper.writeValueAsString(rep);
			
			final ObjectNode node = mapper.readValue(json, ObjectNode.class);
			
			assertThat(node.get("id").getTextValue()).isEqualTo("mrpeepers");
			assertThat(node.get("created-at").getTextValue()).isEqualTo("20091222T040900Z");
			assertThat(node.get("modified-at").getTextValue()).isEqualTo("20091228T142300Z");
			assertThat(node.get("keys").getTextValue()).isEqualTo("[2048-RSA 5F2910, 2048-RSA 23B19D3]");
		}
	}
}

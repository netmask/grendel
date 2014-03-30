package com.wesabe.grendel.decorators.tests;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableSet.of;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.decorators.DocumentListRepresentation;
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
public class DocumentListRepresentationTest {
	public static class Serializing_A_Document_List {
		private UriInfo uriInfo;
		private DocumentListRepresentation rep;
		private Document doc;
		private User owner;
		
		@Before
		public void setup() throws Exception {
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return fromUri("http://example.com");
				}
			});
			
			this.owner = mock(User.class);
			when(owner.getId()).thenReturn("mrpeepers");
			when(owner.toString()).thenReturn("mrpeepers");
			
			this.doc = mock(Document.class);
			when(doc.getName()).thenReturn("document1.txt");
			when(doc.toString()).thenReturn("document1.txt");
			when(doc.getOwner()).thenReturn(owner);
			
			this.rep = new DocumentListRepresentation(uriInfo, of(doc));
		}
		
		@Test
		public void itSerializesIntoJSON() throws Exception {
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writeValueAsString(rep);
			
			final ObjectNode entity = mapper.readValue(json, ObjectNode.class);
			final List<JsonNode> documents = copyOf(entity.get("documents").getElements());
			
			assertThat(documents).hasSize(1);
			
			final JsonNode document = documents.get(0);
			assertThat(document.get("name").getTextValue()).isEqualTo("document1.txt");
			assertThat(document.get("uri").getTextValue()).isEqualTo("http://example.com/users/mrpeepers/documents/document1.txt");
		}
	}
}

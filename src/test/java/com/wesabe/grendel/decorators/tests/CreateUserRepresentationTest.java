package com.wesabe.grendel.decorators.tests;

import com.wesabe.grendel.decorators.CreateUserRepresentation;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Enclosed.class)
public class CreateUserRepresentationTest {
	public static class A_Valid_New_User_Request {
		private CreateUserRepresentation req;
		
		@Before
		public void setup() throws Exception {
			this.req = new CreateUserRepresentation();
			
			req.setId("dingo");
			req.setPassword("happenstance".toCharArray());
		}
		
		@Test
		public void itIsValid() throws Exception {
			try {
				req.validate();
				assertThat(true).isTrue();
			} catch (ValidationException e) {
				fail("didn't expect a ValidationException but one was thrown");
			}
		}
		
		@Test
		public void itHasAUsername() throws Exception {
			assertThat(req.getId()).isEqualTo("dingo");
		}
		
		@Test
		public void itHasAPassword() throws Exception {
			assertThat(req.getPassword()).isEqualTo("happenstance".toCharArray());
		}
		
		@Test
		public void itCanBeSanitized() throws Exception {
			assertThat(req.getPassword()).isEqualTo("happenstance".toCharArray());
			req.sanitize();
			assertThat(req.getPassword()).isEqualTo("\0\0\0\0\0\0\0\0\0\0\0\0".toCharArray());
		}
	}
	
	public static class An_Invalid_New_User_Request {
		private CreateUserRepresentation req;
		
		@Before
		public void setup() throws Exception {
			this.req = new CreateUserRepresentation();
		}
		
		@Test
		public void itThrowsAnExceptionOnValidation() throws Exception {
			try {
				req.validate();
				fail("should have thrown a ValidationException but didn't");
			} catch (ValidationException e) {
				final String msg = (String) e.getResponse().getEntity();
				
				assertThat(msg).isEqualTo(
					"Grendel was unable to process your request for the following reason(s):" +
					"\n" +
					"\n" +
					"* missing required property: id\n" +
					"* missing required property: password\n"
				);
			}
		}
	}
	
	public static class Deserializing_From_Json {
		@Test
		public void itDeserializesJSON() throws Exception {
			final String json = "{\"id\":\"mrpeepers\",\"password\":\"hoohah\"}";
			
			final ObjectMapper mapper = new ObjectMapper();
			final CreateUserRepresentation rep = mapper.readValue(json, CreateUserRepresentation.class);
			
			assertThat(rep.getId()).isEqualTo("mrpeepers");
			assertThat(rep.getPassword()).isEqualTo("hoohah".toCharArray());
		}
	}
}

package com.wesabe.grendel.decorators.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class ValidationExceptionTest {
	public static class Throwing_A_Validation_Exception {
		private ValidationException e;
		
		@Before
		public void setup() throws Exception {
			this.e = new ValidationException();
			e.addReason("insufficient zazz");
			e.addReason("over-familiar tone");
			e.missingRequiredProperty("dingo");
		}
		
		@Test
		public void itHasReasons() throws Exception {
			assertThat(e.hasReasons()).isTrue();
		}
		
		@Test
		public void itReturnsA422WithAnErrorMessage() throws Exception {
			final Response r = e.getResponse();
			
			assertThat(r.getStatus()).isEqualTo(422);
			assertThat(r.getMetadata().get("Content-Type").contains("text/plain"));
			assertThat(r.getEntity()).isEqualTo(
				"Grendel was unable to process your request for the following reason(s):" +
				"\n" +
				"\n" +
				"* insufficient zazz\n" +
				"* over-familiar tone\n" +
				"* missing required property: dingo\n"
			);
		}
	}
}

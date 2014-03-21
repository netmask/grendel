package com.wesabe.grendel.openpgp.tests;

import com.wesabe.grendel.openpgp.CryptographicException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class CryptographicExceptionTest {
	public static class A_Cryptographic_Exception_With_A_Message {
		private CryptographicException e = new CryptographicException("message");
		
		@Test
		public void itHasAMessage() throws Exception {
			assertThat(e.getMessage()).isEqualTo("message");
		}
		
		@Test
		public void itDoesNotHaveACause() throws Exception {
			assertThat(e.getCause()).isNull();
		}
	}
	
	public static class A_Cryptographic_Exception_With_A_Cause {
		private IllegalArgumentException cause = new IllegalArgumentException("AUGH");
		private CryptographicException e = new CryptographicException(cause);
		
		@Test
		public void itHasTheSameMessageAsTheCause() throws Exception {
			assertThat(e.getMessage()).isEqualTo("java.lang.IllegalArgumentException: AUGH");
		}
		
		@Test
		public void itHasACause() throws Exception {
			assertThat(e.getCause()).isSameAs(cause);
		}
	}
	
	public static class A_Cryptographic_Exception_With_A_Message_And_A_Cause {
		private IllegalArgumentException cause = new IllegalArgumentException("AUGH");
		private CryptographicException e = new CryptographicException("message", cause);
		
		@Test
		public void itHasAMessage() throws Exception {
			assertThat(e.getMessage()).isEqualTo("message");
		}
		
		@Test
		public void itHasACause() throws Exception {
			assertThat(e.getCause()).isSameAs(cause);
		}
	}
}

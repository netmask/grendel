package com.wesabe.grendel.modules.tests;

import com.wesabe.grendel.modules.SecureRandomProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.security.SecureRandom;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class SecureRandomProviderTest {
	public static class Providing_A_CSPRNG {
		private SecureRandomProvider provider;
		
		@Before
		public void setup() throws Exception {
			getLogger(SecureRandomProvider.class.getCanonicalName()).setLevel(Level.OFF);
			this.provider = new SecureRandomProvider();
		}
		
		@Test
		public void itProvidesASecureRandomInstance() throws Exception {
			assertThat(provider.getObject()).isInstanceOfAny(SecureRandom.class);
		}
		
		@Test
		public void itProvidesTheSameInstance() throws Exception {
			assertThat(provider.getObject()).isSameAs(provider.getObject());
		}
	}
}

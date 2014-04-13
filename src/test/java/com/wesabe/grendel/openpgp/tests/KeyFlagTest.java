package com.wesabe.grendel.openpgp.tests;

import com.wesabe.grendel.openpgp.KeyFlag;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static com.google.common.collect.ImmutableSet.of;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class KeyFlagTest {
	public static class Certification {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(KeyFlag.CERTIFICATION.toInteger()).isEqualTo(0x01);
		}
	}
	
	public static class Signing {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(KeyFlag.SIGNING.toInteger()).isEqualTo(0x02);
		}
	}
	
	public static class Encryption {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(KeyFlag.ENCRYPTION.toInteger()).isEqualTo(0x0C);
		}
	}
	
	public static class Splitting {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(KeyFlag.SPLIT.toInteger()).isEqualTo(0x10);
		}
	}
	
	public static class Authentication {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(KeyFlag.AUTHENTICATION.toInteger()).isEqualTo(0x20);
		}
	}
	
	public static class Sharing {
		@Test
		public void itHasTheSameValueAsTheBCTag() throws Exception {
			assertThat(KeyFlag.SHARED.toInteger()).isEqualTo(0x80);
		}
	}
	
	public static class Default_Flags_For_Master_Keys {
		@Test
		public void itCanSignBeSplitAndCanAuthenticate() throws Exception {
			assertThat(KeyFlag.MASTER_KEY_DEFAULTS).isEqualTo(of(KeyFlag.AUTHENTICATION, KeyFlag.SIGNING, KeyFlag.SPLIT));
		}
	}
	
	public static class Default_Flags_For_Sub_Keys {
		@Test
		public void itCanSignBeSplitAndCanAuthenticate() throws Exception {
			assertThat(KeyFlag.SUB_KEY_DEFAULTS).isEqualTo(of(KeyFlag.ENCRYPTION, KeyFlag.SPLIT));
		}
	}
}

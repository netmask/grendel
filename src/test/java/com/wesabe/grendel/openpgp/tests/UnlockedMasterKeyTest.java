package com.wesabe.grendel.openpgp.tests;

import com.wesabe.grendel.openpgp.MasterKey;
import static com.wesabe.grendel.openpgp.MasterKey.load;
import com.wesabe.grendel.openpgp.UnlockedMasterKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.FileInputStream;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class UnlockedMasterKeyTest {
	public static class An_Unlocked_Master_Key {
		private UnlockedMasterKey key;
		
		@Before
		public void setup() throws Exception {
			final PGPSecretKeyRing keyRing;
            try (FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg")) {
                keyRing = new PGPSecretKeyRing(keyRingFile);
            }
			
			this.key = load(keyRing.getSecretKey(0x8C7035EF8838238CL)).unlock("test".toCharArray());
		}
		
		@Test
		public void itHasAPrivateKey() throws Exception {
			assertThat(key.getPrivateKey()).isNotNull();
		}
		
		@Test
		public void itReturnsItselfWhenUnlocked() throws Exception {
			assertThat(key.unlock("blah".toCharArray())).isSameAs(key);
		}
	}
}

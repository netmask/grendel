package com.wesabe.grendel.openpgp.tests;

import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.KeySet;
import static com.wesabe.grendel.openpgp.KeySet.load;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.security.SecureRandom;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Enclosed.class)
public class UnlockedKeySetTest {
	public static class An_Unlocked_Key_Set {
		private KeySet keySet;
		private UnlockedKeySet unlockedKeySet;

		@Before
		public void setup() throws Exception {
			final PGPSecretKeyRing keyRing;
            try (FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg")) {
                keyRing = new PGPSecretKeyRing(keyRingFile);
            }

			this.keySet = load(keyRing);
			this.unlockedKeySet = keySet.unlock("test".toCharArray());
		}
		
		@Test
		public void itHasAnUnlockedMasterKey() throws Exception {
			assertThat(unlockedKeySet.getUnlockedMasterKey().getKeyID()).isEqualTo(0x8C7035EF8838238CL);
		}
		
		@Test
		public void itHasAnUnlockedSubKey() throws Exception {
			assertThat(unlockedKeySet.getUnlockedSubKey().getKeyID()).isEqualTo(0xA3A5D038FF30574EL);
		}
		
		@Test
		public void itCanRelockTheKeySetWithADifferentPassphrase() throws Exception {
			final KeySet newKeySet = unlockedKeySet.relock("test".toCharArray(), "yay".toCharArray(), new SecureRandom());
			
			try {
				newKeySet.unlock("test".toCharArray());
				fail("should have thrown a CryptographicException but didn't");
			} catch (CryptographicException e) {
				assertThat(e.getMessage()).isEqualTo("incorrect passphrase");
			}
			
			try {
				newKeySet.unlock("yay".toCharArray());
			} catch (CryptographicException e) {
				e.printStackTrace();
				fail("should not have thrown a CryptographicException but did");
			}
			
		}
	}
}

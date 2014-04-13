package com.wesabe.grendel.openpgp.tests;

import com.wesabe.grendel.openpgp.MasterKey;
import com.wesabe.grendel.openpgp.UnlockedSubKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.util.List;

import static com.wesabe.grendel.openpgp.MasterKey.load;
import static com.wesabe.grendel.openpgp.SubKey.load;
import static com.wesabe.grendel.util.Iterators.toList;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class UnlockedSubKeyTest {
	public static class An_Unlocked_Sub_Key {
		private UnlockedSubKey key;
		
		@Before
		public void setup() throws Exception {
			final PGPSecretKeyRing keyRing;
            try (FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg")) {
                keyRing = new PGPSecretKeyRing(keyRingFile);
            }
			
			final List<PGPSecretKey> secretKeys = toList(keyRing.getSecretKeys());
			
			final MasterKey masterKey = load(secretKeys.get(0));
			this.key = load(secretKeys.get(1), masterKey).unlock("test".toCharArray());
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

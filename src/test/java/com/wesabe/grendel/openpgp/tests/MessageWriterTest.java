package com.wesabe.grendel.openpgp.tests;

import com.google.common.collect.ImmutableList;
import com.wesabe.grendel.openpgp.KeySet;
import static com.wesabe.grendel.openpgp.KeySet.load;
import static com.wesabe.grendel.openpgp.KeySet.load;
import com.wesabe.grendel.openpgp.MessageReader;
import com.wesabe.grendel.openpgp.MessageWriter;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Random;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class MessageWriterTest {
	public static class Encrypting_A_Message {
		private UnlockedKeySet owner;
		private UnlockedKeySet recipient;
		private byte[] original;
		
		@Before
		public void setup() throws Exception {
			final FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg");
			this.owner = load(keyRingFile).unlock("test".toCharArray());
			
			final FileInputStream anotherKeyRingFile = new FileInputStream("src/test/resources/another-secret-keyring.gpg");
			this.recipient = load(anotherKeyRingFile).unlock("test2".toCharArray());
			
			// 1MB of data
			final Random random = new Random();
			this.original = new byte[1 << 20];
			random.nextBytes(original);
		}
		
		@Test
		public void itIsDecryptableByMessageReader() throws Exception {
			final MessageWriter writer = new MessageWriter(owner, ImmutableList.<KeySet>of(recipient), new SecureRandom());
			
			final byte[] encrypted = writer.write(original);
			
			final MessageReader reader = new MessageReader(owner, recipient);
			final byte[] decrypted = reader.read(encrypted);
			
			assertThat(decrypted).isEqualTo(original);
		}
	}
}


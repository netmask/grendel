package com.wesabe.grendel.openpgp.tests;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.ImmutableList.of;
import com.wesabe.grendel.openpgp.*;
import static com.wesabe.grendel.openpgp.MasterKey.load;
import static com.wesabe.grendel.openpgp.SubKey.load;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.FileInputStream;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class SubKeyTest {
	public static class A_Sub_Key {
		private MasterKey masterKey;
		private SubKey key;

		@Before
		public void setup() throws Exception {
			final PGPSecretKeyRing keyRing;
            try (FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg")) {
                keyRing = new PGPSecretKeyRing(keyRingFile);
            }
			
			this.masterKey = load(keyRing.getSecretKey(0x8C7035EF8838238CL));
			this.key = load(keyRing.getSecretKey(0xA3A5D038FF30574EL), masterKey);
		}

		@Test
		public void itHasAnID() throws Exception {
			assertThat(key.getKeyID()).isEqualTo(0xA3A5D038FF30574EL);
		}
		
		@Test
		public void itHasAMasterKey() throws Exception {
			assertThat(key.getMasterKey().getKeyID()).isEqualTo(0x8C7035EF8838238CL);
		}
		
		@Test
		public void itHasAUserID() throws Exception {
			assertThat(key.getUserID()).isEqualTo("Sample Key <sample@wesabe.com>");
			assertThat(key.getUserIDs()).isEqualTo(of("Sample Key <sample@wesabe.com>"));
		}
		
		@Test
		public void itIsAnRSAKey() throws Exception {
			assertThat(key.getAlgorithm()).isEqualTo(AsymmetricAlgorithm.RSA);
		}
		
		@Test
		public void itIs2048BitsLong() throws Exception {
			assertThat(key.getSize()).isEqualTo(2048);
		}
		
		@Test
		public void itCanEncryptData() throws Exception {
			assertThat(key.canEncrypt()).isTrue();
		}
		
		@Test
		public void itCannotSignData() throws Exception {
			assertThat(key.canSign()).isFalse();
		}
		
		@Test
		public void itHasACreationTimestamp() throws Exception {
			assertThat(key.getCreatedAt()).isEqualTo(new DateTime(2009, 7, 9, 16, 23, 5, 0, DateTimeZone.UTC));
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredSymmetricAlgorithms() throws Exception {
			assertThat(key.getPreferredSymmetricAlgorithms())
				.isEqualTo(of(
					SymmetricAlgorithm.AES_256,
					SymmetricAlgorithm.AES_192,
					SymmetricAlgorithm.AES_128,
					SymmetricAlgorithm.CAST_128,
					SymmetricAlgorithm.TRIPLE_DES,
					SymmetricAlgorithm.IDEA
				));
		}
		
		@Test
		public void itHasPreferredCompressionAlgorithms() throws Exception {
			assertThat(key.getPreferredCompressionAlgorithms())
				.isEqualTo(of(
					CompressionAlgorithm.ZLIB,
					CompressionAlgorithm.BZIP2,
					CompressionAlgorithm.ZIP
				));
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredHashAlgorithms() throws Exception {
			assertThat(key.getPreferredHashAlgorithms())
				.isEqualTo(of(
					HashAlgorithm.SHA_1,
					HashAlgorithm.SHA_256,
					HashAlgorithm.RIPEMD_160
				));
		}
	}
}

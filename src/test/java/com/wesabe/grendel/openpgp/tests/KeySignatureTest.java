package com.wesabe.grendel.openpgp.tests;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.ImmutableList.of;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.ImmutableSet.of;
import com.wesabe.grendel.openpgp.*;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class KeySignatureTest {
	public static class A_Self_Signature {
		private PGPSecretKey key;
		private KeySignature signature;
		
		@Before
		public void setup() throws Exception {
			final PGPSecretKeyRing keyRing;
            try (FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg")) {
                keyRing = new PGPSecretKeyRing(keyRingFile);
            }
			
			this.key = keyRing.getSecretKey(0x8C7035EF8838238CL);
			final Iterator<?> sigs = key.getPublicKey().getSignatures();
			this.signature = new KeySignature((PGPSignature) sigs.next());
		}
		
		@Test
		public void itIsAPositiveCertification() throws Exception {
			assertThat(signature.getSignatureType()).isEqualTo(SignatureType.POSITIVE_CERTIFICATION);
		}
		
		@Test
		public void itHasAKeyID() throws Exception {
			assertThat(signature.getKeyID()).isEqualTo(0x8C7035EF8838238CL);
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasAHashAlgorithm() throws Exception {
			assertThat(signature.getHashAlgorithm()).isEqualTo(HashAlgorithm.SHA_1);
		}
		
		@Test
		public void itHasAKeyAlgorithm() throws Exception {
			assertThat(signature.getKeyAlgorithm()).isEqualTo(AsymmetricAlgorithm.RSA);
		}
		
		@Test
		public void itHasACreationTimestamp() throws Exception {
			assertThat(signature.getCreatedAt()).isEqualTo(new DateTime(2009, 7, 9, 16, 22, 3, 00, DateTimeZone.UTC));
		}
		
		@Test
		public void itHasKeyFlags() throws Exception {
			assertThat(signature.getKeyFlags()).isEqualTo(of(KeyFlag.CERTIFICATION, KeyFlag.SIGNING));
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredSymmetricAlgorithms() throws Exception {
			assertThat(signature.getPreferredSymmetricAlgorithms())
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
			assertThat(signature.getPreferredCompressionAlgorithms())
				.isEqualTo(of(
					CompressionAlgorithm.ZLIB,
					CompressionAlgorithm.BZIP2,
					CompressionAlgorithm.ZIP
				));
		}
		
		@SuppressWarnings("deprecation")
		@Test
		public void itHasPreferredHashAlgorithms() throws Exception {
			assertThat(signature.getPreferredHashAlgorithms())
				.isEqualTo(of(
					HashAlgorithm.SHA_1,
					HashAlgorithm.SHA_256,
					HashAlgorithm.RIPEMD_160
				));
		}
	}
}

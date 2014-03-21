package com.wesabe.grendel.entities.tests;

import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.openpgp.KeySet;
import static com.wesabe.grendel.openpgp.KeySet.load;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import static org.fest.assertions.Assertions.assertThat;
import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class UserTest {
	public static class A_Fresh_User {
		private KeySet keySet;
		private DateTime now;
		
		@Before
		public void setup() throws Exception {
			this.now = new DateTime(2009, 12, 27, 10, 0, 43, 0, DateTimeZone.UTC);
			setCurrentMillisFixed(now.getMillis());
			
			this.keySet = mock(KeySet.class);
			when(keySet.getUserID()).thenReturn("user");
		}
		
		@After
		public void teardown() {
			setCurrentMillisSystem();
		}
		
		@Test
		public void itStoresTheKeySetAsAnEncodedBlob() throws Exception {
			final User user = new User(keySet);
			
			assertThat(user.getId()).isEqualTo("user");
			assertThat(user.getKeySet()).isEqualTo(keySet);
		}
		
		@Test
		public void itHasAModificationTimestamp() throws Exception {
			final User user = new User(keySet);
			
			assertThat(user.getModifiedAt()).isEqualTo(now);
		}
		
		@Test
		public void itHasACreationTimestamp() throws Exception {
			final User user = new User(keySet);
			
			assertThat(user.getCreatedAt()).isEqualTo(now);
		}
		
		@Test
		public void itHasNoDocuments() throws Exception {
			final User user = new User(keySet);
			
			assertThat(user.getDocuments()).isEmpty();
		}
		
		@Test
		public void itIsLinkable() throws Exception {
			final User user = new User(keySet);
			
			assertThat(user.toString()).isEqualTo("user");
		}
		
		@Test
		public void itHasAnETag() throws Exception {
			final User user = new User(keySet);
			
			assertThat(user.getETag()).isEqualTo("user-user-0");
		}
	}
	
	public static class A_User_Loaded_From_The_Database {
		private User user;
		
		@Before
		public void setup() throws Exception {
			final ByteArrayOutputStream output;
            try (FileInputStream keyRingFile = new FileInputStream("src/test/resources/secret-keyring.gpg")) {
                output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int r = 0;
                while ((r = keyRingFile.read(buffer)) >= 0) {
                    output.write(buffer, 0, r);
                }
            }
			this.user = new User(load(output.toByteArray()));
		}
		
		@Test
		public void itHasAUserId() throws Exception {
			assertThat(user.getKeySet().getUserID()).isEqualTo("Sample Key <sample@wesabe.com>");
		}
	}
}

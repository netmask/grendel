package com.wesabe.grendel.util.tests;

import com.wesabe.grendel.util.HashCode;
import static com.wesabe.grendel.util.HashCode.calculate;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class HashCodeTest {
	public static class Calculating_A_Hash_Code {
		@Test
		public void itHandlesNullValues() throws Exception {
			assertThat(calculate(null, null)).isEqualTo(961);
			
			assertThat(calculate(null, null, null)).isEqualTo(29791);
		}
		
		@Test
		public void itHandlesArrays() throws Exception {
			assertThat(calculate(null, new int[] { 1, 2, 3 })).isEqualTo(31778);
			
			assertThat(calculate(null, new int[] { 1, 2, 4 })).isEqualTo(31779);
		}
		
		@Test
		public void itHandlesObjects() throws Exception {
			assertThat(calculate(null, new int[] { 1, 2, 3 }, "blah")).isEqualTo(4011535);
			
			assertThat(calculate(null, new int[] { 1, 2, 3 }, "blar")).isEqualTo(4011545);
		}
	}
}

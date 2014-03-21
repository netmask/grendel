package com.wesabe.grendel.util.tests;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.of;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.ImmutableSet.of;
import com.wesabe.grendel.util.Iterators;
import static com.wesabe.grendel.util.Iterators.toList;
import static com.wesabe.grendel.util.Iterators.toSet;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class IteratorsTest {
	public static class Converting_An_Iterator_Into_A_List {
		@Test
		public void itReturnsAList() throws Exception {
			final List<String> numbers = of("one", "two", "three");
			final List<String> otherNumbers = toList(numbers.iterator());
			assertThat(otherNumbers).isEqualTo(numbers);
		}
	}
	
	public static class Converting_An_Iterator_Into_A_Set {
		@Test
		public void itReturnsAList() throws Exception {
			final Set<String> numbers = of("one", "two", "three");
			final Set<String> otherNumbers = toSet(numbers.iterator());
			assertThat(otherNumbers).isEqualTo(numbers);
		}
	}
}

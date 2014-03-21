package com.wesabe.grendel.util;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.copyOf;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.ImmutableSet.copyOf;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utility methods for dealing with untyped {@link Iterator} instances.
 * 
 * @author coda
 */
public final class Iterators {
	private Iterators() {}
	
	/**
	 * Returns the items available via {@code iterator} as a {@link List}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(Iterator<?> iterator) {
		return copyOf((Iterator<T>) iterator);
	}
	
	/**
	 * Returns the items available via {@code iterator} as a {@link Set}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> toSet(Iterator<?> iterator) {
		return copyOf((Iterator<T>) iterator);
	}
}

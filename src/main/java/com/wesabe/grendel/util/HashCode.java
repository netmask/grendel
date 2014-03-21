package com.wesabe.grendel.util;

import java.util.Arrays;
import static java.util.Arrays.deepHashCode;

/**
 * A simple utility singleton for generating hash codes.
 * 
 * @author coda
 */
public class HashCode {
	private HashCode() {}
	
	public static int calculate(Object... objects) {
		return deepHashCode(objects);
	}
}

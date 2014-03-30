package com.wesabe.grendel.util;

import static java.util.Arrays.deepHashCode;

/**
 * A simple utility singleton for generating hash codes.
 *
 * @author coda
 */
public class HashCode {
    public static int calculate(Object... objects) {
        return deepHashCode(objects);
    }
}

package com.wesabe.grendel;

import com.codahale.shore.Shore;
import static com.codahale.shore.Shore.run;

/**
 * The main Grendel class.
 * 
 * @author coda
 */
public class Runner {
	public static void main(String[] args) {
		run(new Configuration(), args);
	}
}

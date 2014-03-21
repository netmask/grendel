package com.wesabe.grendel.modules;

import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import static java.security.SecureRandom.getInstance;
import static java.security.SecureRandom.getSeed;
import java.util.concurrent.Executors;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A Guice {@link Provider} which manages a {@link SecureRandom} instance.
 * 
 * @author coda
 */
public class SecureRandomProvider implements Provider<SecureRandom> {
	private static final int ENTROPY_UPDATE_SIZE = 64;
	
	/**
	 * A scheduled, asynchronous task which generates additional entropy and
	 * adds it to the PRNG's entropy pool.
	 */
	private static class UpdateTask implements Runnable {
		private final SecureRandom random;
		private final Logger logger;
		
		public UpdateTask(SecureRandom random, Logger logger) {
			this.random = random;
			this.logger = logger;
		}
		
		@Override
		public void run() {
			logger.info("Generating new PRNG seed");
			final byte[] seed = getSeed(ENTROPY_UPDATE_SIZE);
			logger.info("Updating PRNG seed");
			random.setSeed(seed);
		}
	}
	
	private static final Logger LOGGER = getLogger(SecureRandomProvider.class);
	private static final String PRNG_ALGORITHM = "SHA1PRNG";
	private static final String PRNG_PROVIDER = "SUN";
	
	private final SecureRandom random;
	private final ScheduledExecutorService pool;
	
	public SecureRandomProvider() {
		this.pool = newSingleThreadScheduledExecutor();
		
		LOGGER.info("Creating PRNG");
		try {
			this.random = getInstance(PRNG_ALGORITHM, PRNG_PROVIDER);
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
		LOGGER.info("Seeding PRNG");
		random.nextInt(); // force seeding
		
		// update the PRNG every hour, starting in an hour
		pool.scheduleAtFixedRate(new UpdateTask(random, LOGGER), 1, 1, TimeUnit.HOURS);
	}
	
	@Override
	public SecureRandom get() {
		return random;
	}
}

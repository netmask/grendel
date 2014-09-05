package com.wesabe.grendel.modules;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.security.SecureRandom.getInstance;
import static java.security.SecureRandom.getSeed;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * A Guice {@link Provider} which manages a {@link SecureRandom} instance.
 *
 * @author coda
 */
@Service
public class SecureRandomProvider implements FactoryBean<SecureRandom> {
    private static final int ENTROPY_UPDATE_SIZE = 64;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SecureRandomProvider.class);

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
        LOGGER.info("Next Seed PRNG");

        // update the PRNG every hour, starting in an hour
        pool.scheduleAtFixedRate(new UpdateTask(random, LOGGER), 1, 1, TimeUnit.HOURS);

        LOGGER.info("Scheduled Seed PRNG");
    }

    @Override
    public SecureRandom getObject() throws Exception {
        return random;
    }

    @Override
    public Class<?> getObjectType() {
        return SecureRandom.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

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
}

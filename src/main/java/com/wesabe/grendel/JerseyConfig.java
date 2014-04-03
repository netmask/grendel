package com.wesabe.grendel;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * 4/3/14 Created by Jonathan Garay
 */
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(RequestContextFilter.class);
        packages("com.wesabe.grendel.resources");
    }
}
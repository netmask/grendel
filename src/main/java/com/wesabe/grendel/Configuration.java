package com.wesabe.grendel;

import com.codahale.shore.AbstractConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Stage;
import com.wesabe.grendel.modules.SecureRandomProvider;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;

import java.security.SecureRandom;

/**
 * The Shore configuration class.
 * 
 * @author coda
 */
public class Configuration extends AbstractConfiguration {
	@Override
	protected void configure() {
		addEntityPackage("com.wesabe.grendel.entities");
		addResourcePackage("org.codehaus.jackson.jaxrs");
		addResourcePackage("com.wesabe.grendel.auth");
		addResourcePackage("com.wesabe.grendel.resources");
		addModule(new AbstractModule() {
			@Override
			protected void configure() {
				bind(SecureRandom.class).toProvider(new SecureRandomProvider());
			}
		});
		setStage(Stage.PRODUCTION);
	}
	
	@Override
	protected void configureRequestLog(RequestLog log) {
		final NCSARequestLog ncsaLog = (NCSARequestLog) log;
		ncsaLog.setExtended(false);
		ncsaLog.setLogLatency(true);
	}

	@Override
	public String getExecutableName() {
		return "grendel";
	}
}

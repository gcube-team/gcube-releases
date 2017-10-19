package org.gcube.common.authorizationservice.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationHolder {

	private static Logger logger = LoggerFactory.getLogger(ConfigurationHolder.class);
	
	private static AuthorizationConfiguration configuration;

	public static AuthorizationConfiguration getConfiguration() {
		logger.trace("getting configuration {}",configuration);
		return configuration;
	}

	public static void setConfiguration(AuthorizationConfiguration authConfiguration) {
		logger.trace("setting configuration {}",authConfiguration);
		configuration = authConfiguration;
	}
		
}

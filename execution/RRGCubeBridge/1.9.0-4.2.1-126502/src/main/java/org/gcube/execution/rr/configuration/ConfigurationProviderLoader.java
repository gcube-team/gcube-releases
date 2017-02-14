package org.gcube.execution.rr.configuration;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationProviderLoader {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ConfigurationProviderLoader.class);

	private static ConfigurationProvider configurationProvider =  null;

	public static synchronized ConfigurationProvider getProvider(){
		if (configurationProvider != null)
			return configurationProvider;
		
		ServiceLoader<ConfigurationProvider> loader = ServiceLoader.load(ConfigurationProvider.class, Thread.currentThread().getContextClassLoader());
		
		
		for (ConfigurationProvider cp : ServiceLoader.load(ConfigurationProvider.class, Thread.currentThread().getContextClassLoader())){
			logger.info( "found " + ConfigurationProvider.class.getName() + " impl " + cp.getClass());
		}
		
		for (ConfigurationProvider cp : loader){
			logger.info( "got " + ConfigurationProvider.class.getName() + " impl " + cp.getClass());
			configurationProvider = cp;
			break;
		}
		if (configurationProvider == null){
			logger.warn("No " + ConfigurationProvider.class.getName() + " no implementations found");
			throw new Error("No " + ConfigurationProvider.class.getName() + " no implementations found");
		}
		return configurationProvider;
	}
	
	public static void main(String[] args) {
		getProvider();
	}
}

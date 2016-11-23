package org.gcube.execution.rr.configuration.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.gcube.execution.rr.configuration.ConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

public class ConfigurationProviderWebServiceImpl implements ConfigurationProvider {

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigurationProviderWebServiceImpl.class);
	
	private Properties properties = null;
	
	private synchronized Properties getPropertyFile() throws Exception {
		if (properties != null)
			return properties;
		
		String filename = "deploy.properties";
		
		properties = new Properties();
		try (InputStream is = Resources.getResource(filename).openStream()) {
			properties.load(is);
		} catch (Exception e) {
			throw new Exception("could not load property file  : " + filename);
		}

		return properties;
	}

	public List<String> getGHNContextStartScopes() {

		try {
			Properties prop = getPropertyFile();

			String scopeProperty = prop.getProperty("scope").trim();
			
			List<String> scopes = Lists.newArrayList(Splitter.on(",").trimResults().omitEmptyStrings().split(scopeProperty));
			
			logger.info("scopes in property file : " + scopes);
			
			return scopes;
		} catch (Exception e) {
			logger.error("error while getting scope from property file ",e);
			return null;
		}
	}

	public List<String> getGHNContextScopes() {
		try {
			Properties prop = getPropertyFile();

			String scopeProperty = prop.getProperty("scope").trim();
			
			List<String> scopes = Lists.newArrayList(Splitter.on(",").trimResults().omitEmptyStrings().split(scopeProperty));
			
			return scopes;
		} catch (Exception e) {
			logger.error("error while getting scope from property file ",e);
			return null;
		}
	}

	public boolean isClientMode() {
		try {
			Properties prop = getPropertyFile();

			boolean isClientMode = Boolean.valueOf(prop.getProperty("clientMode", "true"));

			logger.info("isClientMode in property file (true for default) : " + isClientMode);
			
			return isClientMode;
		} catch (Exception e) {
			logger.error("error while getting clientMode from property file ",e);
			return true;
		}
	}

}

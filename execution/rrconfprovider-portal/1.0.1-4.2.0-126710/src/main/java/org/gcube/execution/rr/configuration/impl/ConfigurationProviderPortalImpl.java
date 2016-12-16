package org.gcube.execution.rr.configuration.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.gcube.execution.rr.configuration.ConfigurationProvider;

public class ConfigurationProviderPortalImpl implements ConfigurationProvider {

	private Properties properties = null;
	private synchronized Properties getPropertyFile() throws FileNotFoundException,
			IOException {
		if (properties != null)
			return properties;
		
		String filename = System.getenv("CATALINA_HOME")
				+ "/conf/infrastructure.properties";

		properties = new Properties();
		properties.load(new FileInputStream(filename));

		return properties;
	}

	public List<String> getGHNContextStartScopes() {

		try {
			Properties prop = getPropertyFile();

			String infrastructure = prop.getProperty("infrastructure").trim();
			List<String> scopes = Arrays.asList(prop.getProperty("scopes")
					.split(","));

			List<String> startScopes = new ArrayList<String>();

			//startScopes.add("/" + infrastructure.trim());

			for (String sc : scopes)
				startScopes.add("/" + infrastructure + "/" + sc.trim());

			return startScopes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> getGHNContextScopes() {
		try {
			Properties prop = getPropertyFile();

			String infrastructure = prop.getProperty("infrastructure").trim();
			List<String> scopes = Arrays.asList(prop.getProperty("scopes")
					.split(","));

			List<String> startScopes = new ArrayList<String>();

			//startScopes.add("/" + infrastructure);

			for (String sc : scopes)
				startScopes.add("/" + infrastructure + "/" + sc.trim());

			return startScopes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isClientMode() {
		try {
			Properties prop = getPropertyFile();

			boolean isClientMode = Boolean.valueOf(prop.getProperty("clientMode", "true"));

			return isClientMode;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

}

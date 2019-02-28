package org.gcube.gcat.utils;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Constants {
	
	public static final String CATALOGUE_NAME = "gCat";
	
	private static final String PROPERTY_FILENAME = "config.properties";
	
	/*
	 * Key : Context
	 * Value : Application Token
	 */
	protected static final Map<String,String> applicationTokens;
	
	
	public static String getCatalogueApplicationToken() {
		try {
			return applicationTokens.get(ContextUtility.getCurrentContext());
		}catch (Exception e) {
			throw new InternalServerErrorException("Unable to retrieve Application Token for context " + ContextUtility.getCurrentContext(), e);
		}
	}

	static {
		try {
			applicationTokens = new HashMap<>();
			Properties properties = new Properties();
			InputStream input = Constants.class.getClassLoader().getResourceAsStream(PROPERTY_FILENAME);
			// load a properties file
			properties.load(input);
			Enumeration<?> enumeration = properties.propertyNames();
			while(enumeration.hasMoreElements()) {
				String context = (String) enumeration.nextElement();
				String applicationToken = properties.getProperty(context);
				applicationTokens.put(context, applicationToken);
			}
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
}

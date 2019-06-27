package org.gcube.dataharvest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextAuthorizationTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(ContextAuthorizationTest.class);
	
	private static final String PROPERTY_FILENAME = "config.properties";
	
	private Properties properties;
	
	private void getConfigParameters() throws IOException {
		properties = new Properties();
		InputStream input = AccountingDataHarvesterPlugin.class.getClassLoader().getResourceAsStream(PROPERTY_FILENAME);
		properties.load(input);
		AccountingDataHarvesterPlugin.getProperties().set(properties);
	}
	
	@Test
	public void testRetrieveContextsAndTokens() throws Exception {
		try {
			getConfigParameters();
		}catch (Exception e) {
			logger.warn("Unable to load {} file containing configuration properties. AccountingDataHarvesterPlugin will use defaults", PROPERTY_FILENAME);
		}
		ContextAuthorization contextAuthorization = new ContextAuthorization();
		contextAuthorization.retrieveContextsAndTokens();
	}
	
}

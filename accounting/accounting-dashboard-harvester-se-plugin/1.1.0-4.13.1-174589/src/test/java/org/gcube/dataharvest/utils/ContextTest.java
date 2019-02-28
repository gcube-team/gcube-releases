/**
 * 
 */
package org.gcube.dataharvest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ContextTest.class);
	
	protected static final String PROPERTIES_FILENAME = "token.properties";
	
	private static final String GCUBE_DEVNEXT_VARNAME = "GCUBE_DEVNEXT";
	public static final String GCUBE_DEVNEXT;
	
	private static final String GCUBE_DEVNEXT_NEXTNEXT_VARNAME = "GCUBE_DEVNEXT_NEXTNEXT";
	public static final String GCUBE_DEVNEXT_NEXTNEXT;
	
	public static final String GCUBE_VARNAME = "GCUBE";
	public static final String GCUBE;
	
	public static final String DEFAULT_TEST_SCOPE;
	
	public static final String ROOT_VARNAME = "ROOT_ERIC";
	public static final String ROOT;
	
	public static final String TAGME_VARNAME = "TAGME_ERIC";
	public static final String TAGME;
	
	public static final String StockAssessment_VARNAME = "StockAssessment";
	public static final String StockAssessment;

	public static final String RESOURCE_CATALOGUE_VARNAME = "RESOURCE_CATALOGUE";
	public static final String RESOURCE_CATALOGUE;
	
	
	
	static {
		
		logger.trace("Retrieving Tokens from {}", PROPERTIES_FILENAME);
		
		Properties properties = new Properties();
		InputStream input = ContextTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME);
		
		try {
			// load the properties file
			properties.load(input);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		GCUBE = properties.getProperty(GCUBE_VARNAME);
		
		GCUBE_DEVNEXT = properties.getProperty(GCUBE_DEVNEXT_VARNAME);
		GCUBE_DEVNEXT_NEXTNEXT = properties.getProperty(GCUBE_DEVNEXT_NEXTNEXT_VARNAME);
		
		ROOT = properties.getProperty(ROOT_VARNAME);
		
		TAGME = properties.getProperty(TAGME_VARNAME);
		
		RESOURCE_CATALOGUE = properties.getProperty(RESOURCE_CATALOGUE_VARNAME);
		
		StockAssessment = properties.getProperty(StockAssessment_VARNAME);
		
		DEFAULT_TEST_SCOPE = GCUBE;
	}
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		Utils.setContext(DEFAULT_TEST_SCOPE);
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
	}
	
}

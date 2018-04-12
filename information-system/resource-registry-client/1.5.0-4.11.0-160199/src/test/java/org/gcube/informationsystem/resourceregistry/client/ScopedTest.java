/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ScopedTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ScopedTest.class);
	
	protected static final String PROPERTIES_FILENAME = "token.properties"; 
	
	private static final String GCUBE_DEVNEXT_VARNAME = "GCUBE_DEVNEXT";
	public static final String GCUBE_DEVNEXT;
	
	private static final String GCUBE_DEVNEXT_NEXTNEXT_VARNAME = "GCUBE_DEVNEXT_NEXTNEXT";
	public static final String GCUBE_DEVNEXT_NEXTNEXT;
	
	public static final String GCUBE_DEVSEC_VARNAME = "GCUBE_DEVSEC";
	public static final String GCUBE_DEVSEC;
	
	public static final String GCUBE_DEVSEC_DEVVRE_VARNAME = "GCUBE_DEVSEC_DEVVRE";
	public static final String GCUBE_DEVSEC_DEVVRE;
	
	public static final String GCUBE_VARNAME = "GCUBE";
	public static final String GCUBE;
	
	
	public static final String DEFAULT_TEST_SCOPE;
	public static final String ALTERNATIVE_TEST_SCOPE;
	
	
	protected static final String REGISTRY_PROPERTIES_FILENAME = "registry.properties"; 
	public static final String RESOURCE_REGISTRY_URL_PROPERTY = "RESOURCE_REGISTRY_URL";
	public static final String RESOURCE_REGISTRY_URL;
	
	static {
		Properties properties = new Properties();
		InputStream input = ScopedTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME);

		try {
			// load the properties file
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			
		GCUBE_DEVNEXT = properties.getProperty(GCUBE_DEVNEXT_VARNAME);
		GCUBE_DEVNEXT_NEXTNEXT = properties.getProperty(GCUBE_DEVNEXT_NEXTNEXT_VARNAME);
		
		GCUBE_DEVSEC = properties.getProperty(GCUBE_DEVSEC_VARNAME);
		GCUBE_DEVSEC_DEVVRE = properties.getProperty(GCUBE_DEVSEC_DEVVRE_VARNAME);
		
		GCUBE = properties.getProperty(GCUBE_VARNAME);
		
		DEFAULT_TEST_SCOPE = GCUBE_DEVNEXT_NEXTNEXT;
		ALTERNATIVE_TEST_SCOPE = GCUBE_DEVSEC_DEVVRE;
		
		
		properties = new Properties();
		input = ScopedTest.class.getClassLoader().getResourceAsStream(REGISTRY_PROPERTIES_FILENAME);
		try {
			// load the properties file
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		RESOURCE_REGISTRY_URL = properties.getProperty(RESOURCE_REGISTRY_URL_PROPERTY);
		
		if(RESOURCE_REGISTRY_URL!=null){
			ResourceRegistryClientFactory.forceToURL(RESOURCE_REGISTRY_URL);
		}
		
	}
	
	public static String getCurrentScope(String token) throws ObjectNotFound, Exception{
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String context = authorizationEntry.getContext();
		logger.info("Context of token {} is {}", token, context);
		return context;
	}
	
	
	public static void setContext(String token) throws ObjectNotFound, Exception{
		SecurityTokenProvider.instance.set(token);
		ScopeProvider.instance.set(getCurrentScope(token));
	}
	
	@BeforeClass
	public static void beforeClass() throws Exception{
		setContext(DEFAULT_TEST_SCOPE);
	}
	
	@AfterClass
	public static void afterClass() throws Exception{
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
	}
	
}

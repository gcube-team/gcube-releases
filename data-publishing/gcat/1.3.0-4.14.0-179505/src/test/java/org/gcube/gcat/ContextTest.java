/**
 * 
 */
package org.gcube.gcat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ContextTest.class);
	
	protected static Properties properties;
	protected static final String PROPERTIES_FILENAME = "token.properties";
	
	public static final String DEFAULT_TEST_SCOPE_NAME;
	
	static {
		properties = new Properties();
		InputStream input = ContextTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME);
		
		try {
			// load the properties file
			properties.load(input);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		//DEFAULT_TEST_SCOPE_NAME = "/pred4s/preprod/preVRE";
		DEFAULT_TEST_SCOPE_NAME = "/gcube/devNext/NextNext";
	}
	
	public static String getCurrentScope(String token) throws ObjectNotFound, Exception {
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String context = authorizationEntry.getContext();
		logger.info("Context of token {} is {}", token, context);
		return context;
	}
	
	public static void setContextByName(String fullContextName) throws ObjectNotFound, Exception {
		String token = ContextTest.properties.getProperty(fullContextName);
		setContext(token);
	}
	
	public static void setContext(String token) throws ObjectNotFound, Exception {
		SecurityTokenProvider.instance.set(token);
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		ClientInfo clientInfo = authorizationEntry.getClientInfo();
		logger.debug("User : {} - Type : {}", clientInfo.getId(), clientInfo.getType().name());
		String qualifier = authorizationEntry.getQualifier();
		Caller caller = new Caller(clientInfo, qualifier);
		AuthorizationProvider.instance.set(caller);
		ScopeProvider.instance.set(getCurrentScope(token));
	}
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		setContextByName(DEFAULT_TEST_SCOPE_NAME);
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
	}
	
}

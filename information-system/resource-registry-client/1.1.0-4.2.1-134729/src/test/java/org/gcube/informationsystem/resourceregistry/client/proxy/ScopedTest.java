/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client.proxy;

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
	
	protected static final String PROPERTIES_FILENAME = "config.properties"; 
	
	private static final String TOKEN_VARNAME = "TOKEN";
	private static final String TOKEN;
	
	static {
		Properties properties = new Properties();
		InputStream input = ScopedTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME);

		try {
			// load the properties file
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			
		TOKEN = properties.getProperty(TOKEN_VARNAME);
	}
	
	private static String getCurrentScope() throws ObjectNotFound, Exception{
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(TOKEN);
		String context = authorizationEntry.getContext();
		logger.info("Context of token {} is {}", TOKEN, context);
		return context;
	}
	
	@BeforeClass
	public static void beforeClass() throws Exception{
		SecurityTokenProvider.instance.set(TOKEN);
		ScopeProvider.instance.set(getCurrentScope());
	}
	
	@AfterClass
	public static void afterClass() throws Exception{
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
	}
	
}

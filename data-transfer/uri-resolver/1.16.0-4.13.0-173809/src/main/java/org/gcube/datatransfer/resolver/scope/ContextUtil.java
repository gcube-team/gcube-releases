/**
 *
 */
package org.gcube.datatransfer.resolver.scope;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class ScopeUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 22, 2017
 */
public class ContextUtil {

	public static final String ENV_SCOPE = "SCOPE"; //Environment Variable

	public static final String STORAGE_HUB_APP_TOKEN = "STORAGE_HUB_APP_TOKEN"; //Environment Variable

	private static final Logger logger = LoggerFactory.getLogger(ContextUtil.class);


	/**
	 * Gets the scope from environment.
	 *
	 * @return the scope from environment
	 * @throws ServletException the servlet exception
	 */
	public static String getScopeFromEnvironment() throws ServletException{

		logger.info("Reading Environment Variable "+ENV_SCOPE);
		String scopeFromEnv = System.getenv(ENV_SCOPE);

		if(scopeFromEnv == null || scopeFromEnv.isEmpty())
			throw new ServletException(ContextUtil.class.getName() +" cannot read scope from Environment Variable: "+ENV_SCOPE+", It is null or empty");

		logger.info("Read scope: "+scopeFromEnv+" from Environment Variable: "+ENV_SCOPE);
		return scopeFromEnv;
	}



	/**
	 * Gets the app token storag hub environment.
	 *
	 * @return the app token storag hub environment
	 * @throws ServletException the servlet exception
	 */
	public static String getAppTokenStoragHubEnvironment() throws ServletException{

		logger.info("Reading Environment Variable "+STORAGE_HUB_APP_TOKEN);
		String appTokenFromEnv = System.getenv(STORAGE_HUB_APP_TOKEN);

		if(appTokenFromEnv == null || appTokenFromEnv.isEmpty())
			throw new ServletException(ContextUtil.class.getName() +" cannot read the Application Token for StorageHub from Environment Variable: "+STORAGE_HUB_APP_TOKEN+", It is null or empty");

		logger.info("Read App Token: "+appTokenFromEnv.substring(0,appTokenFromEnv.length()-6)+"XXXXXX from Environment Variable: "+STORAGE_HUB_APP_TOKEN);
		return appTokenFromEnv;
	}
}

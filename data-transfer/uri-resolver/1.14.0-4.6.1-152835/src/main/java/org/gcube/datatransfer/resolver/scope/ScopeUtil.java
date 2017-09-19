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
public class ScopeUtil {

	public static final String ENV_SCOPE = "SCOPE"; //Environment Variable

	private static final Logger logger = LoggerFactory.getLogger(ScopeUtil.class);


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
			throw new ServletException(ScopeUtil.class.getName() +" cannot read scope from Environment Variable: "+ENV_SCOPE+", It is null or empty");

		logger.info("Read scope: "+scopeFromEnv+" from Environment Variable: "+ENV_SCOPE);
		return scopeFromEnv;
	}
}

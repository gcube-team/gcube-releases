/**
 *
 */
package org.gcube.datatransfer.resolver.applicationprofile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class ScopeUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 16, 2017
 */
public class ScopeUtil {

	private static final String SCOPE_SEPARATOR = "/";
	public static final Logger logger = LoggerFactory.getLogger(ScopeUtil.class);


	/**
	 * Gets the infrastructure name from scope.
	 *
	 * @param scope the scope
	 * @return the infrastructure name from scope
	 * @throws Exception the exception
	 */
	public static String getInfrastructureNameFromScope(String scope) throws Exception{

		if(scope==null || scope.isEmpty()){
			throw new Exception("Scope is null or empty");
		}

		if(!scope.startsWith(SCOPE_SEPARATOR)){
			logger.warn("Input scope: "+scope+" not have / is a really scope?");
			scope = SCOPE_SEPARATOR+scope;
			logger.warn("Tentative as scope: "+scope);
		}

		String[] splitScope = scope.split(SCOPE_SEPARATOR);

		String rootScope = SCOPE_SEPARATOR + splitScope[1];

		if(rootScope.length()<2){
			throw new Exception("Infrastructure name not found in "+scope);
		}

		return rootScope;

	}
}

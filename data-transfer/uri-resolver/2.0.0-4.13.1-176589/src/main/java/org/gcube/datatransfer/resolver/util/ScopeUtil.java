/**
 *
 */
package org.gcube.datatransfer.resolver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class ScopeUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 5, 2018
 */
public class ScopeUtil {

	public static final String SCOPE_SEPARATOR = "/";
	public static final Logger logger = LoggerFactory.getLogger(ScopeUtil.class);

	/**
	 * Normalize scope.
	 * Add the '/' as prefix and remove all input replaceSepartor
	 * @param scope the scope
	 * @param replaceSepartor the string to replace with {@link ScopeUtil#SCOPE_SEPARATOR}}
	 * @return the normalized scope
	 */
	public static String normalizeScope(String scope, String replaceSepartor){
		if(!scope.startsWith("/"))
			scope="/"+scope;
		scope = scope.replaceAll("\\"+replaceSepartor, SCOPE_SEPARATOR);
		return scope;
	}

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

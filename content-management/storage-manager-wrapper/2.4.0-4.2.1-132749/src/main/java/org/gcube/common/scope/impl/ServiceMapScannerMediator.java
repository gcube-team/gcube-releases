package org.gcube.common.scope.impl;

import java.util.Set;

/**
 * Check the validity of a given scope
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class ServiceMapScannerMediator {
	
	/**
	 * The validation has been removed
	 * @param scope
	 * @return
	 */
	@Deprecated
	public static boolean isValid(String scope){
		return true;
	}
	
	
	public static Set<String> getScopeKeySet(){
		return ServiceMapScanner.maps().keySet();
	}

}

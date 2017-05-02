package org.gcube.common.scope.impl;

import java.util.Set;

import org.gcube.common.scope.api.ScopeProvider;

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
//		log.debug("validating scope "+scope);
		String currentScope=ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		try{
			new ScopedServiceMap().scope();

		}catch(Exception e){
			return false;
		}finally{
			ScopeProvider.instance.set(currentScope);
		}
		return true;
	}
	
	
	public static Set<String> getScopeKeySet(){
		return ServiceMapScanner.maps().keySet();
	}

}

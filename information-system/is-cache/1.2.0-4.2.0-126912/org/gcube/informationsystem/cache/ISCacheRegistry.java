/**
 * 
 */
package org.gcube.informationsystem.cache;

import org.gcube.common.core.scope.GCUBEScope;

/**
 * @author paul
 *
 */
public class ISCacheRegistry implements ISCacheRegistryMBean {

	
	/**
	 * Add new Cache manager for the given scope
	 * @param scope scope
	 * @return true if the manager does not already exist
	 * @throws Exception in case of error
	 * @see org.gcube.informationsystem.cache.ISCacheRegistryMBean#addManager(org.gcube.common.core.scope.GCUBEScope)
	 */
	public boolean addManager(String scope) throws Exception {
		return this.addManager(GCUBEScope.getScope(scope));
	}
	private boolean addManager(GCUBEScope scope) throws Exception {
		return ISCacheManager.addManager(scope);
	}

	/**
	 * Get {@link ISCacheManager} instance for the given scope
	 * @param scope scope
	 * @return {@link ISCacheManager} instance for the given scope
	 * @throws Exception in case of error
	 * @see org.gcube.informationsystem.cache.ISCacheRegistryMBean#delManager(org.gcube.common.core.scope.GCUBEScope)
	 */
	public ISCacheManager getManager(String scope) throws Exception {
		return this.getManager(GCUBEScope.getScope(scope));
	}
	private ISCacheManager getManager(GCUBEScope scope) throws Exception {
		return ISCacheManager.managers.get(scope.toString());
	}

	/**
	 * Delete the Cache manager for the given scope
	 * @param scope scope
	 * @return true if the manager existed
	 * @throws Exception in case of error
	 * 
	 */
	public boolean delManager(String scope) throws Exception {
		return this.delManager(GCUBEScope.getScope(scope));
	}
	/**
	 * Delete the Cache manager for the given scope
	 * @param scope scope
	 * @return true if the manager existed
	 * @throws Exception in case of error
	 *
	 */
	public boolean delManager(GCUBEScope scope) throws Exception {
		return ISCacheManager.delManager(scope);
	}

}

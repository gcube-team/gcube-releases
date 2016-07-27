/**
 * 
 */
package org.gcube.informationsystem.cache;

/**
 * @author paul
 *
 */
public interface ISCacheRegistryMBean {

	/**
	 * Add new Cache manager for the given scope
	 * @param scope scope
	 * @return true if the manager does not already exist
	 * @throws Exception in case of error
	 */
	public boolean addManager(String scope) throws Exception;
	/**
	 * Delete the Cache manager for the given scope
	 * @param scope scope
	 * @return true if the manager existed
	 * @throws Exception in case of error
	 */
	public boolean delManager(String scope) throws Exception;

}

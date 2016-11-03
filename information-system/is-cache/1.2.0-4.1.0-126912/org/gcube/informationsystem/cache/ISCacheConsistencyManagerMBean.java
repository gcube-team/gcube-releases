/**
 * 
 */
package org.gcube.informationsystem.cache;

/**
 * The MBean manages the available cache consistency components (cc). It exposes
 * the standard add/remove/enumerate/activate operations over the cc components.
 * 
 * @author UoA
 * 
 */
public interface ISCacheConsistencyManagerMBean {

	/**
	 * Get all registered Cache Consistency Managers (CCMs).
	 * @return all registered Cache Consistency Managers (CCMs)
	 */
	public String[] getCCManagers();

	/**
	 * Add new Cache Consistency Manager (CCM).
	 * @param CCManagerFQName CCM fully qualified (FQ) class name 
	 * @return true if this set did not already contain the specified element; false otherwise
	 * @throws Exception in case of error; most probably due to non-accessibility to the specified class
	 */
	public boolean addCCManager(String CCManagerFQName) throws Exception;

	/**
	 * Add new Cache Consistency Manager (CCM).
	 * @param CCManagerFQName CCM fully qualified (FQ) class name
	 * @param codebase URL of the jar file that contains the specified class  
	 * @return true if this set did not already contain the specified element; false otherwise
	 * @throws Exception in case of error; most probably due to non-accessibility to the specified class
	 */
	public boolean addCCManagerDynamic(String CCManagerFQName, String codebase)
			throws Exception;

	/**
	 * Delete Cache Consistency Manager (CCM).
	 * @param CCManagerFQName CCM fully qualified (FQ) class name 
	 * @return true if this set contains the specified element; false otherwise
	 * @throws Exception in case of error
	 */
	public boolean delCCManager(String CCManagerFQName) throws Exception;

	/**
	 * Activate the specified CCM.
	 * @param CCManagerFQName CCM fully qualified (FQ) class name
	 * @throws Exception in case of the CCM not being added.
	 */
	public void setActiveCCManager(String CCManagerFQName) throws Exception;

	/**
	 * Get the active CCM.
	 * @return the active CCM
	 * @throws Exception in case of error
	 */
	public String getActiveCCManager() throws Exception;

}

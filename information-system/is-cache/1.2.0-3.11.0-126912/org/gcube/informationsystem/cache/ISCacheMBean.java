/**
 * 
 */
package org.gcube.informationsystem.cache;

import org.apache.axis.message.addressing.EndpointReference;

/**
 * Cache object. It provides cached information regarding the running instances
 * of registered services. It exposes add/delete/enumerate operations on
 * services as well as retrieval of corresponding running instances (URLs).
 * 
 * @author UoA
 * @version 0.9
 */

public interface ISCacheMBean {

	/**
	 * Get registered services
	 * 
	 * @return the registered services
	 */
	public String[] getSrvsStr();

	/**
	 * Get number of registered services
	 * 
	 * @return the number of registered services
	 */
	public int getSrvNumber();

	/**
	 * Get the running instances (array of URLs) of the specified service
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @return the running instances (array of URLs) of the specified service
	 * @throws Exception
	 *             in case of error; most probably due to the fact that the
	 *             specified service is not registered.
	 */
	public EndpointReference[] getEPRsFor(String srvClass, String srvName)
			throws Exception;

	/**
	 * Get the running instances (array of URLs) of the specified service
	 * 
	 * @param srv
	 *            service
	 * @return the running instances (array of URLs) of the specified service
	 * @throws Exception
	 *             in case of error; most probably due to the fact that the
	 *             specified service is not registered.
	 */
	public EndpointReference[] getEPRsFor(Srv srv) throws Exception;

	/**
	 * 
	 * @param srv
	 *            service
	 * @param srvType
	 *            service type
	 * @return the running instances (array of EPRs) of the specified service
	 * @throws Exception in case of error
	 */
	public EndpointReference[] getEPRsFor(Srv srv, String srvType) throws Exception;

	/**
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param srvType
	 *            service type
	 * @return the running instances (array of EPRs) of the specified service
	 * @throws Exception in case of error
	 */
	public EndpointReference[] getEPRsFor(String srvClass, String srvName, String srvType)
			throws Exception;

	/**
	 * Add new filtering criterion on a specific service on a given type. This
	 * filtering criterion applies only for the given service type.
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param srvType
	 *            service type
	 * @param critVar
	 *            criterion r-value
	 * @param critVal
	 *            criterion l-value
	 * @return true if the critVar has not been added before; false otherwise
	 * @throws Exception
	 *             in case of error
	 */
	public boolean addFilterCriterion(String srvClass, String srvName,
			String srvType, String critVar, String critVal) throws Exception;

	/**
	 * Add new filtering criterion on a specific service on a given type. This
	 * filtering criterion applies only for the given service type.
	 * 
	 * @param srv
	 *            service instance
	 * @param srvType
	 *            service type
	 * @param critVar
	 *            criterion r-value
	 * @param critVal
	 *            criterion l-value
	 * @return true if the critVar has not been added before; false otherwise
	 * @throws Exception
	 *             in case of error
	 */
	public boolean addFilterCriterion(Srv srv, String srvType, String critVar,
			String critVal) throws Exception;

	/**
	 * Delete filtering criterion from a specific service of a given type.
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param srvType
	 *            service type
	 * @param critVar
	 *            criterion r-value
	 * @return true if the critVar has been added; false otherwise
	 * @throws Exception
	 *             in case of error
	 */
	public boolean delFilterCriterion(String srvClass, String srvName,
			String srvType, String critVar) throws Exception;

	/**
	 * Delete filtering criterion from a specific service of a given type.
	 * 
	 * @param srv
	 *            service instance
	 * @param srvType
	 *            service type
	 * @param critVar
	 *            criterion r-value
	 * @return true if the critVar has been added; false otherwise
	 * @throws Exception
	 *             in case of error
	 */
	public boolean delFilterCriterion(Srv srv, String srvType, String critVar)
			throws Exception;

	/**
	 * Delete all filtering criteria from a specific service of a given type.
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param srvType
	 *            service type
	 * @return true if the given service type already had some criteria; false
	 *         otherwise
	 * @throws Exception
	 *             in case of error
	 */
	public boolean delAllFilterCriterion(String srvClass, String srvName,
			String srvType) throws Exception;

	/**
	 * Delete all filtering criteria from a specific service of a given type.
	 * 
	 * @param srv
	 *            service instance
	 * @param srvType
	 *            service type
	 * @return true if the given service type already had some criteria; false
	 *         otherwise
	 * @throws Exception
	 *             in case of error
	 */
	public boolean delAllFilterCriterion(Srv srv, String srvType)
			throws Exception;

	/**
	 * Get all filtering criteria from a specific service of a given type.
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param srvType
	 *            service type
	 * @return list of all criteria; key: criterion variable, value: criterion
	 *         value
	 * @throws Exception
	 *             in case of error
	 */
	public String[] getAllFilterCriteria(String srvClass, String srvName,
			String srvType) throws Exception;

	/**
	 * Get all filtering criteria from a specific service of a given type.
	 * 
	 * @param srv
	 *            service instance
	 * @param srvType
	 *            service type
	 * @return list of all criteria; key: criterion variable, value: criterion
	 *         value
	 * @throws Exception
	 *             in case of error
	 */
	public String[] getAllFilterCriteria(Srv srv, String srvType)
			throws Exception;
}

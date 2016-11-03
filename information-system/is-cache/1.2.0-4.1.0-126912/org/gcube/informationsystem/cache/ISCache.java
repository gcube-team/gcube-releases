/**
 * 
 */
package org.gcube.informationsystem.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.axis.message.addressing.EndpointReference;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Implementation of the {@link ISCacheMBean} interface.
 * 
 * @author UoA
 * @version 0.9
 * 
 */
public class ISCache implements ISCacheMBean {

	private SrvRegistry registry = null;
	private GCUBELog log = new GCUBELog(ISCache.class);

	/**
	 * Constructor
	 * @param scope scope
	 */
	public ISCache(GCUBEScope scope) {
		this.registry = new SrvRegistry(scope);
	}

	/**
	 * Get registered services
	 * @return array of {@link Srv} instances
	 */
	public Srv[] getSrvs() {
		return this.getRegistry().getSrvs().toArray(new Srv[0]);
	}

	/**
	 * Get the running instances (array of URLs) of the specified service
	 * 
	 * @param srv
	 *            service
	 * @return the running instances (array of URLs) of the specified service
	 * @throws Exception
	 *             in case of error; most probably due to the fact that the
	 *             specified service is not registered.
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getEPRsFor(Srv)
	 */
	public EndpointReference[] getEPRsFor(Srv srv) throws Exception {
		return this.getRegistry().getEPRsFor(srv);
	}

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
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getEPRsFor(String, String)
	 */
	public EndpointReference[] getEPRsFor(String srvClass, String srvName)
			throws Exception {
		return this.getEPRsFor(new Srv(srvClass, srvName));
		
	}

	/**
	 * Get the running instances (array of URLs) of the specified service
	 * 
	 * @param srv
	 *            service
	 * @param srvType
	 * 			  service type
	 * @return the running instances (array of URLs) of the specified service
	 * @throws Exception
	 *             in case of error; most probably due to the fact that the
	 *             specified service is not registered.
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getEPRsFor(Srv, String)
	 */
	public EndpointReference[] getEPRsFor(Srv srv, String srvType) throws Exception {
		return this.getRegistry().getEPRsFor(srv, srvType);
	}

	/**
	 * Get the running instances (array of URLs) of the specified service
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param srvType
	 * 			  service type
	 * @return the running instances (array of URLs) of the specified service
	 * @throws Exception
	 *             in case of error; most probably due to the fact that the
	 *             specified service is not registered.
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getEPRsFor(String, String,
	 *      String)
	 */
	public EndpointReference[] getEPRsFor(String srvClass, String srvName, String srvType)
			throws Exception {
		return this.getRegistry().getEPRsFor(srvClass, srvName, srvType);
	}

	/**
	 * Get number of registered services
	 * 
	 * @return the number of registered services
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getSrvNumber()
	 */
	public int getSrvNumber() {
		return this.getRegistry().getSrvs().size();
	}

	/**
	 * Get registered services
	 * 
	 * @return the registered services
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getSrvsStr()
	 */
	public String[] getSrvsStr() {
		Set<String> vec = new HashSet<String>();
		Iterator<Srv> s = this.getRegistry().getSrvs().iterator();
		while (s.hasNext())
			vec.add(s.next().toString());
		return vec.toArray(new String[0]);
	}

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
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#addFilterCriterion(String,
	 *      String, String, String, String)
	 */
	public boolean addFilterCriterion(String srvClass, String srvName,
			String srvType, String critVar, String critVal) throws Exception {
		return addFilterCriterion(new Srv(srvClass, srvName), srvType, critVar,
				critVal);
	}

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
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#addFilterCriterion(Srv,
	 *      String, String, String)
	 */
	public boolean addFilterCriterion(Srv srv, String srvType, String critVar,
			String critVal) throws Exception {
		if (Srv.isServiceTypeValid(srvType) == false)
			throw new Exception("Service Type '" + srvType + "'"
					+ " is not valid.");
		Srv actualSrv = this.getRegistry().getService(srv);
		if(actualSrv == null) {
			this.getRegistry().addService(srv);
			actualSrv = this.getRegistry().getService(srv);
			if(actualSrv == null)
				throw new Exception("Got null service, even after I explicitly added!");
		}
		HashMap<String, String> h = actualSrv.getFilterCriteria().get(srvType);
		if (h == null)
			h = new HashMap<String, String>();
		boolean ret = false;
		if (h.put(critVar, critVal) == null)
			ret = true;
		actualSrv.getFilterCriteria().put(srvType, h);
		log.debug("Forcing service update");
		actualSrv.forceRefreshService();
		//this.getRegistry().
		log.debug("Service update finished");
		return ret;
	}

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
	 *             in
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#delAllFilterCriterion(String,
	 *      String, String)
	 */
	public boolean delAllFilterCriterion(String srvClass, String srvName,
			String srvType) throws Exception {
		return delAllFilterCriterion(new Srv(srvClass, srvName), srvType);
	}

	/**
	 * Delete all filtering criteria from a specific service of a given type.
	 * 
	 * @param srv
	 *            service
	 * @param srvType
	 *            service type
	 * @return true if the given service type already had some criteria; false
	 *         otherwise
	 * @throws Exception
	 *             in case of error
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#delAllFilterCriterion(Srv,
	 *      String)
	 */
	public boolean delAllFilterCriterion(Srv srv, String srvType)
			throws Exception {
		if (Srv.isServiceTypeValid(srvType) == false)
			throw new Exception("Service Type '" + srvType + "'"
					+ " is not valid.");
		if (this.getRegistry().getService(srv).getFilterCriteria().remove(srvType) == null)
			return false;
		return true;
	}

	/**
	 * Delete all filtering criteria from a specific service of a given type.
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param srvType
	 *            service type
	 * @param critVar
	 *            criterion r-value
	 * @return true if the given service type already had some criteria; false
	 *         otherwise
	 * @throws Exception
	 *             in case of error
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#delFilterCriterion(String,
	 *      String, String, String)
	 */
	public boolean delFilterCriterion(String srvClass, String srvName,
			String srvType, String critVar) throws Exception {
		return delFilterCriterion(new Srv(srvClass, srvName), srvType, critVar);
	}

	/**
	 * Delete all filtering criteria from a specific service of a given type.
	 * 
	 * @param srv
	 *            service
	 * @param srvType
	 *            service type
	 * @param critVar
	 *            criterion r-value
	 * @return true if the given service type already had some criteria; false
	 *         otherwise
	 * @throws Exception
	 *             in case of error
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#delFilterCriterion(Srv,
	 *      String, String)
	 */
	public boolean delFilterCriterion(Srv srv, String srvType, String critVar)
			throws Exception {
		if (Srv.isServiceTypeValid(srvType) == false)
			throw new Exception("Service Type '" + srvType + "'"
					+ " is not valid.");
		HashMap<String, String> h = this.getRegistry().getService(srv)
				.getFilterCriteria().get(srvType);
		if (h == null || h.size() == 0)
			return false;
		if (h.remove(critVar) == null)
			return false;
		return true;
	}

	/**
	 * Get all filtering criteria from a specific service of a given type.
	 * 
	 * @param srvClass
	 *            service instance
	 * @param srvName
	 *            service name
	 * @param srvType
	 *            service type
	 * @return list of all criteria; key: criterion variable, value: criterion
	 *         value
	 * @throws Exception
	 *             in case of error
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getAllFilterCriteria(String,
	 *      String, String)
	 */
	public String[] getAllFilterCriteria(String srvClass, String srvName,
			String srvType) throws Exception {
		return getAllFilterCriteria(new Srv(srvClass, srvName), srvType);
	}

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
	 * @see org.gcube.informationsystem.cache.ISCacheMBean#getAllFilterCriteria(Srv,
	 *      String)
	 */
	public String[] getAllFilterCriteria(Srv srv, String srvType)
			throws Exception {
		if (Srv.isServiceTypeValid(srvType) == false)
			throw new Exception("Service Type '" + srvType + "'"
					+ " is not valid.");
		HashMap<String, String> h = this.getRegistry().getService(srv)
				.getFilterCriteria().get(srvType);
		if (h == null)
			return new String[0];

		Iterator<Entry<String, String>> it = h.entrySet().iterator();
		Vector<String> vec = new Vector<String>();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String var = entry.getKey();
			String val = entry.getValue();
			vec.add(var + ":" + val);
		}

		return vec.toArray(new String[0]);
	}

	/**
	 * @param registry the registry to set
	 */
	public void setRegistry(SrvRegistry registry) {
		this.registry = registry;
	}

	/**
	 * @return the registry
	 */
	public SrvRegistry getRegistry() {
		return registry;
	}

}

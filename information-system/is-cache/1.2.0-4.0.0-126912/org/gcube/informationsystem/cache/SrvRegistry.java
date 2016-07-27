/**
 * 
 */
package org.gcube.informationsystem.cache;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReference;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.cache.consistency.manager.ConsistencyManagerIF;

/**
 * @author paul
 *
 */
public class SrvRegistry {

	private ConsistencyManagerIF cmi = null;
	
	private static GCUBELog log = new GCUBELog(SrvRegistry.class);
	private GCUBEScope scope = null;
	
	/**
	 * Constructor
	 * @param scope scope
	 */
	public SrvRegistry(GCUBEScope scope) {
		this.srvs = new Hashtable<Srv, Srv>();
		this.setScope(scope);
	}
	
	private Hashtable<Srv, Srv> srvs = new Hashtable<Srv, Srv>();

	/**
	 * Get all service information for the given service
	 * @param srv service
	 * @return all service information for the given service
	 */
	public Srv getService(Srv srv) {
		return this.srvs.get(srv);
	}
	
	/**
	 * Get all service information for the given service
	 * @param srvClass service class
	 * @param srvName service name
	 * @return all service information for the given service
	 */
	public Srv getService(String srvClass, String srvName) {
		return this.getService(new Srv(srvClass, srvName));
	}

	/**
	 * Add the given service
	 * @param srv service to be added
	 * @return true if the service did not already exist
	 */
	public boolean addService(Srv srv) {
		srv.setScope(this.getScope());
		srv.setMyManager(this.cmi);
		if(this.srvs.put(srv, srv) == null)
			return true;
		return false;
	}

	/**
	 * Add the given service
	 * @param srvClass class of the service to be added
	 * @param srvName name of the service to be added
	 * @return true if the service did not already exist
	 */
	public boolean addService(String srvClass, String srvName) {
		return this.addService(new Srv(srvClass, srvName));
	}
	
	/**
	 * Delete given service
	 * @param srv service
	 * @return true if the given service was successfully deleted (a.k.a. it existed before)
	 */
	public boolean delService(Srv srv) {
		if(this.srvs.remove(srv) == null)
			return false;
		return true;
	}
	
	/**
	 * Delete given service
	 * @param srvClass service class
	 * @param srvName service name
	 * @return true if the given service was successfully deleted (a.k.a. it existed before)
	 */
	public boolean delService(String srvClass, String srvName) {
		return this.delService(new Srv(srvClass, srvName));
	}
	
	/**
	 * Get services
	 * @return set of {@link Srv} instances
	 */
	public Set<Srv> getSrvs() {
		Iterator<Srv> it = this.srvs.keySet().iterator();
		Set<Srv> srvs = new HashSet<Srv>();
		log.debug("Adding " + this.srvs.keySet().size() + " services");
		while(it.hasNext()) {
			Srv srv = it.next();
			log.debug("Adding '" + srv.toString() + "'");
			srvs.add(srv);
		}
		log.debug("Returing registered services");
		return srvs;
	}
	
	/**
	 * Get EPRs for the given service
	 * @param srv service
	 * @return EPRs for the given service
	 * @throws Exception in case of error
	 */
	public EndpointReference[] getEPRsFor(Srv srv) throws Exception {
		if(this.srvs.get(srv) == null) {
			this.addService(srv);
		}
		Set<EndpointReference> col = this.srvs.get(srv).getEPRs();
		log.debug("Number of registered services: " + this.srvs.size());
		log.debug("Number of registered services 2: " + this.srvs.keySet().size());
		return col.toArray(new EndpointReference[0]);
	}
	
	/**
	 * Get EPRs for the given service
	 * @param srvClass service class
	 * @param srvName service name
	 * @return EPRs for the given service
	 * @throws Exception in case of error
	 */
	public EndpointReference[] getEPRsFor(String srvClass, String srvName)
			throws Exception {
		return this.getEPRsFor(new Srv(srvClass, srvName));
	}

	/**
	 * Get EPRs for the given service
	 * @param srv service
	 * @param srvType service type
	 * @return EPRs for the given service
	 * @throws Exception in case of error
	 */
	public EndpointReference[] getEPRsFor(Srv srv, String srvType) throws Exception {
		if(this.srvs.get(srv) == null) {
			this.addService(srv);
		}
		Set<EndpointReference> col = this.srvs.get(srv).getEPRs(srvType);
		if(col == null)
			throw new Exception("Could not find service type: " + srvType);
		return col.toArray(new EndpointReference[0]);
	}
	
	/**
	 * Get EPRs for the given service
	 * @param srvClass service class
	 * @param srvName service name
	 * @param srvType service type
	 * @return EPRs for the given service
	 * @throws Exception in case of error
	 */
	public EndpointReference[] getEPRsFor(String srvClass, String srvName, String srvType) throws Exception {
		return this.getEPRsFor(new Srv(srvClass, srvName), srvType);
	}
	
	protected ConsistencyManagerIF getConsistencyManagerImpl() {
		return this.cmi;
	}
	
	protected void setConsistencyManagerImpl(ConsistencyManagerIF cmi) {
		this.cmi = cmi;
		log.debug("Updating the consistency manager pointer to registered services");
		Iterator<Srv> it = this.getSrvs().iterator();
		while (it.hasNext()) {
			Srv srv = it.next();
			log.debug("Updating the consistency manager pointer to srv '" + srv.toString() + "'");
			srv.setMyManager(this.cmi);
		}
	}

	/**
	 * @param scope the scope to set
	 */
	protected void setScope(GCUBEScope scope) {
		this.scope = scope;
	}

	/**
	 * @return the scope
	 */
	protected GCUBEScope getScope() {
		return scope;
	}
}

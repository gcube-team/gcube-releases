/**
 * 
 */
package org.gcube.informationsystem.cache.consistency.manager;

import java.util.Set;

import org.apache.axis.message.addressing.EndpointReference;
import org.gcube.informationsystem.cache.Srv;
import org.gcube.informationsystem.cache.SrvRegistry;

/**
 * Declares the necessary methods that all Cache Constistency Managers should
 * implement.
 * 
 * @author UoA
 * 
 */
public interface ConsistencyManagerIF {

	/**
	 * Initialize the Cache Constistency Manager instance
	 * 
	 * @param registry the service registry
	 * @throws Exception
	 *             in case of initialization error
	 */
	public void initialize(SrvRegistry registry) throws Exception;

	/**
	 * Get EPRs for the given service
	 * @param service service
	 * @return EPRs for the given service
	 * @throws Exception in case of error
	 */
	public Set<EndpointReference> getEPRs(Srv service) throws Exception;
	/**
	 * Get EPRs for the given type of the given service
	 * @param service service
	 * @param serviceType service type
	 * @return EPRs for the given type of the given service
	 * @throws Exception in case of error
	 */
	public Set<EndpointReference> getEPRs(Srv service, String serviceType) throws Exception;
}

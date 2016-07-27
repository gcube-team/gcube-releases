/**
 * 
 */
package org.gcube.informationsystem.cache.consistency.manager.poll;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReference;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.cache.Srv;
import org.gcube.informationsystem.cache.SrvType;

/**
 * @author paul
 * 
 */
public class CacheRefreshUtil {

	private static GCUBELog log = new GCUBELog(CacheRefreshUtil.class);

	private static Set<EndpointReference> getEPRsFor(Srv service) throws Exception {
		if (service.getEprs() != null && service.getEprs().size() != 0) {
			Iterator<String> it = service.getEprs().keySet().iterator();
			Set<EndpointReference> eprSet = new HashSet<EndpointReference>();
			while (it.hasNext()) {
				eprSet.addAll(service.getEprs().get(it.next()));
			}
			return eprSet;
		}
		return null;
	}

	/**
	 * get EPRs for the given service
	 * @param service service
	 * @return EPRs for the given service
	 * @throws Exception in case of error
	 */
	public static Set<EndpointReference> getEPRs(Srv service) throws Exception {
		log.debug("Retrieving eprs for service {" + service.toString() + "}");
		if (service.getEprs().size() != 0) {
			log
					.debug("It seems that this service has been examined before. Returning already registered eprs");
			return CacheRefreshUtil.getEPRsFor(service);
		}
		log.debug("This is the first time that service {" + service.toString()
				+ "} is examined.");
		PollManager.refreshService(service);
		log.debug("Refresh complete.");
		if (service.getEprs().size() != 0) {
			Set<EndpointReference> s = CacheRefreshUtil.getEPRsFor(service);
			log.debug("Number of EPRs: " + s.size());
			return s;
		}
		log.error("After service refresh I still cannot find any ");
		throw new Exception("This should not have happened");
	}

	/**
	 * Get EPRs for the given type of the given service
	 * @param service service
	 * @param serviceType service type
	 * @return EPRs for the given type of the given service
	 * @throws Exception in case of error
	 */
	public static Set<EndpointReference> getEPRs(Srv service, String serviceType)
			throws Exception {
		if(serviceType.equals(SrvType.FACTORY.toString()) == false &&
				serviceType.equals(SrvType.SIMPLE.toString()) == false &&
				serviceType.equals(SrvType.STATEFULL.toString()) == false)
			throw new Exception("Given service type is not valid.");
		if (service.getEprs().size() != 0
				&& service.getEprs().get(serviceType) != null)
			return service.getEprs().get(serviceType);
		PollManager.refreshService(service);
		if (service.getEprs().size() != 0
				&& service.getEprs().get(serviceType) != null)
			return service.getEprs().get(serviceType);
		throw new Exception("This should not have happened");
	}

	protected static void refreshService(Srv service) throws Exception {
		/*service.getEprs().put(SrvType.FACTORY.toString(),
				service.goForFactory());
		service.getEprs().put(SrvType.SIMPLE.toString(), service.goForSimple());
		service.getEprs().put(SrvType.STATEFULL.toString(),
				service.goForStateful());*/
		service.forceRefreshService();
	}

}

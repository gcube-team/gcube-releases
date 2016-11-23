package org.gcube.vremanagement.resourcemanager.impl.brokerage.servicebroker;

import java.io.IOException;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream.XStreamTransformer;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;

/**
 * 
 * Parser for deployment plans. It uses the Serialization API provided by the Broker Service
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class PlanParser {
	
	private static final GCUBELog logger = new GCUBELog(PlanParser.class);

	
	/**
	 * Parses the current plan and assigns gHNs to services
	 * @param plan the plan returned by the Broker
	 * @param services the services to deploy
	 * @param scope the current scope
	 * @throws IOException
	 */
	public static void parse(String plan, Set<ScopedDeployedSoftware> services, ScopeState scopeState) throws IOException {
		XStreamTransformer transformer = new XStreamTransformer();
		PlanResponse resp = transformer.getResponseFromXML(plan, false);
		if (resp.getStatus().getStatus().compareToIgnoreCase("FAILED")==0) {
			logger.error("The creation of the Deployment Plan failed. Broker says: " + resp.getStatus().getMsg());
			throw new IOException ("Broker says: " + resp.getStatus().getMsg());
		} else if (resp.getStatus().getStatus().compareToIgnoreCase("SUCCESS")==0) {
			logger.info("The creation of the Deployment Plan was successful");			
		}
		//assign ghns to services
		for (PackageGroup group: resp.getPackageGroups()) {
			String serviceID = group.getServiceName();
			String ghnID = group.getGHN();
			if (ghnID == null) {
				logger.error("no gHN was assigned to service " + serviceID);
				throw new IOException("no gHN was assigned to service " + serviceID);
			}
			for (ScopedDeployedSoftware service : services) {
				if (service.getId().compareToIgnoreCase(serviceID) == 0) {
					logger.info("Assigning gHN " + ghnID + " to " +service);
					VirtualNode node = null;
					try {
						node = scopeState.getNodeById(ghnID);
					} catch (NoGHNFoundException e) {
						logger.error("unable to find gHN " + ghnID + " returned by the Broker");
						throw new IOException("unable to find gHN " + ghnID + " returned by the Broker");
					} 
					node.setWorkingScope(scopeState.getScope());
					node.isNotWorking();
					service.scheduleDeploy(node);
					break;
				}						
			}		
		}
		
	}
}

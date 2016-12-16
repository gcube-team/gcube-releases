package org.gcube.vremanagement.resourcemanager.impl.brokerage.servicebroker;

import java.io.IOException;
import java.util.Set;

import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.Dependency;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;

import org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream.XStreamTransformer;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;

/**
 * 
 * Creates a plan request with the Serialization API provided by the Broker Service
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class PlanRequest {	
	
	/**
	 * Creates the request plan for the Broker Service
	 * @param services the services to deploy
	 * @param suggestedGHNs the GHNs suggested for the deployment
	 * @param scope the scope of the request
	 * @thrown {@link IOException} if the request cannot be created 
	 * @return the XML representation of the request 
	 * @throws NoGHNFoundException if any of the target suggested node does not exist
	 * 
	 */
	public static String create(Set<ScopedDeployedSoftware> services, ScopeState scopeState, String ... suggestedGHNs) throws IOException, NoGHNFoundException  {
		org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest planReq = new org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest(scopeState.getScope().toString());
		for (ScopedDeployedSoftware service : services) {		
			PackageGroup group = planReq.createPackageGroup( service.getId());			
			for (Dependency dep : service.getLastResolvedDependencies()) {
				group.addPackage(new PackageElem(false, dep.getService().getClazz(), dep.getService().getName(),
						dep.getService().getVersion(), dep.getName(), dep.getVersion()));
				if (service.getSourcePackage().getGHNName() != null)
						group.setGHN(scopeState.getNode(service.getSourcePackage().getGHNName()).getID());
			}		
		}
		if (suggestedGHNs != null) {		
			for (String ghn : suggestedGHNs)
				planReq.getGHNList().addGHN(ghn);			
		}		
		XStreamTransformer transformer = new XStreamTransformer();		
		return transformer.toXML(planReq);
	}
}

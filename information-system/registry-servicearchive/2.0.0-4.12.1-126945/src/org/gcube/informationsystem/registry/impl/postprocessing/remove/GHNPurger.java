package org.gcube.informationsystem.registry.impl.postprocessing.remove;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.porttypes.LocalResourceRegistration;
import org.gcube.informationsystem.registry.impl.porttypes.RegistryFactory;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.stubs.RemoveResourceMessage;

/**
 * 
 * Purger for GHN resources
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class GHNPurger implements Purger<GCUBEHostingNode> {

	protected static final GCUBELog logger = new GCUBELog(GHNPurger.class);

	/**
	 * Unregisters all the RIs hosted on the gHN
	 * 
	 * @throws RemoteException
	 * @deprecated
	 */
	public void unregisterHostedRIs(RegistryFactory service, String ghnid) throws Exception {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		RemoveResourceMessage message = null;
		GCUBEGenericQuery query = client.getQuery("RIOnGHN");
		query.addParameters(new QueryParameter("ID", ghnid));

		// query to IS on order to retrieve the RI deployed on the GHN;
		List<XMLResult> resources = client.execute(query, ServiceContext.getContext().getScope());

		if (resources.size() != 0) {
			for (XMLResult resource : resources) {
				try {
					message = new RemoveResourceMessage();
					message.setType(GCUBERunningInstance.TYPE);
					message.setUniqueID(resource.evaluate("/Resource/ID/text()").get(0));
					service.removeResource(message);
				} catch (RemoteException rme) {
				}
			}

		}
	}
	

	@Override
	public Set<String> purge(String ghnid, GCUBEScope scope) throws Exception {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGenericQuery query = client.getQuery("RIOnGHN");
		query.addParameters(new QueryParameter("ID", ghnid));
		// query to IS on order to retrieve the RI deployed on the GHN;
		List<XMLResult> resources = client.execute(query, scope);
		Set<String> removed = new HashSet<String>();
		if (resources.size() != 0) {
			for (XMLResult ri : resources) {
				try {
					String id = ri.evaluate("/Resource/ID/text()").get(0);
					logger.debug("Removing Running Instance " + id );
					//ISResourcePublisher publisher = GHNContext.getImplementation(ISResourcePublisher.class);									
					//publisher.remove(id, GCUBERunningInstance.TYPE, ServiceContext.getContext().getScope(), ServiceContext.getContext());
					new LocalResourceRegistration().remove(id, GCUBERunningInstance.TYPE);
					logger.debug("Resource " + id+ " successfully removed");
					removed.add(id);
				} catch (Exception e) {
					logger.error("Failed to remove a hosted RI from ghn " + ghnid, e);
				}
			}
		}		
		
		return removed;
	}


	@Override
	public String getName() {
		return GCUBEHostingNode.TYPE;
	}

}

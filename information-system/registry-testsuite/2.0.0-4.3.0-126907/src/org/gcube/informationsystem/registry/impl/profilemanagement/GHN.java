package org.gcube.informationsystem.registry.impl.profilemanagement;

import java.rmi.RemoteException;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.publisher.ISResourcePublisher;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.porttypes.RegistryFactory;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.stubs.RemoveResourceMessage;

/**
 * 
 * GHN profile management
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class GHN {

	public static ISClient client = null;

	protected static final GCUBELog logger = new GCUBELog(GHN.class);

	private String id = null;

	public GHN(String id) {
		this.id = id;
	}

	/**
	 * Unregisters all the RIs hosted on the gHN
	 * 
	 * @throws RemoteException
	 * @deprecated
	 */
	public void unregisterHostedRIs(RegistryFactory service) throws Exception {
		if (client == null)
			client = GHNContext.getImplementation(ISClient.class);
		RemoveResourceMessage message = null;
		GCUBEGenericQuery query = client.getQuery("RIOnGHN");
		query.addParameters(new QueryParameter("ID", this.id));

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
	
	public void unregisterHostedRI() throws Exception {
		if (client == null)
			client = GHNContext.getImplementation(ISClient.class);

		GCUBEGenericQuery query = client.getQuery("RIOnGHN");
		query.addParameters(new QueryParameter("ID", this.id));

		// query to IS on order to retrieve the RI deployed on the GHN;
		List<XMLResult> resources = client.execute(query, ServiceContext.getContext().getScope());

		if (resources.size() != 0) {
			for (XMLResult resource : resources) {
				try {
					String id = resource.evaluate("/Resource/ID/text()").get(0);
					ISResourcePublisher publisher = GHNContext.getImplementation(ISResourcePublisher.class);									
					publisher.remove(id, GCUBERunningInstance.TYPE, ServiceContext.getContext().getScope(), ServiceContext.getContext());
					logger.debug("Resource " + id+ " successfully removed");					
				} catch (Exception e) {
					logger.error("Failed to remove a hosted RI from ghn " + this.id, e);
				}
			}
		}		
	}
	
	
}

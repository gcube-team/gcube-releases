package org.gcube.common.informationsystem.publisher.impl.registrations.resources;

import java.util.List;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;


/**
 * Lookup for new ISRegistry instances in a {@link GCUBEScope}} and refresh the {@link ISRegistryInstanceGroup}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ISRegistryLookup extends GCUBEHandler<GCUBEScope> {
	
	
	@Override
	public void run() throws Exception {		
		logger.trace("Querying the IS for Registry instances");					
		List<GCUBERunningInstance> instances = this.queryForISRegistry();
		logger.trace("Number of instances discovered: " + instances.size());
		if (instances.size() == 0)
			throw new NoRegistryAvailableException();
		
		ISRegistryInstanceGroup availableInstances = ISRegistryInstanceGroup.getInstanceGroup();
		
		for (GCUBERunningInstance instance: instances)
			availableInstances.addRegistry(ISRegistryInstance.fromGCUBERunningInstance(instance));
		
	}
	
	/**
	 * Looks for all the ISRegistry instances in Scope		
	 * @return
	 * @throws Exception
	 */
	private List<GCUBERunningInstance> queryForISRegistry() throws Exception {
		 //looks for all the ISRegistry instances		
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery lookupQuery = client.getQuery(GCUBERIQuery.class);
		lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceName",ISRegistryInstance.ISREGISTRY_NAME));
		lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceClass",ISRegistryInstance.ISREGISTRY_CLASS));
		return client.execute(lookupQuery, this.getHandled());
				
	}

	/** No Registry instance available exception */
	public static class NoRegistryAvailableException extends Exception {private static final long serialVersionUID = 1L;}
	
}

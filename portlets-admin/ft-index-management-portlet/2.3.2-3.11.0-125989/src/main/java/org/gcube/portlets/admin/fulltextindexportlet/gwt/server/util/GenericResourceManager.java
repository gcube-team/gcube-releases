/**
 * 
 */
package org.gcube.portlets.admin.fulltextindexportlet.gwt.server.util;

import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class GenericResourceManager {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(GenericResourceManager.class);
	
	/**
	 * Retrieves a set of generic resources from the IS and returns them.
	 * 
	 * @param conditions pairs of strings representing "property"="value" conditions
	 * @return an object representing the generic resource
	 */
	public static List<GenericResource> retrieveGenericResource(List<String> conditions, String scope) throws Exception {
		try {
			//ScopeProvider.instance.set(scope);
			SimpleQuery query = queryFor(GenericResource.class);
			for (String cond : conditions) 
				query.addCondition(cond);
		
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			List<GenericResource> result = client.submit(query);
			
			if (result==null || result.size()==0)
				throw new Exception("No generic resources found for the given conditions.");
			return result;
		} catch (Exception e) {
			logger.error("Failed to retrieve generic resources.", e);
			throw new Exception("Failed to retrieve generic resources.", e);
		}
	}
	
	public static String updateGenericResource(GenericResource resource, String scope) throws Exception {
		ScopeProvider.instance.set(scope);
		RegistryPublisher publisher = RegistryPublisherFactory.create();
		GenericResource updatedResource = publisher.update(resource);
		return updatedResource.id();
	}
	
	public static void deleteGenericResource(GenericResource resource, String scope) throws Exception {
		ScopeProvider.instance.set(scope);
		RegistryPublisher publisher = RegistryPublisherFactory.create();
		publisher.remove(resource);
	}
}

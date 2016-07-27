package org.gcube.informationsystem.registry.impl.postprocessing.remove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.publisher.ISGenericPublisher;
import org.gcube.common.core.informationsystem.publisher.ISResource;
import org.gcube.common.core.informationsystem.publisher.ISResource.ISRESOURCETYPE;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Purger for Running Instance resources. Cleanup all the resources related to
 * a just-deleted RI.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class RIPurger implements Purger<GCUBERunningInstance> {

	protected static final GCUBELog logger = new GCUBELog(GHNPurger.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> purge(String id, GCUBEScope scope) throws Exception {
		Set<String>  ids = this.getWSResourceIDs(id, scope);
		Set<String> removed = new HashSet<String>();
		ISGenericPublisher publisher = GHNContext.getImplementation(ISGenericPublisher.class);
		List<ISResource> resources = new ArrayList<ISResource>();
		for (String rpdoc : ids) {
			ISResource resource = GHNContext.getImplementation(ISResource.class);
			resource.setType(ISRESOURCETYPE.RPD);
			resource.setCollection("Properties");
			resource.setID(rpdoc);
			logger.debug("Removing WS-ResourceProperty document: " + rpdoc);
			resources.add(resource);
			removed.add(rpdoc);
		}
		
		try {		
			publisher.remove(resources, scope);
		} catch (Exception e) {
			logger.error("Failed to removeWS-ResourceProperty documents", e);
		}
		return removed;
	}

	private Set<String> getWSResourceIDs(String riid, GCUBEScope scope) throws Exception {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGenericQuery query = client.getQuery(GCUBEGenericQuery.class);
		Set<String> ids = new HashSet<String>();
		query.setExpression(this.getAllRPIDsExpression(riid));
		for (XMLResult rpd :  client.execute(query,scope)) 
				ids.add(rpd.toString().trim());
		return ids;
	}
	
	private String getAllRPIDsExpression(String riid) {
		return "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; "
		+ "declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; "
		+ "for $outer in collection(\"/db/Properties\")//Document, $result in  $outer/Data  "
		+ "where ($result//gc:RI/string() eq \""+riid+"\") "
		+ "return $outer/ID/text()";
	}

	@Override
	public String getName() {
		return GCUBERunningInstance.TYPE;
	}
	
}

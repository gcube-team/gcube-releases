/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins.query;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.clients.Plugin;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.queries.Query;
import org.gcube.vremanagement.executor.client.query.Discover;
import org.gcube.vremanagement.executor.client.query.filter.impl.RandomGCoreEndpointQueryFilter;

/**
 * @author Luca Frosini (ISTI - CNR)
 * For internal use only to maintain backward compatibility  
 */
public class SmartExecutorPluginQuery extends Discover implements Query<EndpointReference> {
	
	public SmartExecutorPluginQuery(Plugin<?,?> plugin){
		super(plugin.name());
		this.endpointDiscoveryFilter = new RandomGCoreEndpointQueryFilter();
		this.containsFormat = "contains($entry/string(),'%1s')";
	}
	
	@Override
	public List<EndpointReference> fire() throws DiscoveryException {
		List<EndpointReference> refs = new ArrayList<EndpointReference>();
		try {
			List<String> addresses = getAddresses();
			for(String address : addresses)
				refs.add(new W3CEndpointReferenceBuilder().address(address).build());
		} catch(org.gcube.resources.discovery.client.api.DiscoveryException ex) {
			throw new DiscoveryException(ex);
		}
		
		return refs;
	}
	
}

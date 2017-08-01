package org.gcube.common.clients.fw.queries;

import static java.lang.String.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.common.clients.queries.Query;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

/**
 * A {@link Query} for stateful gCore services.
 * 
 * @author Fabio Simeoni
 *
 */
public class StatefulQuery implements Query<EndpointReference> {

	private static String classFormat = "$resource/Data/gcube:ServiceClass/text() eq '%1s'";
	private static String nameFormat = "$resource/Data/gcube:ServiceName/text() eq '%1s'";
	private static String entryFormat = "$resource/Source/text()[ends-with(.,'%1s')]";

	private static DiscoveryClient<ServiceInstance> client = ICFactory.clientFor(ServiceInstance.class);
	private final SimpleQuery query;
	
	/**
	 * Creates an instance with a given proxy {@link Plugin}.
	 * 
	 * @param plugin the plugin
	 */
	public StatefulQuery(Plugin<?,?> plugin) {
	
		query = ICFactory.queryFor(ServiceInstance.class)
					.addCondition(format(classFormat,plugin.serviceClass()))
					.addCondition(format(nameFormat,plugin.serviceName()))
					.addCondition(format(entryFormat,plugin.name()));
		
	}

	/**
	 * Adds a variable to the query. 
	 * 
	 * @param name the name of the variable
	 * @param range the range of the variable
	 * @return the query
	 *
	 * @see SimpleQuery#addVariable(String, String)
	 * 
	 */
	public StatefulQuery  addVariable(String name, String range) {
		query.addVariable(name, range);
		return this;
	}


	/**
	 * Adds a free-form condition on query results. 
	 * 
	 * @param condition the condition
	 * @return the query
	 * @see SimpleQuery#addCondition(String)
	 */
	public StatefulQuery addCondition(String condition) {
		query.addCondition(condition);
		return this;
	}


	/**
	 * Adds a namespace to the query. 
	 * @param prefix the namespace prefix
	 * @param uri the namespace URI
	 * @return the query
	 * @see SimpleQuery#addNamespace(String, URI)
	 */
	public StatefulQuery addNamespace(String prefix, URI uri) {
		query.addNamespace(prefix, uri);
		return this;
	}

	@Override
	public List<EndpointReference> fire() throws DiscoveryException {
		
		List<EndpointReference> refs = new ArrayList<EndpointReference>();
		
		
		List<ServiceInstance> instances = null;
		try {
			instances = client.submit(query);
		}
		catch(org.gcube.resources.discovery.client.api.DiscoveryException ex) {
			throw new DiscoveryException(ex);
		}
		
		for(ServiceInstance instance : instances)
			refs.add(instance.reference());
		
		return refs;
	}
	
	@Override
	public String toString() {
		return query.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatefulQuery other = (StatefulQuery) obj;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}
	
	
}

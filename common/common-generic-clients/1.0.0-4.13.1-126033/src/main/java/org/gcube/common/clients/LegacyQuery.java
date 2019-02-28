package org.gcube.common.clients;

import static java.lang.String.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.queries.Query;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

/**
 * A query for service endpoints published as {@link GCoreEndpoint}s.
 * @author Fabio Simeoni
 *
 */
public class LegacyQuery implements Query<EndpointReference> {

	private static String classFormat = "$resource/Profile/ServiceClass/text() eq '%1s'";
	private static String nameFormat = "$resource/Profile/ServiceName/text() eq '%1s'";
	private static String statusFormat = "$resource/Profile/DeploymentData/Status/text() eq 'ready'";
	private static String containsFormat = "contains($entry/string(),'%1s')";

	private static DiscoveryClient<String> client = ICFactory.client();
	private final SimpleQuery query;
	
	/**
	 * Creates an instance with a given proxy {@link Plugin}.
	 * 
	 * @param plugin the plugin
	 */
	public LegacyQuery(Plugin<?,?> plugin) {
	
		query = ICFactory.queryFor(GCoreEndpoint.class)
				.addCondition(format(classFormat,plugin.serviceClass()))
				.addCondition(format(nameFormat,plugin.serviceName()))
				.addCondition(format(statusFormat))
				.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(format(containsFormat,plugin.name()))
				.setResult("$entry/text()");
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
	public LegacyQuery addVariable(String name, String range) {
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
	public LegacyQuery addCondition(String condition) {
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
	public LegacyQuery addNamespace(String prefix, URI uri) {
		query.addNamespace(prefix, uri);
		return this;
	}

	@Override
	public List<EndpointReference> fire() throws DiscoveryException {
		
		List<EndpointReference> refs = new ArrayList<EndpointReference>();
		try {
			List<String> addresses = client.submit(query);
			for(String address : addresses)
				refs.add(new W3CEndpointReferenceBuilder().address(address).build());
		}
		catch(org.gcube.resources.discovery.client.api.DiscoveryException ex) {
			throw new DiscoveryException(ex);
		}
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
		LegacyQuery other = (LegacyQuery) obj;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}
}

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
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.vremanagement.executor.client.plugins.query.filter.EndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.plugins.query.filter.RandomEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.plugins.query.filter.ServiceEndpointQueryFilter;
import org.gcube.vremanagement.executor.client.util.Tuple;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SmartExecutorPluginQuery implements Query<EndpointReference> {
	
	public static String classFormat = "$resource/Profile/ServiceClass/text() eq '%1s'";
	public static String nameFormat = "$resource/Profile/ServiceName/text() eq '%1s'";
	public static String statusFormat = "$resource/Profile/DeploymentData/Status/text() eq 'ready'";
	public static String containsFormat = "contains($entry/string(),'%1s')";
	
	private final Plugin<?,?> plugin;
	
	private final SimpleQuery smartExecutorDiscoveryQuery;
	
	private ServiceEndpointQueryFilter serviceEndpointQueryFilter;
	private EndpointDiscoveryFilter endpointDiscoveryFilter = new RandomEndpointDiscoveryFilter();;
	
	private static SimpleQuery makeEndpointDiscoveryQuery(Plugin<?,?> plugin){
		return ICFactory.queryFor(GCoreEndpoint.class)
				.addCondition(String.format(classFormat, plugin.serviceClass()))
				.addCondition(String.format(nameFormat, plugin.serviceName()))
				.addCondition(String.format(statusFormat))
				.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(String.format(containsFormat,plugin.name()))
				.setResult("$entry/text()");
	}
	
	public SmartExecutorPluginQuery(Plugin<?,?> plugin){
		this.plugin = plugin;
		
		smartExecutorDiscoveryQuery = ICFactory.queryFor(ServiceEndpoint.class)
		.addCondition(String.format("$resource/Profile/Category/text() eq '%s'", plugin.serviceClass()))
		.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", plugin.serviceName()))
		.setResult("$resource");
		
	}

	@SuppressWarnings("unchecked")
	public void addConditions(String pluginName, Tuple<String, String> ... tuples){
		smartExecutorDiscoveryQuery.addVariable("$accessPoint", "$resource/Profile/AccessPoint")
		.addCondition(String.format("$accessPoint/Interface/Endpoint/@EntryName eq '%s'", pluginName));
		if(tuples==null){
			return;
		}
		for(int i=0; i<tuples.length; i++){
			Tuple<String, String> tuple = tuples[i];
			String propertyVariableName = String.format("$property%d", i);
			smartExecutorDiscoveryQuery
			.addVariable(propertyVariableName, "$accessPoint/Properties/Property")
			.addCondition(String.format("%s/Name/text() eq '%s'", propertyVariableName, tuple.getName()))
			.addCondition(String.format("%s/Value/text() eq '%s'", propertyVariableName, tuple.getValue()));	
		}
	}
	
	public void setServiceEndpointQueryFilter(ServiceEndpointQueryFilter serviceEndpointQueryFilter){
		this.serviceEndpointQueryFilter = serviceEndpointQueryFilter;
	}
	
	public void setEndpointDiscoveryFilter(EndpointDiscoveryFilter endpointDiscoveryFilter){
		if(endpointDiscoveryFilter!=null){
			this.endpointDiscoveryFilter = endpointDiscoveryFilter;
		}
	}
	
	public List<String> discoverEndpoints(EndpointDiscoveryFilter endpointDiscoveryFilter) throws DiscoveryException {
		if(serviceEndpointQueryFilter!=null){
			serviceEndpointQueryFilter.filter(smartExecutorDiscoveryQuery);
		}
		
		List<ServiceEndpoint> serviceEndpoints = ICFactory.clientFor(ServiceEndpoint.class).submit(smartExecutorDiscoveryQuery);
		
		if(serviceEndpoints.size() == 0){
			throw new DiscoveryException("No running SmartExecutor wich match the requested conditions");
		}
		
		SimpleQuery preRunQuery = makeEndpointDiscoveryQuery(plugin);
		endpointDiscoveryFilter.filter(preRunQuery, serviceEndpoints);
		
		return ICFactory.client().submit(preRunQuery);
		
	}
	
	
	@Override
	public List<EndpointReference> fire() throws DiscoveryException {
		if(serviceEndpointQueryFilter!=null){
			serviceEndpointQueryFilter.filter(smartExecutorDiscoveryQuery);
		}
		
		List<ServiceEndpoint> serviceEndpoints = ICFactory.clientFor(ServiceEndpoint.class).submit(smartExecutorDiscoveryQuery);
		
		if(serviceEndpoints.size() == 0){
			throw new DiscoveryException("No running SmartExecutor wich match the requested conditions");
		}
		
		
		SimpleQuery gCoreEndpointDiscoveryQuery = makeEndpointDiscoveryQuery(plugin);
		endpointDiscoveryFilter.filter(gCoreEndpointDiscoveryQuery, serviceEndpoints);
		
		List<EndpointReference> refs = new ArrayList<EndpointReference>();
		try {
			List<String> addresses = ICFactory.client().submit(gCoreEndpointDiscoveryQuery);
			for(String address : addresses)
				refs.add(new W3CEndpointReferenceBuilder().address(address).build());
		} catch(org.gcube.resources.discovery.client.api.DiscoveryException ex) {
			throw new DiscoveryException(ex);
		}
		
		return refs;
	}
	
	
}

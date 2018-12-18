package org.gcube.vremanagement.executor.client.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.vremanagement.executor.client.Constants;
import org.gcube.vremanagement.executor.client.plugins.query.filter.EndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.query.filter.GCoreEndpointQueryFilter;
import org.gcube.vremanagement.executor.client.query.filter.ServiceEndpointQueryFilter;
import org.gcube.vremanagement.executor.client.util.Tuple;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("deprecation")
public class Discover {
	
	private static String classFormat = "$resource/Profile/ServiceClass/text() eq '%1s'";
	private static String nameFormat = "$resource/Profile/ServiceName/text() eq '%1s'";
	private static String statusFormat = "$resource/Profile/DeploymentData/Status/text() eq 'ready'";
	
	protected String containsFormat;
	
	protected final String entryName;
	
	private final SimpleQuery serviceEndpointQuery;
	
	protected org.gcube.vremanagement.executor.client.plugins.query.filter.ServiceEndpointQueryFilter serviceEndpointQueryFilter;
	protected EndpointDiscoveryFilter endpointDiscoveryFilter;
	
	protected SimpleQuery getGCoreEndpointQuery() {
		return ICFactory.queryFor(GCoreEndpoint.class)
				.addCondition(String.format(classFormat, Constants.SERVICE_CLASS))
				.addCondition(String.format(nameFormat, Constants.SERVICE_NAME))
				.addCondition(String.format(statusFormat))
				.addVariable("$entry", "$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(String.format(containsFormat, entryName))
				.setResult("$entry/text()");
	}
	
	public Discover(String entryName) {
		this.entryName = entryName;
		this.serviceEndpointQuery = ICFactory.queryFor(ServiceEndpoint.class)
				.addCondition(String.format("$resource/Profile/Category/text() eq '%s'", Constants.SERVICE_CLASS))
				.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", Constants.SERVICE_NAME))
				.setResult("$resource");
		this.containsFormat = "$entry/@EntryName eq '%1s'";
		
	}
	
	public void filterByPluginName(String pluginName) {
		serviceEndpointQuery.addVariable("$accessPoint", "$resource/Profile/AccessPoint")
				.addCondition(String.format("$accessPoint/Interface/Endpoint/@EntryName eq '%s'", pluginName));
	}
	
	public void filterByCapabilities(Map<String, String> capabilities) {
		if(capabilities!=null && !capabilities.isEmpty()) {
			int i=0;
			for(String key : capabilities.keySet()) {
				String propertyVariableName = String.format("$property%d", i);
				serviceEndpointQuery.addVariable(propertyVariableName, "$accessPoint/Properties/Property")
						.addCondition(String.format("%s/Name/text() eq '%s'", propertyVariableName, key))
						.addCondition(String.format("%s/Value/text() eq '%s'", propertyVariableName, capabilities.get(key)));
				++i;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public void addConditions(String pluginName, Tuple<String,String>... tuples) {
		
		filterByPluginName(pluginName);
		
		if(tuples == null) {
			return;
		}
		Map<String, String> capabilities = new HashMap<>();
		for(int i = 0; i < tuples.length; i++) {
			capabilities.put(tuples[i].getName(), tuples[i].getValue());
		}
	}

	public void setServiceEndpointQueryFilter(ServiceEndpointQueryFilter serviceEndpointQueryFilter) {
		this.serviceEndpointQueryFilter = serviceEndpointQueryFilter;
	}
	
	@Deprecated
	public void setServiceEndpointQueryFilter(org.gcube.vremanagement.executor.client.plugins.query.filter.ServiceEndpointQueryFilter serviceEndpointQueryFilter) {
		this.serviceEndpointQueryFilter = serviceEndpointQueryFilter;
	}
	
	public void setGCoreEndpointQueryFilter(GCoreEndpointQueryFilter gCoreEndpointQueryFilter) {
		if(gCoreEndpointQueryFilter != null) {
			this.endpointDiscoveryFilter = gCoreEndpointQueryFilter;
		}
	}
	
	@Deprecated
	public void setEndpointDiscoveryFilter(EndpointDiscoveryFilter endpointDiscoveryFilter) {
		if(endpointDiscoveryFilter != null) {
			this.endpointDiscoveryFilter = endpointDiscoveryFilter;
		}
	}
	
	@Deprecated
	public List<String> discoverEndpoints(EndpointDiscoveryFilter endpointDiscoveryFilter) throws DiscoveryException {
		if(serviceEndpointQueryFilter != null) {
			serviceEndpointQueryFilter.filter(serviceEndpointQuery);
		}
		
		List<ServiceEndpoint> serviceEndpoints = ICFactory.clientFor(ServiceEndpoint.class)
				.submit(serviceEndpointQuery);
		
		if(serviceEndpoints.size() == 0) {
			throw new DiscoveryException("No running SmartExecutor wich match the requested conditions");
		}
		
		SimpleQuery gCoreEndpointQuery = getGCoreEndpointQuery();
		endpointDiscoveryFilter.filter(gCoreEndpointQuery, serviceEndpoints);
		
		return ICFactory.client().submit(gCoreEndpointQuery);
		
	}
	
	public List<String> getAddresses() throws DiscoveryException {
		if(serviceEndpointQueryFilter != null) {
			serviceEndpointQueryFilter.filter(serviceEndpointQuery);
		}
		
		List<ServiceEndpoint> serviceEndpoints = ICFactory.clientFor(ServiceEndpoint.class)
				.submit(serviceEndpointQuery);
		
		if(serviceEndpoints.size() == 0) {
			throw new DiscoveryException("No running SmartExecutor wich match the requested conditions");
		}
		
		SimpleQuery gCoreEndpointDiscoveryQuery = getGCoreEndpointQuery();
		if(endpointDiscoveryFilter!=null) {
			endpointDiscoveryFilter.filter(gCoreEndpointDiscoveryQuery, serviceEndpoints);
		}
		
		List<String> addresses = ICFactory.client().submit(gCoreEndpointDiscoveryQuery);
		
		return addresses;
	}
	
}

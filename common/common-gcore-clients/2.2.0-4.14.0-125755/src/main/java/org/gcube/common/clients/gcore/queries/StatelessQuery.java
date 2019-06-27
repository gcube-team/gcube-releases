package org.gcube.common.clients.gcore.queries;

import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.gcore.plugins.Plugin;
import org.gcube.common.clients.gcore.plugins.PluginAdapter;
import org.gcube.common.clients.queries.Query;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Query} for stateless gCore services.
 * 
 * @author Fabio Simeoni
 * 
 */
public final class StatelessQuery extends GCoreQuery<GCUBERunningInstance> {

	private static Logger log = LoggerFactory.getLogger(StatelessQuery.class);
	
	/**
	 * Creates an instance with a {@link ISFacade}, a {@link PluginAdapter}
	 * @param facade the facade
	 * @param plugin the plugin
	 */
	public StatelessQuery(ISFacade runner,Plugin<?,?> plugin) {
		super(runner,plugin);
		
		//adds systematically base conditions
		addCondition("//ServiceName", plugin.serviceName());
		addCondition("//ServiceClass", plugin.serviceClass());
	}
	
	/**
	 * Creates an instance with a {@link PluginAdapter} and a default {@link ISFacade}.
	 * @param plugin the plugin
	 */
	public StatelessQuery(Plugin<?,?> plugin) {
		this(new DefaultISFacade(),plugin);
	}
	
	@Override
	protected List<GCUBERunningInstance> fire(Map<String, String> conditions) throws DiscoveryException {
		return facade().execute(GCUBERIQuery.class,conditions);
	}
	
	@Override
	protected EndpointReferenceType address(GCUBERunningInstance instance) throws IllegalArgumentException {
		EndpointReferenceType address = instance.getAccessPoint().getEndpoint(plugin().name());
		if (address==null) {
			log.warn("Running instance does not match {}: malformed profile or plugin misconfiguration",plugin().name()); 
			throw new IllegalArgumentException();
		}
		else
			return address;
	}
	

}

package org.gcube.common.clients.gcore.queries;

import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.gcore.plugins.Plugin;
import org.gcube.common.clients.gcore.plugins.PluginAdapter;
import org.gcube.common.clients.queries.Query;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;

/**
 * A {@link Query} for stateful gCore services.
 * 
 * @author Fabio Simeoni
 *
 */
public final class StatefulQuery extends GCoreQuery<RPDocument> {

	/**
	 * Creates an instance with a {@link PluginAdapter} and a {@link ISFacade}.
	 * @param facade the facade
	 * @param plugin the plugin
	 */
	public StatefulQuery(ISFacade facade, Plugin<?,?> plugin) {
		super(facade,plugin);
		
		//adds systematically base conditions
		addCondition("//gc:ServiceName", plugin.serviceName());
		addCondition("//gc:ServiceClass", plugin.serviceClass());
	}
	
	/**
	 * Creates an instance with a {@link PluginAdapter}.
	 * @param plugin the plugin
	 */
	public StatefulQuery(Plugin<?,?> plugin) {
		this(new DefaultISFacade(),plugin);
	}
	
	@Override
	protected List<RPDocument> fire(Map<String, String> conditions) throws DiscoveryException {
		return facade().execute(WSResourceQuery.class,conditions);
	}

	@Override
	protected EndpointReferenceType address(RPDocument doc) throws IllegalArgumentException {
		if (doc.getEndpoint().getAddress().getPath().endsWith(plugin().name()))
			return doc.getEndpoint();
		else
			throw new IllegalArgumentException();
	}
}

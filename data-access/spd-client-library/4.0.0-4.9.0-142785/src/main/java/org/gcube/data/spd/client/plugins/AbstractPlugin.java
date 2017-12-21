package org.gcube.data.spd.client.plugins;

import javax.ws.rs.client.WebTarget;

import org.gcube.common.clients.LegacyQuery;
import org.gcube.common.clients.Plugin;
import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.data.spd.client.Constants;
import org.gcube.data.spd.client.proxies.ClassificationClient;
import org.gcube.data.spd.client.proxies.ExecutorClient;
import org.gcube.data.spd.client.proxies.ManagerClient;
import org.gcube.data.spd.client.proxies.OccurrenceClient;
import org.gcube.data.spd.client.proxies.ResultSetClient;



public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	private static final ManagerPlugin manager_plugin = new ManagerPlugin();
	private static final OccurrencePlugin occurrence_plugin = new OccurrencePlugin();
	private static final ClassificationPlugin classification_plugin = new ClassificationPlugin();
	private static final ExecutorPlugin executor_plugin = new ExecutorPlugin();
	private static final ResultSetPlugin resultset_plugin = new ResultSetPlugin();
	
	
	public static ProxyBuilder<ManagerClient> manager() {
	    return new ProxyBuilderImpl<WebTarget,ManagerClient>(manager_plugin);
	}
	
	public static ProxyBuilder<ClassificationClient> classification() {
	    return new ProxyBuilderImpl<WebTarget,ClassificationClient>(classification_plugin);
	}
	
	public static ProxyBuilder<OccurrenceClient> occurrences() {
		return new ProxyBuilderImpl<WebTarget,OccurrenceClient>(occurrence_plugin);
	}
	
	public static ProxyBuilder<ExecutorClient> executor() {
		return new ProxyBuilderImpl<WebTarget,ExecutorClient>(executor_plugin);
	}
	
	public static ProxyBuilder<ResultSetClient> resultset(String endpointId) {
		LegacyQuery query = new LegacyQuery(resultset_plugin);
		query.addCondition("$resource/ID/string() eq '"+endpointId+"'"); 
		return new ProxyBuilderImpl<WebTarget,ResultSetClient>(resultset_plugin, query);
	}
	
	public final String name;
	
	public AbstractPlugin(String name) {
		this.name=name;
	}
	
	@Override
	public String serviceClass() {
		return Constants.SERVICE_CLASS;
	}
	
	@Override
	public String serviceName() {
		return Constants.SERVICE_NAME;
	}
	
	@Override
	public String namespace() {
		return Constants.NAMESPACE;
	}
	
	@Override
	public String name() {
		return name;
	}
	
}
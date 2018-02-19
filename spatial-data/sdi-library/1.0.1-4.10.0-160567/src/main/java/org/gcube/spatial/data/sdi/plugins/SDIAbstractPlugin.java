package org.gcube.spatial.data.sdi.plugins;

import javax.ws.rs.client.WebTarget;

import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.common.clients.Plugin;
import org.gcube.spatial.data.sdi.interfaces.Metadata;
import org.gcube.spatial.data.sdi.model.ServiceConstants;

public abstract class SDIAbstractPlugin<S, P> implements Plugin<S, P>{

	
	private static final MetadataPlugin metadata_plugin=new MetadataPlugin();
	
	
	public static ProxyBuilder<Metadata> metadata() {
	    return new ProxyBuilderImpl<WebTarget,Metadata>(metadata_plugin);
	}
	
//	public static ProxyBuilder<ClassificationClient> classification() {
//	    return new ProxyBuilderImpl<WebTarget,ClassificationClient>(classification_plugin);
//	}
//	
//	public static ProxyBuilder<OccurrenceClient> occurrences() {
//		return new ProxyBuilderImpl<WebTarget,OccurrenceClient>(occurrence_plugin);
//	}
//	
//	public static ProxyBuilder<ExecutorClient> executor() {
//		return new ProxyBuilderImpl<WebTarget,ExecutorClient>(executor_plugin);
//	}
//	
//	public static ProxyBuilder<ResultSetClient> resultset(String endpointId) {
//		LegacyQuery query = new LegacyQuery(resultset_plugin);
//		query.addCondition("$resource/ID/string() eq '"+endpointId+"'"); 
//		return new ProxyBuilderImpl<WebTarget,ResultSetClient>(resultset_plugin, query);
//	}
	
	
	public final String name;

	public SDIAbstractPlugin(String name) {
		this.name = name;
	}
	
	@Override
	public String serviceClass() {
		return ServiceConstants.SERVICE_CLASS;
	}
	@Override
	public String serviceName() {
		return ServiceConstants.SERVICE_NAME;
	}
	@Override
	public String name() {
		return name;
	}
	@Override
	public String namespace() {
		return ServiceConstants.NAMESPACE;
	}
}

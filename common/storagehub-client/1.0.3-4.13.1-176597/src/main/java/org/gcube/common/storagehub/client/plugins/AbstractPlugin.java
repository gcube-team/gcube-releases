package org.gcube.common.storagehub.client.plugins;

import javax.ws.rs.client.WebTarget;

import org.gcube.common.clients.Plugin;
import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.common.gxrest.request.GXWebTargetAdapterRequest;
import org.gcube.common.storagehub.client.Constants;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;



public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	
	
	private static final ItemManagerPlugin item_plugin = new ItemManagerPlugin();
	private static final WorkspaceManagerPlugin workspace_plugin = new WorkspaceManagerPlugin();
	
	
	public static ProxyBuilder<ItemManagerClient> item() {
	    return new ProxyBuilderImpl<GXWebTargetAdapterRequest,ItemManagerClient>(item_plugin);
	}
	
	public static ProxyBuilder<WorkspaceManagerClient> workspace() {
	    return new ProxyBuilderImpl<GXWebTargetAdapterRequest,WorkspaceManagerClient>(workspace_plugin);
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
package org.gcube.common.vremanagement.whnmanager.client.plugins;

import org.gcube.common.clients.Plugin;
import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.common.vremanagement.whnmanager.client.Constants;
import org.gcube.common.vremanagement.whnmanager.client.proxies.WHNManagerProxy;
import org.gcube.resourcemanagement.whnmanager.api.WhnManager;


public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	private static final WHNManagerServicePlugin whnmanager_plugin = new WHNManagerServicePlugin();
	
	public static ProxyBuilder<WHNManagerProxy> whnmanager() {
		return new ProxyBuilderImpl<WhnManager,WHNManagerProxy>(whnmanager_plugin);
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

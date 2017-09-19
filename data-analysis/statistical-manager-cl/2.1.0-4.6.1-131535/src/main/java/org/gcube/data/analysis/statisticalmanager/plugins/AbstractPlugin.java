package org.gcube.data.analysis.statisticalmanager.plugins;

import org.gcube.common.clients.fw.plugin.Plugin;




public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {
	
	private static final String SERVICE_CLASS = "DataAnalysis";
	private static final String SERVICE_NAME = "statistical-manager-gcubews";
	private static final String NAMESPACE = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";
	
	public final String name;
	
	public AbstractPlugin(String name) {
		this.name = name;
	}
	
	@Override
	public String serviceClass() {
		return SERVICE_CLASS;
	}
	
	@Override
	public String serviceName() {
		return SERVICE_NAME;
	}
	
	@Override
	public String namespace() {
		return NAMESPACE;
	}
	
	@Override
	public String name() {
		return name;
	}
	
}

package org.gcube.common.authorization.client.plugin;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.clients.Plugin;



public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

		
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
	public String name() {
		return name;
	}

	@Override
	public String namespace() {
		return "";
	}
		
}
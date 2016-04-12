package org.gcube.common.authorization.client.plugin;


import javax.xml.ws.EndpointReference;

import org.gcube.common.authorization.client.JaxRSEndpointReference;
import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.client.proxy.DefaultAuthorizationProxy;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;

public class AuthorizationPlugin extends AbstractPlugin<String, AuthorizationProxy>{
	
	public AuthorizationPlugin() {
		super("authorization-service/gcube/service");
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public String resolve(EndpointReference address, ProxyConfig<?, ?> config)
			throws Exception {
		return new JaxRSEndpointReference(address).toString();
		
	}

	@Override
	public AuthorizationProxy newProxy(ProxyDelegate<String> delegate) {
		return new DefaultAuthorizationProxy(delegate);
	}

}

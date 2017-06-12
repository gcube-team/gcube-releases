package org.gcube.common.vremanagement.whnmanager.client.plugins;


import static org.gcube.common.calls.jaxws.StubFactory.stubFor;
import static org.gcube.common.vremanagement.whnmanager.client.Constants.whnManager;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.vremanagement.whnmanager.client.Constants;
import org.gcube.common.vremanagement.whnmanager.client.proxies.DefaultWHNManagerProxy;
import org.gcube.common.vremanagement.whnmanager.client.proxies.WHNManagerProxy;
import org.gcube.resourcemanagement.whnmanager.api.WhnManager;


/**
 * 
 * @author roberto cirillo (CNR - ISTI)
 *
 */
public class WHNManagerServicePlugin  extends AbstractPlugin<WhnManager,WHNManagerProxy> {

	public WHNManagerServicePlugin() {
		super(Constants.CONTEX_SERVICE_NAME+"/"+WhnManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public WHNManagerProxy newProxy(ProxyDelegate<WhnManager> delegate) {
		return new DefaultWHNManagerProxy(delegate);
	}

	@Override
	public WhnManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(whnManager).at(address);
	}
	
	
}
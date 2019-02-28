package org.gcube.vremanagement.resourcemanager.client.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.vremanagement.resourcemanager.client.Constants.NAMESPACE_BINDER;
import static org.gcube.vremanagement.resourcemanager.client.Constants.PORT_TYPE_NAME_BINDER;
import static org.gcube.vremanagement.resourcemanager.client.Constants.rm_binder;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.resourcemanager.client.RMBinderLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.RMBinderServiceJAXWSStubs;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class BinderPlugin extends AbstractPlugin<RMBinderServiceJAXWSStubs,RMBinderLibrary> {

	public BinderPlugin() {
		super(PORT_TYPE_NAME_BINDER);
	}
	
	public RMBinderServiceJAXWSStubs resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(rm_binder).at(reference);
	}

	
	public RMBinderLibrary newProxy(ProxyDelegate<RMBinderServiceJAXWSStubs> delegate) {
		return new RMBinderLibrary(delegate);
	}

	@Override
	public String namespace() {
		return NAMESPACE_BINDER;
	}
}

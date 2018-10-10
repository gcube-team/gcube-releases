package org.gcube.vremanagement.resourcemanager.client.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

import static org.gcube.vremanagement.resourcemanager.client.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.resourcemanager.client.RMControllerLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.*;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class ControllerPlugin extends AbstractPlugin<RMControllerServiceJAXWSStubs,RMControllerLibrary> {

	public ControllerPlugin() {
		super(PORT_TYPE_NAME_CONTROLLER);
	}
	
	public RMControllerServiceJAXWSStubs resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(rm_controller).at(reference);
	}

	
	public RMControllerLibrary newProxy(ProxyDelegate<RMControllerServiceJAXWSStubs> delegate) {
		return new RMControllerLibrary(delegate);
	}

	@Override
	public String namespace() {
		return NAMESPACE_CONTROLLER;
	}
}

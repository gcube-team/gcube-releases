package org.gcube.vremanagement.softwaregateway.client.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.vremanagement.softwaregateway.client.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.softwaregateway.client.SGRegistrationLibrary;
import org.gcube.vremanagement.softwaregateway.client.fws.SGRegistrationServiceJAXWSStubs;

/**
 * 
 * @author Roberto Cirillo (ISTI -CNR)
 *
 */
public class RegistrationPlugin extends AbstractPlugin<SGRegistrationServiceJAXWSStubs,SGRegistrationLibrary> {

	public RegistrationPlugin() {
		super(PORT_TYPE_NAME_REGISTRATION);
	}
	
	public SGRegistrationServiceJAXWSStubs resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(sg_registration).at(reference);
	}

	
	public SGRegistrationLibrary newProxy(ProxyDelegate<SGRegistrationServiceJAXWSStubs> delegate) {
		return new SGRegistrationLibrary(delegate);
	}

	@Override
	public String namespace() {
		return NAMESPACE_REGISTRATION;
	}
}


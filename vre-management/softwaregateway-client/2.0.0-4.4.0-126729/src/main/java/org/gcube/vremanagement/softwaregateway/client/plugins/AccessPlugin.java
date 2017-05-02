package org.gcube.vremanagement.softwaregateway.client.plugins;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.softwaregateway.client.SGAccessLibrary;
import org.gcube.vremanagement.softwaregateway.client.fws.SGAccessServiceJAXWSStubs;
import static org.gcube.vremanagement.softwaregateway.client.Constants.*;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;


/**
 * 
 * @author Roberto Cirillo (ISTI -CNR)
 *
 */
public class AccessPlugin extends AbstractPlugin<SGAccessServiceJAXWSStubs,SGAccessLibrary> {

	public AccessPlugin() {
		super(PORT_TYPE_NAME_ACCESS);
	}
	
	public SGAccessServiceJAXWSStubs resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(sg_access).at(reference);
	}

	
	public SGAccessLibrary newProxy(ProxyDelegate<SGAccessServiceJAXWSStubs> delegate) {
		return new SGAccessLibrary(delegate);
	}

	@Override
	public String namespace() {
		return NAMESPACE_ACCESS;
	}
}


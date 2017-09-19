package org.gcube.vremanagement.resourcemanager.client.plugins;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.resourcemanager.client.Constants;
import org.gcube.vremanagement.resourcemanager.client.RMAdminLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.RMAdminServiceJAXWSStubs;
import static org.gcube.vremanagement.resourcemanager.client.Constants.*;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class AdminPlugin extends AbstractPlugin<RMAdminServiceJAXWSStubs,RMAdminLibrary> {

	public AdminPlugin() {
		super(PORT_TYPE_NAME_ADMIN);
	}
	
	public RMAdminServiceJAXWSStubs resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(Constants.rm_admin).at(reference);
	}

	
	public RMAdminLibrary newProxy(ProxyDelegate<RMAdminServiceJAXWSStubs> delegate) {
		return new RMAdminLibrary(delegate);
	}

	@Override
	public String namespace() {
		return NAMESPACE_ADMIN;
	}
}


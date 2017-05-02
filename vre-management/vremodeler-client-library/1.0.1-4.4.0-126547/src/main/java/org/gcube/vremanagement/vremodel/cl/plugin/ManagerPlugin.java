package org.gcube.vremanagement.vremodel.cl.plugin;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import javax.xml.ws.EndpointReference;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import static org.gcube.vremanagement.vremodel.cl.Constants.*;

import org.gcube.vremanagement.vremodel.cl.proxy.DefaultManager;
import org.gcube.vremanagement.vremodel.cl.proxy.Manager;
import org.gcube.vremanagement.vremodel.cl.stubs.ManagerStub;

public class ManagerPlugin extends AbstractPlugin<ManagerStub, Manager> {

		
	public ManagerPlugin() {
		super("gcube/vremanagement/vremodeler/ModelerService");
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public Manager newProxy(ProxyDelegate<ManagerStub> delegate) {
		return new DefaultManager(delegate);
	}

	@Override
	public ManagerStub resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(manager).at(address);
	}



	
}

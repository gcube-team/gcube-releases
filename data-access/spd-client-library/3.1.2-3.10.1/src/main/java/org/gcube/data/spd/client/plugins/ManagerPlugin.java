package org.gcube.data.spd.client.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import javax.xml.ws.EndpointReference;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.proxies.DefaultManager;
import org.gcube.data.spd.client.proxies.Manager;
import static org.gcube.data.spd.client.Constants.*;
import org.gcube.data.spd.stubs.ManagerStub;

public class ManagerPlugin extends AbstractPlugin<ManagerStub, Manager> {

		
	public ManagerPlugin() {
		super("gcube/data/speciesproductsdiscovery/manager");
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

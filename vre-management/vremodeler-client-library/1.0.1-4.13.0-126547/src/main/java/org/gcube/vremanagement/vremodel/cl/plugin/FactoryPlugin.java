package org.gcube.vremanagement.vremodel.cl.plugin;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.vremodel.cl.proxy.DefaultFactory;
import org.gcube.vremanagement.vremodel.cl.proxy.Factory;
import org.gcube.vremanagement.vremodel.cl.stubs.FactoryStub;

import static org.gcube.vremanagement.vremodel.cl.Constants.*;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

public class FactoryPlugin extends AbstractPlugin<FactoryStub, Factory> {

	public FactoryPlugin(){
		super("gcube/vremanagement/vremodeler/ModelerFactoryService");
	}
	
	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public FactoryStub resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(factory).at(address);
	}

	@Override
	public Factory newProxy(ProxyDelegate<FactoryStub> delegate) {
		return new DefaultFactory(delegate);
	}

	

	

}

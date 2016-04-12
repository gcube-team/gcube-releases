package org.gcube.data.spd.client.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.data.spd.client.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.proxies.Classification;
import org.gcube.data.spd.client.proxies.DefaultClassification;
import org.gcube.data.spd.stubs.ClassificationStub;


public class ClassificationPlugin extends AbstractPlugin<ClassificationStub, Classification> {

	public ClassificationPlugin() {
		super("gcube/data/speciesproductsdiscovery/classification");
	}

	@Override
	public ClassificationStub resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(classification).at(address);
	}

	@Override
	public Classification newProxy(ProxyDelegate<ClassificationStub> delegate) {
		return new DefaultClassification(delegate);
	}
	
	
	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	
}

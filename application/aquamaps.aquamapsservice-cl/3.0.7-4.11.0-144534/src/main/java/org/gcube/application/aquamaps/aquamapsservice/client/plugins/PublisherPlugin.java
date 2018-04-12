package org.gcube.application.aquamaps.aquamapsservice.client.plugins;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.pubService;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

import javax.xml.ws.EndpointReference;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DefaultPublisher;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.PublisherStub;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;


public class PublisherPlugin extends AbstractPlugin<PublisherStub,Publisher> {

	public PublisherPlugin() {
		super(Constants.NAMESPACE+"/PublisherService");
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public Publisher newProxy(ProxyDelegate<PublisherStub> delegate) {
		return new DefaultPublisher(delegate);
	}


	@Override
	public PublisherStub resolve(EndpointReference arg0, ProxyConfig<?, ?> arg1)
			throws Exception {
		return stubFor(pubService).at(arg0);
	}

}

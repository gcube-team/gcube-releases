package org.gcube.application.aquamaps.aquamapsservice.client.plugins;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aqService;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

import javax.xml.ws.EndpointReference;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DefaultMaps;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Maps;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.MapsStub;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;

public class MapsPlugin extends AbstractPlugin<MapsStub,Maps> {

	public MapsPlugin() {
		super(Constants.NAMESPACE+"/AquaMapsService");
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public Maps newProxy(ProxyDelegate<MapsStub> delegate) {
		return new DefaultMaps(delegate);
	}

	@Override
	public MapsStub resolve(EndpointReference arg0, ProxyConfig<?, ?> arg1)
			throws Exception {
	return stubFor(aqService).at(arg0);
	}


}

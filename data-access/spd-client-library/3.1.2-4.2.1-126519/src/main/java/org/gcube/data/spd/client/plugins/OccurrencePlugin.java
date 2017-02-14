package org.gcube.data.spd.client.plugins;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.proxies.DefaultOccurrence;
import org.gcube.data.spd.client.proxies.Occurrence;
import org.gcube.data.spd.stubs.OccurrenceStub;
import static org.gcube.data.spd.client.Constants.*;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

public class OccurrencePlugin extends AbstractPlugin<OccurrenceStub, Occurrence> {

	public OccurrencePlugin(){
		super("gcube/data/speciesproductsdiscovery/occurrences");
	}
	
	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public OccurrenceStub resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(occurrence).at(address);
	}

	@Override
	public Occurrence newProxy(ProxyDelegate<OccurrenceStub> delegate) {
		return new DefaultOccurrence(delegate);
	}

	

	

}

package org.gcube.data.tml.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.data.tml.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.tml.Constants;
import org.gcube.data.tml.proxies.DefaultTWriter;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.tml.stubs.TWriterStub;

/**
 * Extends {@link AbstractPlugin} for the T-Writer service.
 * 
 * @author Fabio Simeoni
 *
 */
public class TWriterPlugin extends AbstractPlugin<TWriterStub,TWriter> {

	public TWriterPlugin() {
		super(writerWSDDName);
	}
	
	@Override
	public TWriterStub resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(Constants.writer).at(reference);
		
	}
	
	@Override
	public TWriter newProxy(ProxyDelegate<TWriterStub> delegate) {
		return new DefaultTWriter(delegate);
	}
}

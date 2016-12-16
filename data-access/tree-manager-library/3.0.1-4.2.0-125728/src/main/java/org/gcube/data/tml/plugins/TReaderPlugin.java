package org.gcube.data.tml.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.data.tml.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.tml.Constants;
import org.gcube.data.tml.proxies.DefaultTReader;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.stubs.TReaderStub;

/**
 * Extends {@link AbstractPlugin} for the T-Reader service.
 * 
 * @author Fabio Simeoni
 *
 */
public class TReaderPlugin extends AbstractPlugin<TReaderStub,TReader> {

	public TReaderPlugin() {
		super(readerWSDDName);
	}
	
	public TReaderStub resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(Constants.reader).at(reference);
		
	}
	
	@Override
	public TReader newProxy(ProxyDelegate<TReaderStub> delegate) {
		return new DefaultTReader(delegate);
	}
}

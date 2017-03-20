package org.gcube.data.tml.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.data.tml.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.tml.Constants;
import org.gcube.data.tml.proxies.DefaultTBinder;
import org.gcube.data.tml.stubs.TBinderStub;

/**
 * Extends {@link AbstractPlugin} for the T-Binder service.
 * 
 * @author Fabio Simeoni
 *
 */
public class TBinderPlugin extends AbstractPlugin<TBinderStub,DefaultTBinder> {

	public TBinderPlugin() {
		super(binderWSDDName);
	}
	
	public TBinderStub resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(Constants.binder).at(reference);
	}

	
	public DefaultTBinder newProxy(ProxyDelegate<TBinderStub> delegate) {
		return new DefaultTBinder(delegate);
	}
}

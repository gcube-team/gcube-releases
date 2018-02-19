package org.gcube.common.vremanagement.ghnmanager.client.plugins;


import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.common.vremanagement.ghnmanager.client.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.vremanagement.ghnmanager.client.Constants;
import org.gcube.common.vremanagement.ghnmanager.client.GHNManagerLibrary;
import org.gcube.common.vremanagement.ghnmanager.client.fws.GHNManagerServiceJAXWSStubs;


/**
 * 
 * @author andrea
 *
 */
public class GHNManagerServicePlugin  extends AbstractPlugin<GHNManagerServiceJAXWSStubs,GHNManagerLibrary> {

	public GHNManagerServicePlugin() {
		super(Constants.PORT_TYPE_NAME);
	}
	
	
	
	public GHNManagerServiceJAXWSStubs resolve(EndpointReference address,ProxyConfig<?,?> config) throws Exception {
		return stubFor(ghnmanager).at(address);
		
	}
	
	@Override
	public GHNManagerLibrary newProxy(ProxyDelegate<GHNManagerServiceJAXWSStubs> delegate) {
		return new GHNManagerLibrary(delegate);
	}



	@Override
	public Exception convert(Exception arg0, ProxyConfig<?, ?> arg1) {
		return arg0;
	}

}
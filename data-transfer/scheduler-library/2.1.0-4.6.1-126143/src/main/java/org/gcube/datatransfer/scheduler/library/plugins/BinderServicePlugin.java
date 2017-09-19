package org.gcube.datatransfer.scheduler.library.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.datatransfer.scheduler.library.fws.Constants.binder;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.datatransfer.scheduler.library.BinderLibrary;
import org.gcube.datatransfer.scheduler.library.fws.BinderServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.utils.Constants;

public class BinderServicePlugin extends BinderAbstractPlugin<BinderServiceJAXWSStubs,BinderLibrary> {

	public BinderServicePlugin() {
		super(Constants.FACTORY_PORT_TYPE_NAME);
	}
	
	
	
	public BinderServiceJAXWSStubs resolve(EndpointReference address,ProxyConfig<?,?> config) throws Exception {
		return stubFor(binder).at(address);
		
	}
	
	@Override
	public BinderLibrary newProxy(ProxyDelegate<BinderServiceJAXWSStubs> delegate) {
		return new BinderLibrary(delegate);
	}



	@Override
	public Exception convert(Exception arg0, ProxyConfig<?, ?> arg1) {
		return arg0;
	}



}
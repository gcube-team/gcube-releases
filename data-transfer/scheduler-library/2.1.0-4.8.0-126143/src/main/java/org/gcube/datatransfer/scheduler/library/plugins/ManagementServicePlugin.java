package org.gcube.datatransfer.scheduler.library.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.datatransfer.scheduler.library.fws.Constants.management;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.datatransfer.scheduler.library.ManagementLibrary;
import org.gcube.datatransfer.scheduler.library.fws.ManagementServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.utils.Constants;

public class ManagementServicePlugin extends ManagementAbstractPlugin<ManagementServiceJAXWSStubs,ManagementLibrary> {

	public ManagementServicePlugin() {
		super(Constants.MANAGEMENT_PORT_TYPE_NAME);
	}
	
	
	
	public ManagementServiceJAXWSStubs resolve(EndpointReference address,ProxyConfig<?,?> config) throws Exception {
		return stubFor(management).at(address);
		
	}
	
	@Override
	public ManagementLibrary newProxy(ProxyDelegate<ManagementServiceJAXWSStubs> delegate) {
		return new ManagementLibrary(delegate);
	}



	@Override
	public Exception convert(Exception arg0, ProxyConfig<?, ?> arg1) {
		return arg0;
	}



}
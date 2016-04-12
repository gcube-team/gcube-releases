package org.gcube.datatransfer.scheduler.library.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.datatransfer.scheduler.library.fws.Constants.scheduler;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.datatransfer.scheduler.library.SchedulerLibrary;
import org.gcube.datatransfer.scheduler.library.fws.SchedulerServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.utils.Constants;

public class SchedulerServicePlugin extends SchedulerAbstractPlugin<SchedulerServiceJAXWSStubs,SchedulerLibrary> {

	public SchedulerServicePlugin() {
		super(Constants.SCHEDULER_PORT_TYPE_NAME);
	}
	
	
	
	public SchedulerServiceJAXWSStubs resolve(EndpointReference address,ProxyConfig<?,?> config) throws Exception {
		return stubFor(scheduler).at(address);
		
	}
	
	@Override
	public SchedulerLibrary newProxy(ProxyDelegate<SchedulerServiceJAXWSStubs> delegate) {
		return new SchedulerLibrary(delegate);
	}



	@Override
	public Exception convert(Exception arg0, ProxyConfig<?, ?> arg1) {
		return arg0;
	}



}
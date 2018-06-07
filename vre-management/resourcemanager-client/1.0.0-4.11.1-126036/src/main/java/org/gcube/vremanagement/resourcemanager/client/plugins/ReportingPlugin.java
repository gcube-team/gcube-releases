package org.gcube.vremanagement.resourcemanager.client.plugins;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.vremanagement.resourcemanager.client.Constants.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.resourcemanager.client.RMReportingLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.RMReportingServiceJAXWSStubs;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class ReportingPlugin extends AbstractPlugin<RMReportingServiceJAXWSStubs,RMReportingLibrary> {

	public ReportingPlugin() {
		super(PORT_TYPE_NAME_REPORTING);
	}
	
	public RMReportingServiceJAXWSStubs resolve(EndpointReference reference,ProxyConfig<?,?> config) throws Exception {
		return stubFor(rm_reporting).at(reference);
	}

	
	public RMReportingLibrary newProxy(ProxyDelegate<RMReportingServiceJAXWSStubs> delegate) {
		return new RMReportingLibrary(delegate);
	}

	@Override
	public String namespace() {
		return NAMESPACE_REPORTING;
	}
}


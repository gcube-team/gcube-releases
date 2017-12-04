package org.gcube.application.aquamaps.aquamapsservice.client.plugins;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.dmService;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

import javax.xml.ws.EndpointReference;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DefaultDataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.DataManagementStub;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;

public class DataManagementPlugin extends AbstractPlugin<DataManagementStub,DataManagement> {

	public DataManagementPlugin() {
		super(Constants.NAMESPACE+"/DataManagement");
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public DataManagement newProxy(ProxyDelegate<DataManagementStub> delegate) {
		return new DefaultDataManagement(delegate);
	}

	@Override
	public DataManagementStub resolve(EndpointReference arg0,
			ProxyConfig<?, ?> arg1) throws Exception {
		return stubFor(dmService).at(arg0);
	}
	
}

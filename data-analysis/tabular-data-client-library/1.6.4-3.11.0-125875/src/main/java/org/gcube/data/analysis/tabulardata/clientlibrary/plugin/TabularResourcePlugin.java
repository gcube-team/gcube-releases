package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultTabularResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TabularResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;

import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.tabularResourceManager;
import static org.gcube.common.calls.jaxws.StubFactory.stubFor;

public class TabularResourcePlugin extends AbstractPlugin<TabularResourceManager, TabularResourceManagerProxy> {

	public TabularResourcePlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+TabularResourceManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public TabularResourceManagerProxy newProxy(ProxyDelegate<TabularResourceManager> delegate) {
		return new DefaultTabularResourceManagerProxy(delegate);
	}

	@Override
	public TabularResourceManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(tabularResourceManager).at(address);
	}
	
	
	
}

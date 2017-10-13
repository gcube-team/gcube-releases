package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import static org.gcube.common.calls.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.externalResourceManager;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultExternalResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.ExternalResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.ExternalResourceManager;


public class ExternalResourcePlugin extends AbstractPlugin<ExternalResourceManager, ExternalResourceManagerProxy>{

	public ExternalResourcePlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+ExternalResourceManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public ExternalResourceManagerProxy newProxy(ProxyDelegate<ExternalResourceManager> delegate) {
		return new DefaultExternalResourceManagerProxy(delegate);
	}

	@Override
	public ExternalResourceManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(externalResourceManager).at(address);
	}
	
}

package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import static org.gcube.common.calls.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.operationManager;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultOperationManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.OperationManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;

public class OperationPlugin extends AbstractPlugin<OperationManager, OperationManagerProxy> {

	public OperationPlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+OperationManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public OperationManagerProxy newProxy(ProxyDelegate<OperationManager> delegate) {
		return new DefaultOperationManagerProxy(delegate);
	}

	@Override
	public OperationManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(operationManager).at(address);
	}
	
}

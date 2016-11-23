package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultHistoryManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.HistoryManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager;

import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.historyManager;
import static org.gcube.common.calls.jaxws.StubFactory.stubFor;

public class HistoryPlugin extends AbstractPlugin<HistoryManager, HistoryManagerProxy> {

	public HistoryPlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+HistoryManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public HistoryManagerProxy newProxy(ProxyDelegate<HistoryManager> delegate) {
		return new DefaultHistoryManagerProxy(delegate);
	}

	@Override
	public HistoryManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(historyManager).at(address);
	}
	
	
	
}


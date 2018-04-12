package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import static org.gcube.common.calls.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.queryManager;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultQueryManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.QueryManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.QueryManager;

public class QueryPlugin extends AbstractPlugin<QueryManager, QueryManagerProxy> {

	public QueryPlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+QueryManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public QueryManagerProxy newProxy(ProxyDelegate<QueryManager> delegate) {
		return new DefaultQueryManagerProxy(delegate);
	}

	@Override
	public QueryManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(queryManager).at(address);
	}
	
	
	
}

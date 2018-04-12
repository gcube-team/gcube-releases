package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import static org.gcube.common.calls.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.ruleManager;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultRuleManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.RuleManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.RuleManager;

public class RulePlugin extends AbstractPlugin<RuleManager, RuleManagerProxy> {

	public RulePlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+RuleManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public RuleManagerProxy newProxy(ProxyDelegate<RuleManager> delegate) {
		return new DefaultRuleManagerProxy(delegate);
	}

	@Override
	public RuleManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(ruleManager).at(address);
	}
}
package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import static org.gcube.common.calls.jaxws.StubFactory.stubFor;
import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.templateManager;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import javax.xml.ws.EndpointReference;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultTemplateManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TemplateManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager;

public class TemplatePlugin extends AbstractPlugin<TemplateManager, TemplateManagerProxy> {

	public TemplatePlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+TemplateManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public TemplateManagerProxy newProxy(ProxyDelegate<TemplateManager> delegate) {
		return new DefaultTemplateManagerProxy(delegate);
	}

	@Override
	public TemplateManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(templateManager).at(address);
	}
}

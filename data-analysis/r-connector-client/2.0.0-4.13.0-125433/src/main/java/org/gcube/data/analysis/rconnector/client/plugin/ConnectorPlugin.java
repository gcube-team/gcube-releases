package org.gcube.data.analysis.rconnector.client.plugin;

import static org.gcube.data.analysis.rconnector.client.GcubeService.service;
import static org.gcube.data.analysis.rconnector.client.TargetFactory.stubFor;

import javax.ws.rs.client.WebTarget;
import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.rconnector.client.Constants;
import org.gcube.data.analysis.rconnector.client.GcubeService;
import org.gcube.data.analysis.rconnector.client.proxy.ConnectorProxy;
import org.gcube.data.analysis.rconnector.client.proxy.DefaultConnectorProxy;

public class ConnectorPlugin extends AbstractPlugin<WebTarget, ConnectorProxy>{
	
	public ConnectorPlugin() {
		super("r-connector/gcube/service");
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public WebTarget resolve(EndpointReference address, ProxyConfig<?, ?> config)
			throws Exception {
		GcubeService service = service().withName(Constants.CONNECTOR_QNAME).useRootPath();
		return stubFor(service).at(address);
		
	}

	@Override
	public ConnectorProxy newProxy(ProxyDelegate<WebTarget> delegate) {
		return new DefaultConnectorProxy(delegate);
	}

}

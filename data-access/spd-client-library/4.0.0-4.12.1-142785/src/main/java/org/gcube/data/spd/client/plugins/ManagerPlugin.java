package org.gcube.data.spd.client.plugins;

import javax.ws.rs.client.WebTarget;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;

import org.gcube.common.calls.jaxrs.GcubeService;
import org.gcube.common.calls.jaxrs.TargetFactory;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.Constants;
import org.gcube.data.spd.client.proxies.DefaultManager;
import org.gcube.data.spd.client.proxies.ManagerClient;
import org.w3c.dom.Node;

public class ManagerPlugin extends AbstractPlugin<WebTarget, ManagerClient> {

	public ManagerPlugin() {
		super("species-products-discovery/gcube/service");
	}

	@Override
	public Exception convert(Exception e, ProxyConfig<?, ?> arg1) {
		return e;
	}

	@Override
	public ManagerClient newProxy(ProxyDelegate<WebTarget> delegate) {
		return new DefaultManager(delegate);
	}

	@Override
	public WebTarget resolve(EndpointReference epr, ProxyConfig<?, ?> config)
			throws Exception {
		DOMResult result = new DOMResult();
		epr.writeTo(result);
		Node node =result.getNode();
		Node child=node.getFirstChild();
		String address = child.getTextContent();
		GcubeService service = GcubeService.service().withName(Constants.MANAGER_QNAME).andPath("retrieve");
		return TargetFactory.stubFor(service).at(address);
				
	}
	
}

package org.gcube.common.storagehub.client.plugins;

import javax.ws.rs.client.WebTarget;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;

import org.gcube.common.calls.jaxrs.GcubeService;
import org.gcube.common.calls.jaxrs.TargetFactory;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.storagehub.client.Constants;
import org.gcube.common.storagehub.client.proxies.DefaultItemManager;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.w3c.dom.Node;

public class ItemManagerPlugin extends AbstractPlugin<WebTarget, ItemManagerClient> {

	public ItemManagerPlugin() {
		super("storagehub/workspace");
	}

	@Override
	public Exception convert(Exception e, ProxyConfig<?, ?> arg1) {
		return e;
	}

	@Override
	public ItemManagerClient newProxy(ProxyDelegate<WebTarget> delegate) {
		return new DefaultItemManager(delegate);
	}

	@Override
	public WebTarget resolve(EndpointReference epr, ProxyConfig<?, ?> config)
			throws Exception {
		DOMResult result = new DOMResult();
		epr.writeTo(result);
		Node node =result.getNode();
		Node child=node.getFirstChild();
		String address = child.getTextContent();
		GcubeService service = GcubeService.service().withName(Constants.MANAGER_QNAME).andPath("items");
		return TargetFactory.stubFor(service).at(address);
				
	}
	
}

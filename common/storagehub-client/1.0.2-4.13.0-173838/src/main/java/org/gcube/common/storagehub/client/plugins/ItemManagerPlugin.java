package org.gcube.common.storagehub.client.plugins;

import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;

import org.gcube.common.calls.jaxrs.GcubeService;
import org.gcube.common.calls.jaxrs.TargetFactory;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.gxrest.request.GXWebTargetAdapterRequest;
import org.gcube.common.storagehub.client.Constants;
import org.gcube.common.storagehub.client.proxies.DefaultItemManager;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.w3c.dom.Node;

public class ItemManagerPlugin extends AbstractPlugin<GXWebTargetAdapterRequest, ItemManagerClient> {

	public ItemManagerPlugin() {
		super("storagehub/workspace");
	}

	@Override
	public Exception convert(Exception e, ProxyConfig<?, ?> arg1) {
		return e;
	}

	@Override
	public ItemManagerClient newProxy(ProxyDelegate<GXWebTargetAdapterRequest> delegate) {
		return new DefaultItemManager(delegate);
	}

	@Override
	public GXWebTargetAdapterRequest resolve(EndpointReference epr, ProxyConfig<?, ?> config)
			throws Exception {
		DOMResult result = new DOMResult();
		epr.writeTo(result);
		Node node =result.getNode();
		Node child=node.getFirstChild();
		String address = child.getTextContent();
		//GXWebTargetAdapterRequest request = GXWebTargetAdapterRequest.newRequest(address).path(this.name).path("items");
		GcubeService service = GcubeService.service().withName(Constants.MANAGER_QNAME).andPath("items");
		return TargetFactory.stubFor(service).getAsGxRest(address);
				
	}
	
}

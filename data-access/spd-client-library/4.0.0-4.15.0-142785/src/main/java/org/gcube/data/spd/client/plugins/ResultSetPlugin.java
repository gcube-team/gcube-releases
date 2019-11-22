package org.gcube.data.spd.client.plugins;

import javax.ws.rs.client.WebTarget;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;

import org.gcube.common.calls.jaxrs.GcubeService;
import org.gcube.common.calls.jaxrs.TargetFactory;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.Constants;
import org.gcube.data.spd.client.proxies.DefaultResultSet;
import org.gcube.data.spd.client.proxies.ResultSetClient;
import org.w3c.dom.Node;

public class ResultSetPlugin extends AbstractPlugin<WebTarget, ResultSetClient> {

	public ResultSetPlugin(){
		super("species-products-discovery/gcube/service");
	}

	@Override
	public Exception convert(Exception e, ProxyConfig<?, ?> arg1) {
		return e;
	}

	@Override
	public ResultSetClient newProxy(ProxyDelegate<WebTarget> delegate) {
		return new DefaultResultSet(delegate);
	}

	@Override
	public WebTarget resolve(EndpointReference epr, ProxyConfig<?, ?> config)
			throws Exception {
		DOMResult result = new DOMResult();
		epr.writeTo(result);
		Node node =result.getNode();
		Node child=node.getFirstChild();
		String address = child.getTextContent();
		GcubeService service = GcubeService.service().withName(Constants.RESULTSET_QNAME).andPath("resultset");
		return TargetFactory.stubFor(service).at(address);
	}
	
}

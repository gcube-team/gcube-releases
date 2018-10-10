package org.gcube.spatial.data.sdi.plugins;

import javax.ws.rs.client.WebTarget;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;

import org.gcube.common.calls.jaxrs.GcubeService;
import org.gcube.common.calls.jaxrs.TargetFactory;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.spatial.data.sdi.interfaces.Metadata;
import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.proxies.DefaultMetadata;
import org.w3c.dom.Node;

public class MetadataPlugin extends SDIAbstractPlugin<WebTarget, Metadata>{

	public MetadataPlugin() {
		super("sdi-service/gcube/service");
	}
	
	@Override
	public Exception convert(Exception arg0, ProxyConfig<?, ?> arg1) {
		return arg0;
	}
	@Override
	public Metadata newProxy(ProxyDelegate<WebTarget> arg0) {
		return new DefaultMetadata(arg0);
	}
	
	@Override
	public WebTarget resolve(EndpointReference epr, ProxyConfig<?, ?> arg1) throws Exception {
		DOMResult result = new DOMResult();
		epr.writeTo(result);
		Node node =result.getNode();
		Node child=node.getFirstChild();
		String address = child.getTextContent();
		GcubeService service = GcubeService.service().
				withName(new QName(ServiceConstants.NAMESPACE,ServiceConstants.Metadata.INTERFACE)).
				andPath(ServiceConstants.Metadata.INTERFACE);		
		return TargetFactory.stubFor(service).at(address);
				
	}
}

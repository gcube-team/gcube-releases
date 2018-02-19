package org.gcube.resources.federation.fhnmanager.cl.fwsimpl;

import static org.gcube.resources.federation.fhnmanager.api.Constants.SERVICE_API_NAME;

import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.resources.federation.fhnmanager.api.exception.ConnectorException;
import org.gcube.resources.federation.fhnmanager.api.exception.ISException;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;


public class FHNManagerClientPlugin  extends FHNManagerClientAbstractPlugin<WebTarget, FHNManagerClient> {

	private static Logger logger=LoggerFactory.getLogger(FHNManagerClientPlugin.class);
	
	private URL directUrl = null;
	
	public FHNManagerClientPlugin() {
		super(SERVICE_API_NAME);
	}
	
	public FHNManagerClientPlugin(URL directURL){
		super(SERVICE_API_NAME);
		this.directUrl = directURL;
	}

	public WebTarget resolve(EndpointReference epr, ProxyConfig<?, ?> arg1) throws Exception {

		if (directUrl == null) {
			DOMResult result = new DOMResult();
			epr.writeTo(result);
			Node node = result.getNode();
			Node child = node.getFirstChild();
			String address = child.getTextContent();
			logger.info("Endpoint resolved for FHNManagerService is \"" + address + "\"");
			this.directUrl = new URL(address);
		}

		Client client = ClientBuilder.newClient();
		client.register(ScopeTokenSetFilter.class);
		client.register(RESTClientFilter.class);
		client.register(new LoggingFilter());
		return client.target(this.directUrl.toString());
	}
	
	public String namespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public Exception convert(Exception arg0, ProxyConfig<?, ?> arg1) {
		//the Jersey client wraps the connection we throws in the RESTClientFilter in ProcessingException
		//so we need to get the wrapped exception here
		Throwable fhnException = arg0.getCause();
		
		if(fhnException instanceof ConnectorException || 
				fhnException instanceof ISException){
			return (Exception) fhnException;
		}
		
		return arg0;
	}

	public FHNManagerClient newProxy(ProxyDelegate<WebTarget> delegate) {
		return new FHNManagerClient(delegate);
	}

}

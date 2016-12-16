/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client.plugin;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.informationsystem.resourceregistry.Constants;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.client.proxy.ResourceRegistryClient;
import org.gcube.informationsystem.resourceregistry.client.proxy.ResourceRegistryClientImpl;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ResourceRegistryClientPlugin extends AbstractPlugin<EndpointReference, ResourceRegistryClient>{

	public ResourceRegistryClientPlugin(){
		super(Constants.SERVICE_ENTRY_NAME);
	}
	
	@Override
	public String namespace() {
		return null;
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		// The Jersey client wraps the exception. So we need to get the wrapped 
		// exception thrown by ResourceRegistry Service.
		
		Throwable throwable = fault.getCause();
		if(throwable != null && throwable instanceof ResourceRegistryException){
			return (Exception) throwable;
		}
		
		return fault;
	}

	@Override
	public EndpointReference resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return address;
	}

	@Override
	public ResourceRegistryClient newProxy(
			ProxyDelegate<EndpointReference> delegate) {
		return new ResourceRegistryClientImpl(delegate);
	}

	

}

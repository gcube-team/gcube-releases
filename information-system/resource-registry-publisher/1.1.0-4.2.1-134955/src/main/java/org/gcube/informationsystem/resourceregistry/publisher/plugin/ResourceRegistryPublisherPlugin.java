/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.publisher.plugin;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.informationsystem.resourceregistry.Constants;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.publisher.proxy.ResourceRegistryPublisher;
import org.gcube.informationsystem.resourceregistry.publisher.proxy.ResourceRegistryPublisherImpl;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ResourceRegistryPublisherPlugin extends AbstractPlugin<EndpointReference, ResourceRegistryPublisher>{

	public ResourceRegistryPublisherPlugin(){
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
	public ResourceRegistryPublisher newProxy(
			ProxyDelegate<EndpointReference> delegate) {
		return new ResourceRegistryPublisherImpl(delegate);
	}

	

}

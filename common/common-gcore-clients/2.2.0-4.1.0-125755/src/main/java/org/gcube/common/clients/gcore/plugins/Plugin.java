package org.gcube.common.clients.gcore.plugins;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.delegates.ProxyPlugin;

/**
 * A {@link ProxyPlugin} for gCore services
 * 
 * @author Fabio Simeoni
 *
 * @param <S> the type of service proxies
 * @param <P> the type of service stubs
 */
public interface Plugin<S,P> extends ProxyPlugin<EndpointReferenceType, S,P> {

	
	/**
	 * Returns the namespace of the service
	 * @return the namespace
	 */
	String namespace();
}

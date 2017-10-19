package org.gcube.common.clients.fw.plugin;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.delegates.ProxyPlugin;

/**
 * A {@link ProxyPlugin} for gCore services.
 * 
 * @author Fabio Simeoni
 *
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 */
public interface Plugin<S,P> extends ProxyPlugin<EndpointReference, S,P> {
	
	/**
	 * Returns the namespace of the service.
	 * @return the namespace
	 */
	String namespace();
}

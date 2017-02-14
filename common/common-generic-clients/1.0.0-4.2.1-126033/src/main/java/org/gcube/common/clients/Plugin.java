package org.gcube.common.clients;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.delegates.ProxyPlugin;

/**
 * Plugs into the framework to provide information about target endpoints.
 * 
 * @author Fabio Simeoni
 *
 * @param <S> the type of endpoint stubs
 * @param <P> the type of endpoint proxies
 */
public interface Plugin<S,P> extends ProxyPlugin<EndpointReference, S, P> {

}

package org.gcube.common.clients.config;

import org.gcube.common.clients.delegates.DirectDelegate;
import org.gcube.common.clients.delegates.ProxyPlugin;

/**
 * The configuration of a proxy created in direct mode.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of service addresses
 * @param <S> the type of service stubs
 * 
 * @see DirectDelegate
 */
public class EndpointConfig<A,S> extends AbstractConfig<A,S> {

	private final A address;
	
	public EndpointConfig(ProxyPlugin<A,S,?> plugin, A address) {
		super(plugin);
		this.address=address;
	}
	
	public A address() {
		return address;
	}
}

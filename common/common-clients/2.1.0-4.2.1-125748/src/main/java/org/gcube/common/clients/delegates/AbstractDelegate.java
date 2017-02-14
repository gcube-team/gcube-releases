package org.gcube.common.clients.delegates;

import org.gcube.common.clients.config.ProxyConfig;

/**
 * Partial implementation of {@link ProxyDelegate}s
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of service addresses
 * @param <S> the type of service stubs
 * @param <C> the type of {@link ProxyConfig} used by the delegate
 */
public abstract class AbstractDelegate<A,S,C extends ProxyConfig<A,S>> implements ProxyDelegate<S> {

	private final C config;
	
	/**
	 * Constructs an instance with a given configuration
	 * @param config the configuration
	 */
	public AbstractDelegate(C config) {
		this.config=config;
	}
	
	@Override
	public C config() {
		return config;
	}
	
	@Override
	public String toString() {
		return config().plugin().name()+"'s proxy";
	}
}

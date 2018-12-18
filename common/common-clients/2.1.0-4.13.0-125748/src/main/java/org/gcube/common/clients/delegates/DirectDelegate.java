package org.gcube.common.clients.delegates;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.config.EndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * A {@link ProxyDelegate} that sends {@link Call}s to service endpoints at known addresses.
 * <p>
 * This is a no-op {@link ProxyDelegate}, i.e. it executes the {@link Call} interface and converts its faults.
 * It exists to support uniform programming against the {@link ProxyDelegate} interface.   
 *  
 * @author Fabio Simeoni
 *
 * @param <A> the of service addresses
 * @param <S> the type of service proxies
 * 
 */
public final class DirectDelegate<A,S> extends AbstractDelegate<A,S,EndpointConfig<A,S>> implements ProxyDelegate<S> {

	private static Logger log = LoggerFactory.getLogger(DirectDelegate.class);
	
	/**
	 * Creates an instance with a {@link ProxyPlugin} and an endpoint address.
	 * @param plugin the plugin
	 * @param address the address
	 */
	public DirectDelegate(EndpointConfig<A,S> config) {
		super(config);
	}
	
	@Override
	public <V> V make(Call<S, V> call) throws Exception {
		
		ProxyPlugin<A,S,?> plugin = config().plugin();
		
		A address = config().address();
		
		log.info("calling {} @ {}",plugin.name(),address);
		
		S stub =null;
		try {
			stub = plugin.resolve(address,config());
		}
		catch(Exception e) {
			throw new IllegalStateException("could not resolve "+address,e);
		}
		
		try {
			return call.call(stub);
		}
		catch(Exception fault) {
			throw plugin.convert(fault,config());
		}
	}
	
}

package org.gcube.common.clients.delegates;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.config.ProxyConfig;


/**
 * Makes {@link Call}s to service endpoints on behalf a service proxy.
 * <p>
 * Delegates obtain the addresses of service endpoints according to some strategy, using information
 * found in their {@link ProxyConfig}.
 * 
 * @author Fabio Simeoni
 * 
 * @param <S> the type of service stubs
 * 
 * @see Call
 */
public interface ProxyDelegate<S> {

	/**
	 * Makes a {@link Call} to a given service endpoint.
	 * 
	 * @param call the call
	 * @param <V> the type of the value returned from the call
	 * @return the value returned from the call
	 * @throws Exception if the call fails
	 * 
	 */
	<V> V make(Call<S, V> call) throws Exception;
	
	
	/**
	 * Returns the configuration of the proxy.
	 * @return the configuration
	 */
	ProxyConfig<?,S> config();
}

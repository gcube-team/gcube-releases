package org.gcube.common.clients.delegates;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.config.ProxyConfig;




/**
 * Provides information to customise the strategy of a {@link ProxyDelegate}. 
 * 
 * @author Fabio Simeoni
 * 
 * @see Call
 * @see ProxyDelegate
 */
public interface ProxyPlugin<A,S,P> {

	/**
	 * Returns the name of the service.
	 * 
	 * @return the name
	 */
	String name();

	/**
	 * Returns the namespace of the service
	 * @return the namespace
	 */
	String namespace();
	
	/**
	 * Returns the gCube name of the service.
	 * @return the name
	 */
	String serviceClass();
	
	/**
	 * Returns the gCube class of the service.
	 * @return the class
	 */
	String serviceName();

	/**
	 * Converts a fault raised by a {@link Call} into a fault that can be recognised by {@link ProxyDelegate} clients.
	 * 
	 * @param fault the original fault
	 * @return the converted fault
	 */
	Exception convert(Exception fault, ProxyConfig<?,?> config);
	
	/**
	 * Returns a stub for a given service endpoint
	 * @param address the address of the endpoint
	 * @return the stub
	 * @throws Exception if the address cannot be resolved
	 */
	S resolve(A address, ProxyConfig<?,?> config) throws Exception;
	
	
	/**
	 * Returns a proxy with a given delegate
	 * @param delegate the delegate
	 * @return the proxy
	 */
	P newProxy(ProxyDelegate<S> delegate);
	
}

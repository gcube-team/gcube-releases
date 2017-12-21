package org.gcube.common.clients;

/**
 * A call to an endpoint of a given service.
 * 
 * <p>
 * 
 * Calls interact with service endpoints at addresses provided by clients.
 * 
 * @author Fabio Simeoni
 * 
 * @param <S> the type of service stubs
 * @param <R> the type of values returned from the call
 * 
 */
public interface Call<S, R> {

	/**
	 * Calls a given service endpoint.
	 * 
	 * @param address a proxy of the endpoint
	 * @return the value returned by the call
	 * @throws Exception if the call fails
	 */
	R call(S endpoint) throws Exception;
}

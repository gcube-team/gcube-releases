package org.gcube.common.clients.stubs.jaxws;

import java.net.URI;

import javax.xml.ws.EndpointReference;

/**
 * Simple DSL for the {@link StubFactory}
 * 
 * @author Fabio Simeoni
 *
 */
public interface StubFactoryDSL {

	/**
	 * Selects the address of the service endpoint or service instance.
	 * 
	 * @author Fabio Simeoni
	 *
	 * @param <T>
	 */
	interface AtClause<T> {
		
		/**
		 * Returns a stub for a service endpoint at a given address.
		 * @param address the address
		 * @return the stub
		 */
		T at(URI address);

		
		/**
		 * Returns a stub for a service endpoint or service instance at a given address.
		 * @param ref a reference to the endpoint or instance
		 * @return the stub
		 */
		T at(EndpointReference ref);
	}
}

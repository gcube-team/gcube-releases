package org.gcube.common.calls.jaxrs;

import javax.ws.rs.client.WebTarget;

import org.gcube.common.gxrest.request.GXWebTargetAdapterRequest;




/**
 * Simple DSL for the {@link StubFactory}
 * 
 * @author Fabio Simeoni
 *
 */
public interface TargetFactoryDSL {

	/**
	 * Selects the address of the service endpoint or service instance.
	 * 
	 * @author Fabio Simeoni
	 *
	 * @param <T>
	 */
	interface AtClause {
		
		/**
		 * Returns a stub for a service endpoint at a given address.
		 * @param address the address
		 * @return the stub
		 */
		GXWebTargetAdapterRequest getAsGxRest(String address);
		
		/**
		 * Returns a stub for a service endpoint at a given address.
		 * @param address the address
		 * @return the stub
		 */
		WebTarget at(String address);
		
	}
}

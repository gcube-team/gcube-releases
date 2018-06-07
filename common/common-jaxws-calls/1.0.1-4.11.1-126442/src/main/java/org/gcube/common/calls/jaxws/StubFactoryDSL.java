package org.gcube.common.calls.jaxws;


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
		T at(String address);
		
		
	}
}

package org.gcube.common.uri.ap;

/**
 * A {@link ScopedAuthorityProvider} that retrieves the authority in scope from the Information System.
 * 
 * @author Fabio Simeoni
 *
 */
public class ISAP implements ScopedAuthorityProvider {

	@Override
	public String authorityIn(String scope) {
		
		//@TODO
		//I would really want to wait for lighter-weight IS client APIs
		throw new RuntimeException("aint' ready yet"); 
	}
}

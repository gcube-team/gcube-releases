package org.gcube.common.uri.ap;

/**
 * Returns naming authorities for given scopes.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ScopedAuthorityProvider {

	/**
	 * Returns the naming authority associated a given scope.
	 * @param scope the scope
	 * @return the authority
	 * @throws IllegalArgumentException if the scope is invalid
	 * @throws IllegalStateException if there is no authority in the given scope
	 */
	String authorityIn(String scope) throws IllegalArgumentException, IllegalStateException;
}

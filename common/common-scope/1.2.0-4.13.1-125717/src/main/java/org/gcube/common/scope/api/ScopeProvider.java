package org.gcube.common.scope.api;

import org.gcube.common.scope.impl.ScopeProviderScanner;

/**
 * Provides a scope in the caller's context.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ScopeProvider {

	/**
	 * Shared {@link ScopeProvider}.
	 */
	public static final ScopeProvider instance = ScopeProviderScanner.provider();
	
	/**
	 * Returns the scope in the caller's context.
	 * @return the scope
	 */
	String get();

	/**
	 * Sets the scope in the caller's context.
	 * @param scope the scope
	 */
	void set(String scope);
	
	/**
	 * Resets the scope in the caller's context.
	 */
	void reset();
}

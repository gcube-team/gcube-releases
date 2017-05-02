package org.gcube.common.scope.impl;

import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ScopeProvider} that uses threads as contexts.
 * <p>
 * Relies an an internal {@link InheritableThreadLocal}.
 * 
 * @author Fabio Simeoni
 * @see ScopeProviderScanner
 *
 */
public class DefaultScopeProvider implements ScopeProvider {

	/** System property for scope */
	public static final String SCOPE_PROPERTY = "gcube.scope";

	private static Logger log = LoggerFactory.getLogger(DefaultScopeProvider.class);
	
	private InheritableThreadLocal<String> scopes = new InheritableThreadLocal<String>();

	protected DefaultScopeProvider() {};
	
	@Override
	public String get() {
		String scope = scopes.get();
		if (scope==null)
			scope = System.getProperty(SCOPE_PROPERTY);
		return scope;
	}

	@Override
	public void set(String scope) {
		if (scope!=null)
			log.debug("setting scope {} in thread {}",scope,Thread.currentThread().getId());
		scopes.set(scope);
		
	}
	
	@Override
	public void reset() {
		log.debug("resetting scope in thread {}",Thread.currentThread().getId());
		scopes.remove();
	}
	
	
	
}

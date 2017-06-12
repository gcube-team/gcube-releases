package org.gcube.common.uri.ap;

import org.gcube.common.scope.api.ScopeProvider;

/**
 * An {@link AuthorityProvider} that provides the authority in the current scope.
 * <p>
 * The provider delegates the identification of the authority to a {@link ScopedAuthorityProvider}.
 * 
 * @author Fabio Simeoni
 *
 */
public class ScopedAPAdapter implements AuthorityProvider {

	private final ScopedAuthorityProvider provider;
	
	/**
	 * Creates an instance that delegates to a given {@link ScopedAuthorityProvider}
	 * @param provider the provider
	 */
	public ScopedAPAdapter(ScopedAuthorityProvider provider) {
		this.provider=provider;
	}
	
	@Override
	public String authority() {
		
		String scope = ScopeProvider.instance.get();
		
		if (scope==null | scope.isEmpty())
			throw new IllegalStateException("cannot provide an authority outside a scope");

		return provider.authorityIn(scope);
	};
}

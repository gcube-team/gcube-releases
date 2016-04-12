package org.gcube.common.uri.ap;

/**
 * A {@link ScopedAuthorityProvider} that provides authorities associated with the current infrastructure.
 * <p>
 * The provider reflects deployment strategies in which authorities are infrastructure-wide.
 * 
 * @author Fabio Simeoni
 *
 */
public class InfrastructureFilterSAP implements ScopedAuthorityProvider {

	private final ScopedAuthorityProvider target;
	
	/**
	 * Creates an instances that delegates to a given {@link ScopedAuthorityProvider}
	 * @param target
	 */
	public InfrastructureFilterSAP(ScopedAuthorityProvider target) {
		this.target=target;
	}
	
	@Override
	public String authorityIn(String scope) {
		
		String infrastructure = scope.split("/")[1];
		
		return target.authorityIn(infrastructure);
	}
}

package org.gcube.common.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.uri.ap.AuthorityProvider;
import org.gcube.common.uri.ap.InfrastructureFilterSAP;
import org.gcube.common.uri.ap.ScopedAPAdapter;
import org.gcube.common.uri.ap.ScopedPropertyAP;

/**
 * A {@link Mint} of HTTP URIs that delegates the identification of the naming authority to an {@link AuthorityProvider}
 * .
 * 
 * @author Fabio Simeoni
 * 
 */
public class HttpMint implements Mint {

	private static final String scheme = "http";

	private final AuthorityProvider provider;

	/**
	 * Creates an instance with a default {@link AuthorityProvider}
	 * <p>
	 * The default provider uses the authority currently in scope, resolving it from a
	 * {@link ScopedPropertyAP#AUHTORITY_PROVIDER_PROPERTY_FILE}.
	 * 
	 * @see ScopeProvider
	 * @see ScopedAPAdapter
	 * @see ScopedPropertyAP
	 */
	public HttpMint() {
		// want to go to the IS? use a cached scoped adapter that uses IS in infra scope
		// this(new ScopedDPAdapter(new InfrastructureFilterSDP(new CachingSDP(new ISDP()))));
		this(new ScopedAPAdapter(new InfrastructureFilterSAP(new ScopedPropertyAP())));

	}

	/**
	 * Creates an instance with a given {@link AuthorityProvider}.
	 * 
	 * @param provider the provider.
	 */
	public HttpMint(AuthorityProvider provider) {
		this.provider = provider;
	}

	@Override
	public URI mint(List<String> elements) throws IllegalStateException {

		String path = check(elements);

		String scope = ScopeProvider.instance.get();

		check(scope);

		String authority = provider.authority();

		try {
			
			//note:this encodes
			return new URI(scheme, authority, path, null, null);
			
		} catch (URISyntaxException e) {
			// as path has been encoded, this ought to mark a code or configuration error
			throw new IllegalStateException("cannot create URI", e);
		}
	}

	// helper
	private String check(List<String> elements) {

		if (elements == null || elements.isEmpty())
			throw new IllegalArgumentException("resource path is null or empty");

		for (String element : elements)
			if (element == null || element.isEmpty())
				throw new IllegalArgumentException("resource path " + elements + " contains a null or empty element");

		StringBuilder builder = new StringBuilder();

		for (String e : elements)
			builder.append("/" + e);

		String path = builder.toString();

		return path;
	}

	// helper
	private void check(String scope) {

		if (scope == null || scope.isEmpty())
			throw new IllegalStateException("cannot mint a URI outside a scope");
	}
}

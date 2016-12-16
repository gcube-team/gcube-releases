package org.gcube.common.uri.ap;

import org.gcube.common.uri.Mint;

/**
 * Returns naming authorities for URIs produced by {@link Mint}s.
 * <p>
 * Some providers may return constant authorities, others may use the thread context to discriminate between authorities.
 * 
 * @author Fabio Simeoni
 *
 */
public interface AuthorityProvider {

	/**
	 * Returns the naming authority for a URI produced by a {@link Mint}.
	 * @return the authority
	 */
	String authority();
}

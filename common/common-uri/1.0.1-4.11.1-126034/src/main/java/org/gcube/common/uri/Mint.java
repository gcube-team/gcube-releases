package org.gcube.common.uri;

import java.net.URI;
import java.util.List;

/**
 * Mints absolute hierarchical URIs from URI paths.
 * <p>
 * A {@link Mint} is responsible for providing the schema and naming authority of the URI.
 * Depending on the implementation, the {@link Mint} may provide further URI elements (e.g query parameters).
 * 
 * 
 * @author Fabio Simeoni
 *
 */
public interface Mint {

	/**
	 * Mints an absolute hierarchical URI from the elements of its path.
	 * @param pathElements the elements, as defined in <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a> 
	 * @return the URI
	 * @throws IllegalArgumentException if the elements do not conform to URI syntax
	 * @throws IllegalStateException if the URI cannot be minted in the state of the current thread.
	 */
	URI mint(List<String> pathElements) throws IllegalArgumentException,IllegalStateException;
}

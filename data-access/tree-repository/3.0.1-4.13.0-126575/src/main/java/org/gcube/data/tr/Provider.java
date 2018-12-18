package org.gcube.data.tr;

import org.gcube.data.tr.requests.BindSource;

/**
 * Provides dependencies to distinguished plugin components.
 * 
 * @author Fabio Simeoni
 * 
 */
public interface Provider {

	/**
	 * Returns a {@link TreeSource} configured according to a {@link BindSource} request.
	 * 
	 * @param request the request
	 * @return the source
	 */
	TreeSource newSource(BindSource request);

	/**
	 * Returns a {@link Store} with a given identifier.
	 * 
	 * @param sourceId the identifier
	 * @return the store
	 */
	Store newStore(String sourceId);

}

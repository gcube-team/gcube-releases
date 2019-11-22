package org.gcube.common.core.utils.handlers.lifetime;

import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEIHandler;

/**
 * Characterises the behaviour of {@link GCUBEIHandler GCUBEIHandlers} that manage their own lifetime. 
 * @author Fabio Simeoni (University of Strathclyde)
 * @param <HANDLED> the types of the handled object
 * @see GCUBEHandler
 */
public interface Lifetime<HANDLED> extends GCUBEIHandler<HANDLED> {
	
	/**Returns the current lifetime state of the handler.
	 * @return the status.*/
	public State getState();

}


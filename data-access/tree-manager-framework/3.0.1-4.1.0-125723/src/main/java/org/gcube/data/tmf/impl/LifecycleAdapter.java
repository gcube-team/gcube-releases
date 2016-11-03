/**
 * 
 */
package org.gcube.data.tmf.impl;

import org.gcube.data.tmf.api.SourceLifecycle;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.w3c.dom.Element;

/**
 * No-op implementation of {@link SourceLifecycle} for selective overriding in subclasses.
 * 
 * @author Fabio Simeoni
 * 
 * @see SourceLifecycle
 *
 */
public class LifecycleAdapter implements SourceLifecycle {

	private static final long serialVersionUID = 1L;
	
	/**{@inheritDoc}*/
	public void init() throws Exception {}
	
	/**{@inheritDoc}*/
	public void resume() throws Exception {}
	
	/**{@inheritDoc}*/
	@Override
	public void reconfigure(Element request) throws InvalidRequestException,Exception {}
	
	/**{@inheritDoc}*/
	public void terminate() {}
	
	/**{@inheritDoc}*/
	public void stop() {}


}

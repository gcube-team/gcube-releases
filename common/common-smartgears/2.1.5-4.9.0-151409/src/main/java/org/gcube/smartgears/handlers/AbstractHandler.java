package org.gcube.smartgears.handlers;

/**
 * Partial implementation of {@link Handler}.
 * 
 * @author Fabio Simeoni
 *
 * 
 */
public abstract class AbstractHandler {

	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public final boolean equals(Object obj) {
		AbstractHandler handler = (AbstractHandler) obj;
		return this.getClass().getCanonicalName().equals(handler.getClass().getCanonicalName());
	}
	
	//so far, largely a placeholder for future cross-handler behaviour
	
}

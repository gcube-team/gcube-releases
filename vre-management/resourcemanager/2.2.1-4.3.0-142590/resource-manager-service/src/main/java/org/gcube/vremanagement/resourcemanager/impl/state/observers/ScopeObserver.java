package org.gcube.vremanagement.resourcemanager.impl.state.observers;

import java.util.Observable;
import java.util.Observer;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;

/**
 * 
 * Base observer for {@link ScopeState} 
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class ScopeObserver implements Observer {

	protected GCUBELog logger = new GCUBELog(this.getClass());
	
	public void update(Observable observed, Object arg) {
		
		logger.trace(this.getClass().getSimpleName() + " Observer has been notified");
		ScopeState resources;

		if (ScopeState.class.isAssignableFrom(observed.getClass()))
			resources = (ScopeState) observed;
		else 
			 throw new IllegalArgumentException("Can't manage the observed obj");

		//notify subclasses
		this.scopeChanged(resources);
	}
	
	/**
	 * Manages the modified scope
	 * 
	 * @param scopeState the scope
	 */
	protected abstract void scopeChanged(ScopeState scopeState);

}

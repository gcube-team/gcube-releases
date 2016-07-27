package org.gcube.vremanagement.resourcemanager.impl.state.observers;

import org.gcube.vremanagement.resourcemanager.impl.state.ProfileDate;
import org.gcube.vremanagement.resourcemanager.impl.state.PublishedScopeResource;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import static org.gcube.vremanagement.resourcemanager.impl.state.ScopeState.OPERATION;
/**
 * 
 * Synchronizes the resources' list with the IS
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Publisher extends ScopeObserver  {


	@Override
	protected void scopeChanged(ScopeState scopeState) {
		 
		if (scopeState.getLastOperationPerformed() == OPERATION.PUBLISHED)
			return; //no need to republish and loop on this
		
		try {
			PublishedScopeResource resource = PublishedScopeResource.getResource(scopeState.getScope());
			try {
				logger.trace("PUBLISHER: Start time " + ProfileDate.toXMLDateAndTime(scopeState.getStartTime()));
				resource.synchWithLocalState(scopeState);
				resource.publish();
				scopeState.setLastOperationPerformed(OPERATION.PUBLISHED);
				scopeState.notifyObservers();//force to serialize after publishing (not nice, thought)
			} catch (Exception e) {
				logger.fatal("Can't publish the Scope Resource in the IS");
				throw e;
			}
		} catch (Exception e) {
			logger.fatal("An error occured in the resource's publishing", e);
		}
		
	}

}


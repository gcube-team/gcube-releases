package org.gcube.vremanagement.resourcemanager.impl.state;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.state.observers.Serializer;
import org.globus.wsrf.NoSuchResourceException;

/**
 * Local scoped resources map
 * @author manuele simi (CNR)
 *
 */
class ScopeStateList {

	/** mapping between scopes and their actual state*/
	Map<GCUBEScope, ScopeState> states = new HashMap<GCUBEScope, ScopeState>();
	
	protected GCUBELog logger = new GCUBELog(ScopeStateList.class);

	ScopeStateList() {}
	
	/**
	 * Initializes the scope state 
	 * @throws Exception if initialization fails
	 */
	void initializeScope(GCUBEScope scope) throws Exception {
		logger.debug("initialising scope " + scope.toString());
		try {
			states.put(scope, this.loadState(scope));
			logger.debug("scope state " + scope.toString() + " created from a previously serialized state");
		} catch (Exception e) {
			states.put(scope, this.createState(scope));
			logger.debug("scope state " + scope.toString() + " created from scratch");
		}
		
	}
	
	/**
	 * Loads the scope state from the local file system or the IS
	 * @param scope
	 * @throws Exception
	 */
	ScopeState loadState(GCUBEScope scope) throws NoSuchResourceException  {
		ScopeState scopeState = new ScopeState();
		try {
			logger.info("initializing scope "+ scope.getName() + " from the local file system...");
			Serializer.load(scopeState,scope);
			getPublishedScopeResource(scope).loadFromLocalState(scopeState);
		} catch (Exception e) {
			logger.warn("local serialized scope is not available");			
			logger.info("loading the instance state from the IS...");			
			if (getPublishedScopeResource(scope).load()) {
				logger.info("scope "+ scope.getName() + " successfully harvested from the IS");
				scopeState.initialize(scope, scope.getName(), GHNContext.getContext().isSecurityEnabled());
				//synch scopeState w/ IS list				
				getPublishedScopeResource(scope).to(scopeState);				
			} else 
				throw new NoSuchResourceException();
					
		}
		return scopeState;
	}
	
	/**
	 * Gets the {@link PublishedScopeResource} for the state
	 * 
	 * @return the {@link PublishedScopeResource} 
	 * @throws NoSuchResourceException
	 */
	 PublishedScopeResource getPublishedScopeResource(GCUBEScope scope) throws NoSuchResourceException {
		try {
			return PublishedScopeResource.getResource(scope);
		} catch (Exception e) {
			throw new NoSuchResourceException();
		}
	}

	 /**
	  * All scope states actually handled by the instance
	  * @return the states
	  */
	Collection<ScopeState> getAllStates() {
		return Collections.unmodifiableCollection(states.values());
	}

	/**
	 * The state of the given scope
	 * @param scope
	 * @return the state or null if the state does not exist
	 */
	ScopeState getState(GCUBEScope scope) {
		return states.get(scope);
	}
	
	/**
	 * Create a new resource for the given scope
	 * @param scope
	 * @return
	 * @throws NoSuchResourceException
	 * @throws Exception
	 */
	ScopeState createState(GCUBEScope scope) throws NoSuchResourceException, Exception {
		logger.info("creating scope state for "+ scope.getName());
		ScopeState scopeState = new ScopeState();
		//we assume that if we are running on a secure ghn, we are in a secure Scope
		scopeState.initialize(scope, scope.getName(), GHNContext.getContext().isSecurityEnabled());
		getPublishedScopeResource(scope).loadFromLocalState(scopeState);				
		states.put(scope, scopeState);
		return scopeState;
	}

	void removeState(ScopeState scopeState) {
		states.remove(scopeState.getScope());	
	}

}

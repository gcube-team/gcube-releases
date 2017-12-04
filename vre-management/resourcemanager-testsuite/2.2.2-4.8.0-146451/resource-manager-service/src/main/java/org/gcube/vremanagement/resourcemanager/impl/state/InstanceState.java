package org.gcube.vremanagement.resourcemanager.impl.state;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;
import org.gcube.vremanagement.resourcemanager.impl.state.observers.Disposer;
import org.gcube.vremanagement.resourcemanager.impl.state.observers.Executor;
import org.gcube.vremanagement.resourcemanager.impl.state.observers.Publisher;
import org.gcube.vremanagement.resourcemanager.impl.state.observers.Serializer;
import org.globus.wsrf.NoSuchResourceException;

/**
 * The ResourceManager's stateful resource
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class InstanceState extends GCUBEWSResource {

	static final ScopeStateList resources = new ScopeStateList();
	
	/** session id -> session map*/
	static Map<String, Session> id2session = Collections.synchronizedMap(new HashMap<String, Session>());; 
	
	@Override
	protected void initialise(Object... params) throws Exception {
		this.reuseState(this.getScope());
	}
	
	protected void reuseState(final GCUBEScope scope) throws Exception {
		//initialize the scope states
		resources.initializeScope(scope);
		final ScopeState state = resources.getState(scope);
		this.registerObservers(state);			
		//notify the observers about the initialization
		new Thread("ReuseStateThread"+scope.getName()) {
			@Override
			public void run() {
				state.notifyObservers();
			}
		}.start();
	}

	/**
	 * Gets the {@link Session}
	 * 
	 * @param id the session ID
	 * @return the session
	 * @throws IOException 
	 */
	public Session getSession(String id) throws IOException {
		if (! id2session.containsKey(id))
			id2session.put(id, Session.load(id));
		return id2session.get(id);		
	}
	
	/**
	 * Gets the string representation of a {@link Session}
	 * 
	 * @param id the session ID
	 * @return the string representation of the session
	 * @throws IOException 
	 */
	public String getSerializedSession(String id) throws IOException {
			return Session.loadAsString(id);
	}

	
	/**
	 * Adds a new {@link Session} to the service's state
	 * 
	 * @param session the session to add
	 * @throws NoSuchResourceException 
	 */
	public void addSession(GCUBEScope scope, Session session) throws NoSuchResourceException {
			this.getState(scope).setLastSession(session);
			id2session.put(session.getId(), session);
	}
	

	/**
	 * Gets the state for the given scope
	 * @return the list of scoped resources
	 * @throws NoSuchResourceException 
	 */
	public ScopeState getState(GCUBEScope scope) throws NoSuchResourceException {
		ScopeState state = resources.getState(scope);
		if (state == null)
			throw new NoSuchResourceException();
		return state;
	}

	/**
	 * Disposes the scope
	 * @param report 
	 * @throws IOException 
	 * @throws NoGHNFoundException 
	 */
	public void disposeState(GCUBEScope scope, Session report) throws IOException, NoGHNFoundException {
		ScopeState scopeState = resources.getState(scope);
		if (scopeState == null) {
			logger.warn("a scope state does not exist for " + scope);
			return;
		}
		Set<ScopedResource> allResources  = new HashSet<ScopedResource>(); 
		for (ScopedResource resource : scopeState.getAllResources()) {
			if ((resource.getType().compareTo(ScopedDeployedSoftware.TYPE) != 0) 
					&& (resource.getStatus() != STATUS.UNPUBLISHED)
					&& (resource.getStatus() != STATUS.LOST)
					&& (resource.getStatus() != STATUS.REMOVED)) {
				allResources.add(resource);
				report.addResource(resource);
			}
		}	
		//remove all resources
		scopeState.removeResources(allResources);	
		report.save();
		scopeState.markAsDisposed();
		resources.removeState(scopeState);

	}

	
	/**
	 * Gets the resource published in the IS for the given scope
	 * @param scope
	 * @return the resource
	 * @throws NoSuchResourceException
	 */
	public PublishedScopeResource getPublishedScopeResource(GCUBEScope scope) throws NoSuchResourceException {
		return resources.getPublishedScopeResource(scope);
	}

	
	/**
	 * Creates a new state for the given scope
	 * @param scope
	 * @throws NoSuchResourceException
	 * @throws Exception
	 */
	public void createState(GCUBEScope scope) throws NoSuchResourceException, Exception {
		ScopeState state = resources.createState(scope);
		this.registerObservers(state);
		state.notifyObservers();
	}
	
	/**
	 * Registers a set of observers for the state management operations 
	 * @param state
	 */
	
	private void registerObservers(ScopeState state) {
		state.addObserver(new Executor());
		state.addObserver(new Publisher());
		state.addObserver(new Serializer());
		state.addObserver(new Disposer());	
	}

}

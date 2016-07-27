package org.gcube.vremanagement.resourcemanager.impl.brokerage;


import java.util.Set;

import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;

/**
 * 
 * Models the expected behavior of a Broker. 
 * A Broker is an entity that can create Deployment Plans for a set of {@link ScopedDeployedSoftware}s to be deployed
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface Broker {

	/**
	 * Initializes the Broker instance
	 * @param scopeState the state where the broker will act
	 * @throws Exception if the initialization fails (depends on the broker's nature)
	 */
	public void initialize(ScopeState scopeState) throws Exception;
	
	/**
	 * Creates a deployment plan for the given services
	 * @param session the current active session on the state
	 * @param services the services to deploy
	 * @param suggestedGHNs the (eventually) GHNs suggested for the deployment, if specified, only these nodes will be used by the planner
	 * @throws Exception if the preparation of the plan fails
	 */
	public void makePlan(Session session, Set<ScopedDeployedSoftware> services, String[] suggestedGHNs) throws Exception;
	
	/**
	 * Sends feedback to the broker about the execution of the plan for the given session
	 * @param session the current active session on the state
	 * @throws Exception if the broker is not able to manage the feedback
	 */
	public void sendFeedback(Session session) throws Exception;
	
}

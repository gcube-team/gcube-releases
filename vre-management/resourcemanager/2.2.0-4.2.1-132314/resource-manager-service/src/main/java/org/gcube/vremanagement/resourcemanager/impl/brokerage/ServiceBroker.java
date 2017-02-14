package org.gcube.vremanagement.resourcemanager.impl.brokerage;


import java.util.List;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.stubs.ResourceBrokerPortType;
import org.gcube.vremanagement.resourcebroker.stubs.service.ResourceBrokerServiceAddressingLocator;
import org.gcube.vremanagement.resourcemanager.impl.brokerage.servicebroker.Feedback;
import org.gcube.vremanagement.resourcemanager.impl.brokerage.servicebroker.PlanParser;
import org.gcube.vremanagement.resourcemanager.impl.brokerage.servicebroker.PlanRequest;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;

/**
 * 
 * Broker that exploits the gCube Resource Broker service
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class ServiceBroker implements Broker {

	protected final GCUBELog logger = new GCUBELog(this);
	
	//private GCUBEScope scope;
	
	private List<GCUBERunningInstance> instances = null; 

	private static final int TIMEOUT = 600000; //in milliseconds
	
	private ScopeState scopeState;

	public ServiceBroker() {}
	
	/**
	 * {@inheritDoc}
	 */
	public void initialize(ScopeState scopeState) throws Exception {
		this.scopeState = scopeState;
		logger.info("Initializing the ServiceBroker..");
		this.instances = this.queryForBrokerInstances();
		if (this.instances.size() == 0)
			throw new Exception("unable to find an instance of the Resource Broker in scope");
		logger.info("Broker instances found: #" + this.instances.size());
	}
	
	public void makePlan(Session session, Set<ScopedDeployedSoftware> services, String[] suggestedGHNs) throws Exception {
		String request = PlanRequest.create(services, this.scopeState,nodeIDsFromNames(suggestedGHNs));
		logger.debug("Request for the Broker \n" + request);
		String plan = null;
		for (GCUBERunningInstance instance : this.instances) {
			EndpointReferenceType endpoint  = instance.getAccessPoint().getEndpoint("gcube/vremanagement/ResourceBroker");
			logger.debug("Querying broker instance at " + endpoint.getAddress());
			try {								
				plan = this.getBrokerPT(endpoint).getPlan(request);
				logger.debug("Plan received from the broker \n" + plan);
				break;
			} catch (Exception e) {
				logger.error("Unable to contact the Resource Broker instance located at " + endpoint.getAddress(), e);
				continue;
			}
		}
		PlanParser.parse(plan, services, this.scopeState);
		session.setDeploymentPlan(plan);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendFeedback(Session session) throws Exception {
		String feedback = Feedback.create(session);
		logger.debug("Feedback for the Broker \n" + feedback);		
		for (GCUBERunningInstance instance : this.instances) {
			EndpointReferenceType endpoint  = instance.getAccessPoint().getEndpoint("gcube/vremanagement/ResourceBroker");
			logger.debug("Sending broker feeback to " + endpoint.getAddress());
			try {
				this.getBrokerPT(endpoint).handleFeedback(feedback);				
				break;
			} catch (Exception e) {
				logger.error("Unable to contact the Resource Broker instance located at " + endpoint.getAddress(), e);
				continue;
			}
		}
	}

	
	/**
	 * Looks for all the Resource Broker instances in Scope		
	 * @return the list of RIs found
	 * @throws Exception if the query to the IS fails
	 */
	private List<GCUBERunningInstance> queryForBrokerInstances() throws Exception {
		 //looks for all the Broker instances		
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery lookupQuery = client.getQuery(GCUBERIQuery.class);
		lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceName","ResourceBroker"));
		lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceClass","VREManagement"));
		return client.execute(lookupQuery, this.scopeState.getScope());
				
	}

	private ResourceBrokerPortType getBrokerPT(EndpointReferenceType endpoint) throws Exception {
		ResourceBrokerPortType pt = new ResourceBrokerServiceAddressingLocator().getResourceBrokerPortTypePort(endpoint);
		pt = GCUBERemotePortTypeContext.getProxy(pt, this.scopeState.getScope(), TIMEOUT, ServiceContext.getContext());
		return pt;
	}
	
	private String[] nodeIDsFromNames(String[] suggestedGHNs) throws NoGHNFoundException {
		if (suggestedGHNs != null && suggestedGHNs.length > 0) //any suggested GHN?
			return null;
		String[] suggestedGHNIDs = new String[suggestedGHNs.length];
		for (int i=0; i<suggestedGHNIDs.length; i++)  
			suggestedGHNIDs[i] = scopeState.getNode(suggestedGHNs[i]).getID();
		return suggestedGHNIDs;
	}

}

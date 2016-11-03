package org.gcube.vremanagement.resourcemanager.impl.brokerage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;

/**
 * 
 * Trivial default Broker that assigns the target gHNs to services with a round robin policy
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class InternalBroker implements Broker {

	private final GCUBELog logger=new GCUBELog(this);
	
	private ScopeState scopeState;
	
	private String[] suggestedGHNs = new String[0];
	
	//used to apply a round robin assignment of the input GHNs (if any)
	private int nodewalker = 0;
	
	public InternalBroker() {} 

	/**
	 * {@inheritDoc}
	 */
	@Override public void initialize(ScopeState scopeState) throws Exception {this.scopeState = scopeState;}

	/**
	 * {@inheritDoc}
	 */
	@Override public void makePlan(Session session, Set<ScopedDeployedSoftware> services, String[] suggestedGHNs) throws Exception {
		this.suggestedGHNs = suggestedGHNs;
		for (ScopedDeployedSoftware service : services) {
			try {
				this.assignTargetGHN(service);
			} catch (NoGHNFoundException e1) {
				logger.error(service.getSourcePackage() + "Unable to find a suitable target gHN");
				service.setStatus(STATUS.LOST);
				service.setErrorMessage("Unable to find a suitable target gHN");
				throw new IOException(service.getSourcePackage() + " - unable to find a suitable target gHN for this service");
			}
		}
		
	}
		
   /** Selects a target node for the deployment 
	 * 
	 * @param service the service to deploy
	 * @throws MalformedURIException 
	 * @throws NoGHNFoundException if the gHN is not found
	 */
	private void assignTargetGHN(ScopedDeployedSoftware service) throws org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException {				
		if ( (service.getSourcePackage().getGHNName() != null) && (service.getSourcePackage().getGHNName().compareToIgnoreCase("") != 0)) {
			//a specific GHN has been requested	
			logger.info("A specific GHN (" + service.getSourcePackage().getGHNName() + ") has been requested by the caller for " + service);
			VirtualNode node = scopeState.getNode(service.getSourcePackage().getGHNName());
			node.setWorkingScope(scopeState.getScope());
			node.isNotWorking();
			service.scheduleDeploy(node); 				 				 			 
		} else if (this.suggestedGHNs.length > 0) { 
			//a set of target GHNs has been requested, assign one of them with a round robin 
			logger.info("A set of target GHNs " + Arrays.toString(this.suggestedGHNs)+" has been requested by the caller for " + service);
			VirtualNode node = scopeState.getNode(this.suggestedGHNs[this.nodewalker++ % this.suggestedGHNs.length]);
			node.setWorkingScope(scopeState.getScope());
			node.isNotWorking();
			service.scheduleDeploy(node);						
		} else { 
			logger.info("no GHN has been specified as explicit target");
			//no GHN has been specified as explicit target									
			throw new NoGHNFoundException("No GHN was assigned to the service from the caller");
		}			
	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override public void sendFeedback(Session session) throws Exception {
		/* feedback is not managed by this broker*/
	}

	


}

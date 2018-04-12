package org.gcube.vremanagement.resourcemanager.impl.state.observers;

import java.util.HashSet;
import java.util.Set;

import org.gcube.vremanagement.resourcemanager.impl.operators.Operator.ACTION;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedRunningInstance;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.ResourceNotFound;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState.OPERATION;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;

/**
 * 
 * Performs management operations on {@link ScopedResource}s according to their
 * current {@link STATUS}
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Executor extends ScopeObserver  {

	@Override
	protected void scopeChanged(ScopeState scopeState) {
		logger.trace("Executor: scopeChanged method ");
		if (scopeState.getLastOperationPerformed() == OPERATION.EXECUTED)
			return; //nothing to manage
		boolean managed = false;
		//RIs have to be managed first because
		//if a gHN is removed from a scope, the hosted RIs are automatically removed too by gCore and 
		//this has to be avoided, otherwise the removeFromScope will not find them at removing time
		//Map<String, List<ScopedDeployedSoftware>> softwareToRemoveFromNodes = new HashMap<String,List<ScopedDeployedSoftware>>();
		Set<ScopedResource> toRemoveFromScope = new HashSet<ScopedResource>();
		Set<ScopedResource> toRemoveFromState = new HashSet<ScopedResource>();
		for (ScopedResource ri : scopeState.getResourcesByType(ScopedRunningInstance.TYPE)){
			logger.trace("found a scoped ri resource "+ri.getId()+" with status: "+ri.getStatus()+" type of resource: "+ri.getType());
			switch (ri.getStatus()) {
				case ADDREQUESTED: this.addResourceToScope(ri); managed = true; break;
				case REMOVEREQUESTED: 
					try {
						logger.trace("managing REMOVEREQUESTED state");
						//managing RI undeployments
						if (((ScopedRunningInstance)ri).isUndeployNeeded()) {
							//String hostedOn = ((ScopedRunningInstance)ri).getHostedOn();
							try {
								//if (! softwareToRemoveFromNodes.containsKey(hostedOn)) 
								//	softwareToRemoveFromNodes.put(hostedOn, new ArrayList<ScopedDeployedSoftware>());
								ScopedDeployedSoftware relatedSoftware = scopeState.getRelatedDeployedSoftware(((ScopedRunningInstance)ri).getSourcePackage());	
								//remove the service from the node, since the instance to remove is undeployable
								logger.debug("Removing " + relatedSoftware + " from the scope");
								scopeState.getLastReport().addResource(relatedSoftware);
								scopeState.getLastReport().addService(relatedSoftware);
								//softwareToRemoveFromNodes.get(hostedOn).add(relatedSoftware);
								toRemoveFromScope.add(relatedSoftware);
								((ScopedRunningInstance)ri).wasSuccessful();
								ri.setStatus(STATUS.REMOVED);
							} catch (org.gcube.vremanagement.resourcemanager.impl.resources.ServiceNotFoundException e) {
								((ScopedRunningInstance)ri).reportFailureOnSourceService("Unable to find the source package. It might not be deployed in this scope",e);
								ri.setStatus(STATUS.LOST);
								toRemoveFromState.add(ri);
							}							
						} else { //just remove the instance from the scope					
							this.removeResourceFromScope(ri);
							toRemoveFromState.add(ri);
						}
					} catch (ResourceNotFound e) {
						ri.setStatus(STATUS.LOST);
					}
					managed = true;break;
			}
		}
		//now we can manage all the rest of the resources
		for (ScopedResource resource : scopeState.getAllResources()) {	
			logger.trace("found a scoped  resource "+resource.getId()+" with status: "+resource.getStatus()+" type of resource: "+resource.getType());
			//skip the resource's types already managed
			if (resource.getType().equalsIgnoreCase(ScopedRunningInstance.TYPE)) continue;
			switch (resource.getStatus()) {
				case ADDREQUESTED: this.addResourceToScope(resource); managed = true; break;
				case REMOVEREQUESTED: this.removeResourceFromScope(resource); toRemoveFromState.add(resource); managed = true;break;
			}
		}
		//notify the others for serialization and publication duties
		if (managed) {
			scopeState.setLastOperationPerformed(OPERATION.EXECUTED);
			scopeState.notifyObservers();
		}
		//if there are software to undeploy, redo the procedure for them
		/*for (String ghnid : softwareToRemoveFromNodes.keySet()) {
			for (ScopedDeployedSoftware software : softwareToRemoveFromNodes.get(ghnid)) {
				try {
					software.scheduleUndeploy(scopeState.getNodeById(ghnid));
				} catch (Exception e) {
					logger.error("Unable to undeploy " + software + " from " + ghnid, e);
				}
			}
		}
		*/
		
		//remove the services from the state 
		//or trigger the notifier to manage the service removed from specific nodes
		if (toRemoveFromScope.size() > 0 )
			try {
				scopeState.removeResources(toRemoveFromScope);
				toRemoveFromState.addAll(toRemoveFromScope);
			} catch (NoGHNFoundException e) {
				logger.error("cannot remove one of the software its hosting node:",e);
			}
		//else if (softwareToRemoveFromNodes.keySet().size() > 0)
		//	scopeState.notifyObservers();
		
		//...and finally, a physical cleanup of the state
		scopeState.forceResourceRemoval(toRemoveFromState);
	}

	
	private void addResourceToScope(ScopedResource resource)  {
		try {
			resource.doAction(ACTION.ADD);
			resource.setStatus(STATUS.ADDED);
		} catch (ResourceNotFound e) {
			//the resource does not exist..it will be removed from the internal state
			resource.setStatus(STATUS.REMOVED);
		} catch (Exception e) {
			resource.setStatus(STATUS.LOST);
		}
		
	}
	
	private void removeResourceFromScope(ScopedResource resource) {
		
			try {
				resource.doAction(ACTION.REMOVE);
				resource.setStatus(STATUS.REMOVED);
			} catch (ResourceNotFound e) {		
				//tolerate this exception... anyway it will be removed from the internal state				
				resource.setStatus(STATUS.REMOVED);
			} catch (Exception e) {
				//can't cope with this exception and don't know what to do
				resource.setStatus(STATUS.LOST);
			}
	}
	
}

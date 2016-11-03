package org.gcube.vremanagement.resourcemanager.impl.operators;


import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.brokerage.BrokerConnector;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session.OPERATION;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactory;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.GCUBEPackage;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.stubs.binder.PackageItem;
import org.gcube.vremanagement.resourcemanager.stubs.binder.SoftwareList;

/**
 * 
 * Manages the deployment/undeployment of software on the target nodes 
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DeploySoftwareOperator extends Operator {

	/** Object logger */
	protected final GCUBELog logger=new GCUBELog(this);
	
	private SoftwareList software;
	
	private OperatorConfig configuration;
	
	public DeploySoftwareOperator(ScopeState scopeState, OperatorConfig configuration,  SoftwareList software, ACTION action) {
		this.software = software;		
		this.configuration = configuration;
		this.action = action;
		this.scopeState = scopeState;
	}
	
	public void exec() throws Exception {			
		this.configuration.session.save();		
		Set<ScopedDeployedSoftware> softwareToDeploy = new HashSet<ScopedDeployedSoftware>();
		Set<ScopedResource> softwareToUndeploy = new HashSet<ScopedResource>();
		for (PackageItem packageitem : software.getSoftware()) {
			ScopedDeployedSoftware software=null; 
			try {
				if (this.action == ACTION.ADD) {
					software = createDeployedSoftware(packageitem);
					software.findResource();//resolve the service deps
					if (software.isSuccess())
						softwareToDeploy.add(software);
				} else { //assume undeployment
					software = this.getDeployedSoftware(GCUBEPackage.fromServiceItem(packageitem), packageitem.getTargetGHNName());
					softwareToUndeploy.add(software);
				}	
			} catch (Exception e) {
				logger.warn("Failed to find the package",e);
				software = createDeployedSoftware(packageitem);	//only for reporting purposes
				software.setErrorMessage("Failed to find the package on the IS or in the ScopeState");
				software.setStatus(STATUS.LOST);
			}			
			this.configuration.session.addResource(software);			
			this.configuration.session.addService(software); //for the specific service section						
		}
		//create the Deployment Plan with the Broker	
		if ((this.action == ACTION.ADD) 
			&& this.allocate(this.configuration.session,softwareToDeploy)) {
			Set<ScopedResource> resourcesToAdd = new HashSet<ScopedResource>();
			for (ScopedDeployedSoftware software : softwareToDeploy) {
				if (software.getStatus() != STATUS.LOST) {
					software.setCallbackID(this.configuration.session.getId());
					resourcesToAdd.add(software);
					if (this.configuration.scope.getType() == Type.VRE) {
						//add also the target gHN to the scope
						ScopedResource ghn = ScopedResourceFactory.newResource(scopeState.getScope(), software.getTargetNodeID(), GCUBEHostingNode.TYPE);
						resourcesToAdd.add(ghn);
						this.configuration.session.addResource(ghn);
					}
				}
			}		
			//add the services to the ScopeState (if any)		
			if(softwareToDeploy.size() > 0) {
				this.configuration.scopeState.addResources(resourcesToAdd);
				//periodically check if the feedback has to be sent to the broker
				new Thread() {					
					@Override
					public void run() {
						Session tsession = DeploySoftwareOperator.this.configuration.session;
						while (true) {						
							try { 
								Thread.sleep(5000);
							} catch (InterruptedException e) {}
							//checking if the session is closed and we need to send feedback
							if ((tsession.isSessionClosed() 
									&& ((tsession.getOperation()==OPERATION.AddResources)
										|| (tsession.getOperation()==OPERATION.UpdateResources)
										|| (tsession.getOperation()==OPERATION.Create)))) {
								try {
									BrokerConnector.getBroker(DeploySoftwareOperator.this.scopeState).sendFeedback(tsession);
								} catch (Exception e) {
									logger.error("Failed to send the feedback to the Broker", e);
								}  
								break;
							}
						}
					}
					
				}.start();
			}
		}	
		//remove the services from the ScopeState (if any)
		if(softwareToUndeploy.size() > 0)
			this.configuration.scopeState.removeResources(softwareToUndeploy);
		
		this.configuration.session.save(); 	
		this.configuration.session.startChecker();
	}

	/**
	 * Contacts the broker in order to allocate the given services 
	 * @param servicesToDeploy the services to be deployed
	 * @return true if each service is correctly allocated, false otherwise
	 */
	private boolean allocate(Session session, Set<ScopedDeployedSoftware> servicesToDeploy) {
		try {
			if (servicesToDeploy.size() > 0) {
				BrokerConnector.getBroker(scopeState).makePlan(session, servicesToDeploy, software.getSuggestedTargetGHNNames());
				this.configuration.session.reportBrokerWork(true, "The Deployment Plan was successfully created");
				return true;
			} else {
				logger.warn("The Broker was not contacted. After the dependency resolution phase, there is nothing to deploy");
				this.configuration.session.reportBrokerWork(false, "The Broker was not contacted. After the dependency resolution phase, there is nothing to deploy");
				return true;
			}
		} catch (Exception e) {
			logger.error("An error occurred when interacting with the broker", e);			
			this.configuration.session.reportBrokerWork(false, "An error occurred when interacting with the broker " + e.getMessage());
			return false;
		}
	}
	
	private ScopedDeployedSoftware getDeployedSoftware(GCUBEPackage sourcePackage, String targetNode) throws Exception{
		String id = sourcePackage.getID();
		logger.trace("Searching state for package " + id + " on node " + targetNode);
		Set<ScopedResource> availableSoftware = scopeState.getResourcesByType(GCUBEService.TYPE);
		for (ScopedResource resource : availableSoftware) {
			ScopedDeployedSoftware software = (ScopedDeployedSoftware)resource;
			// package's coordinates by pt
			GCUBEPackage serializedPackage =  software.getSourcePackage();
			if (serializedPackage!=null)
				logger.trace("Found package " + serializedPackage.getID() + " on node " + software.getTargetNodeName());
			if ((serializedPackage!=null) && (serializedPackage.getID().equals(id))
					&& (software.getTargetNodeName().equalsIgnoreCase(targetNode)))   {
					logger.trace("Taking resource " + id + " from the scope state");
					return software;
			} 
		}
		throw new Exception("Unable to find the software in the scope state");
	}

	private ScopedDeployedSoftware createDeployedSoftware(PackageItem packageitem) throws Exception {
		GCUBEPackage sourcePackage = GCUBEPackage.fromServiceItem(packageitem);
		ScopedDeployedSoftware software = (ScopedDeployedSoftware) ScopedResourceFactory.newResource(scopeState.getScope(), sourcePackage.getID(), GCUBEService.TYPE);
		software.setSourcePackage(sourcePackage);	
		return software;
	}
}

package org.gcube.vremanagement.resourcemanager.porttypes;

import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.reporting.DeployerReport;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.reporting.DeployerReport.DeployedRunningInstance;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.state.PublishedScopeResource;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;

import org.gcube.vremanagement.resourcemanager.stubs.common.InvalidScopeFaultType;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.*;

/**
 * <em>Reporting</em> portType implementation
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class Reporting extends ResourceManagerPortType {

	/**
	 * Receives a deployment session. It is called by the Deployer services on the GHNs contacted within the {@link #addResources(AddResourcesParameters)}
	 * operation
	 * 
	 * @param session the resource session 
	 * @throws GCUBEFault  if the session does not have a valid serialization
	 */	
	public void sendReport(SendReportParameters reportMessage) throws GCUBEFault {
		//the scope is not validated, as it could be previously removed from the instance 
		//with a dispose scope. This is still safe, as the SendReport does not modify the scope in any case
		GCUBEScope targetScope = GCUBEScope.getScope(reportMessage.getTargetScope());	
		logger.info("Received session for session " + reportMessage.getCallbackID());
		logger.trace("Report content: \n" + reportMessage.getReport());
		try {
			Session session = this.getInstanceState().getSession(reportMessage.getCallbackID());
			DeployerReport dreport = new DeployerReport(this.getInstanceState(), targetScope, reportMessage.getReport());						
			session.addGHNReport(dreport);			
			session.save();
			PublishedScopeResource resource = this.getInstanceState().getPublishedScopeResource(targetScope);						
			logger.debug("Status session is: " + dreport.getStatus());
			if (dreport.getStatus().compareToIgnoreCase("CLOSED") == 0) {
				logger.trace("Setting the gHN " + dreport.getGHNName() + " as non working");
				//if the session is closed, declare the node as "non working" node
				this.getInstanceState().getState(targetScope).getNode(dreport.getGHNName()).isNotWorking();
				logger.trace("Parsing running instances (if any)...");
				Set<ScopedResource> resources = new HashSet<ScopedResource>();			
				for (DeployedRunningInstance instance : dreport.getInstances()) {
					if (instance.isAlive()) {
						logger.trace("Adding instance " + instance.getRIID() + " to PublishedScopeResource");
						resource.addResource(instance.getInstance());
						resources.add((ScopedResource)instance.getInstance());
					} else {
						logger.warn("Instance " + instance.getRIID() + " not found on the IS");
					}
				}				
				session.addDeployedInstances(dreport.getInstances());
				//add the newly generated RIs to the Scope State
				this.getInstanceState().getState(targetScope).addResources(resources);
			}
			resource.publish();
			session.save();
			
		} catch (NoSuchResourceException e) {
			logger.error("Unable to find ResourceManager resource", e);
			throw ServiceContext.getContext().getDefaultException("Unable to find ResourceManager resource", e).toFault();
		} catch (ResourceException e) {
			logger.error("Unable to find ResourceManager resource", e);
			throw ServiceContext.getContext().getDefaultException("Unable to find ResourceManager resource", e).toFault();
		} catch (Exception e) {
			throw ServiceContext.getContext().getDefaultException("Unable to parse or save the Deployer Report", e).toFault();
		}
	}
	
	/**
	 * Gets a Resource Report
	 * 
	 * @param ID the session identifier
	 * @return the string serialization of the session
	 * @throws GNoSuchReportFaultTypeCUBEFault if the session is not found or does not have a valid serialization
	 */
	public String getReport(String ID) throws NoSuchReportFaultType {
		try {
			return this.getInstanceState().getSession(ID).toXML();
		} catch (Exception e) {
			logger.error("Unable to retrieve the Resource Report for ID " + ID,e);
			throw new NoSuchReportFaultType();
		}
		
	}
	
	
	
}

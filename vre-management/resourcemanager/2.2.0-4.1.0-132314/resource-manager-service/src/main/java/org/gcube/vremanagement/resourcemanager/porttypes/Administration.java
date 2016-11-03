package org.gcube.vremanagement.resourcemanager.porttypes;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;

import org.gcube.vremanagement.resourcemanager.stubs.common.InvalidScopeFaultType;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.*;

/**
 * <em>Administration</em> portType implementation
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class Administration extends ResourceManagerPortType {

	/**
	 * Receives a deployment session. It is called by the Deployer services on the GHNs contacted within the {@link #addResources(AddResourcesParameters)}
	 * operation
	 * 
	 * @param session the resource session 
	 * @throws GCUBEFault  if the session does not have a valid serialization
	 */	
	public void cleanSoftwareState(SendReportParameters reportMessage) throws GCUBEFault {
		GCUBEScope targetScope = ScopeUtils.validate(reportMessage.getTargetScope());
		if (!ScopeUtils.exists(targetScope, this)) {
			logger.warn("Target scope " + targetScope.toString()+ " does not exists and cannot be modified");
			throw new InvalidScopeFaultType();	
		}

	}
	

	
	
	
}

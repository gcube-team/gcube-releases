package org.gcube.vremanagement.resourcemanager.porttypes;


import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager.IllegalScopeException;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.operators.AddResourcesOperator;
import org.gcube.vremanagement.resourcemanager.impl.operators.OperatorConfig;
import org.gcube.vremanagement.resourcemanager.impl.operators.RemoveResourcesOperator;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session.OPERATION;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;

import org.gcube.vremanagement.resourcemanager.stubs.binder.*;
import org.gcube.vremanagement.resourcemanager.stubs.common.InvalidScopeFaultType;



/**
 * <em>ResourceBinder</em> portType implementation
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class ResourceBinder extends ResourceManagerPortType {

	/**
	 * Adds a new group of {@link ScopedResource}s to the managed Scope
	 * 
	 * @param resourcesList the resources to join 
	 * @return the ID assigned to the operation, it can be used to retrieve the resource session by invoking the {@link ResourceBinder#getReport(String)} operation
	 * @throws GCUBEFault if the operation fails
	 */
	public synchronized String addResources(AddResourcesParameters resourceList) throws ResourcesCreationFaultType, GCUBEFault {
		logger.debug("AddResources operation invoked in scope " + ServiceContext.getContext().getScope().getName());		
		GCUBEScope targetScope = ScopeUtils.validate(resourceList.getTargetScope());
		if (!ScopeUtils.exists(targetScope, this)) {
			logger.warn("Target scope " + targetScope.toString()+ " does not exists and cannot be modified");
			throw new InvalidScopeFaultType();	
		}
		try {
			//checks the input scope
			Session report = new Session(UUIDGenFactory.getUUIDGen().nextUUID(),OPERATION.AddResources, targetScope);
			this.getInstanceState().addSession(targetScope,report);			
			new AddResourcesOperator(this.getInstanceState().getState(targetScope),new OperatorConfig(report, this.getInstanceState().getState(targetScope), targetScope),resourceList).run();			
			//resource.publish();			
			//returns the session ID, it can be used to invoke the getReport operation
			return report.getId();
		} catch (IllegalScopeException ise){
			logger.error("The target scope (" + resourceList.getTargetScope() + ") is not valid or null or not joined to this instance", ise);
			throw ServiceContext.getContext().getDefaultException("The target scope (" + resourceList.getTargetScope() + ") is not valid or null or not joined to this instance", ise).toFault();
		} catch (Exception e) {
			logger.error("Unable to manage the input given resources(s) within the scope: " + e.getMessage(), e);
			throw ServiceContext.getContext().getDefaultException("Unable to manage the input given resources(s) within the scope: " + e.getMessage(), e).toFault();
		}			
	}
	
	/**
	 * Removes a group of {@link ScopedResource}s from the managed Scope
	 * 
	 * @param resourcesList the resources to remove from the PublishedScopeResource
	 * @throws GCUBEFault if the operation fails
	 */
	public synchronized String removeResources(RemoveResourcesParameters resourceList) throws ResourcesRemovalFaultType, InvalidScopeFaultType {
		GCUBEScope targetScope = ScopeUtils.validate(resourceList.getTargetScope());
		if (!ScopeUtils.exists(targetScope, this)) {
			logger.warn("Target scope " + targetScope.toString()+ " does not exists and cannot be modified");
			throw new InvalidScopeFaultType();	
		}
		try {
			Session report = new Session(UUIDGenFactory.getUUIDGen().nextUUID(), OPERATION.RemoveResources, targetScope);
			this.getInstanceState().addSession(targetScope,report);
			new RemoveResourcesOperator(this.getInstanceState().getState(targetScope), new OperatorConfig(report, this.getInstanceState().getState(targetScope), targetScope),resourceList).run();
			return report.getId();
		} catch (IllegalScopeException ise){
			logger.error("The target scope (" + resourceList.getTargetScope() + ") is not valid or null or not joined to this instance", ise);
			throw new InvalidScopeFaultType();
		} catch (Exception e) {
			logger.error("Unable to manage the input given resources(s) within the scope: " + e.getMessage(), e);
			//throw ServiceContext.getContext().getDefaultException("Unable to manage the input given resources(s) within the scope: " + e.getMessage(), e).toFault();
			throw new ResourcesRemovalFaultType();
		}
	}
	
//	public synchronized String updateResources(AddResourcesParameters resourceList)  throws ResourcesCreationFaultType, GCUBEFault {
//		throw new GCUBEFault("This operation is not implemented");
//	}
	
}

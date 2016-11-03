package org.gcube.vremanagement.resourcemanager.porttypes;


import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session.OPERATION;
import org.gcube.vremanagement.resourcemanager.impl.state.PublishedScopeResource;
import org.gcube.vremanagement.resourcemanager.impl.state.PublishedScopeResource.UnknownScopeOptionException;
import org.globus.wsrf.NoSuchResourceException;

import org.gcube.vremanagement.resourcemanager.stubs.common.InvalidScopeFaultType;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.*;



/**
 * <em>ScopeController</em> portType implementation
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class ScopeController extends ResourceManagerPortType {

	/**
	 * Disposes the managed Scope
	 * @param params
	 * @return
	 * @throws GCUBEFault
	 */
	public synchronized String disposeScope(String targetScope) throws InvalidScopeFaultType, GCUBEFault {
		logger.info("Dispose Scope invoked... the entire scope is going to be thrown away!!");
		GCUBEScope scope = ScopeUtils.validate(targetScope.trim());
		if (!ScopeUtils.exists(scope, this)) {
			logger.warn("Target scope " + scope.toString()+ " does not exists and cannot be disposed");
			throw new InvalidScopeFaultType();	
		}
		ScopeUtils.removeFromInstance(scope,this);
		try {	
			scope.getServiceMap();		
		} catch (GCUBEScopeNotSupportedException e) {
			logger.error("Scope not supported " + targetScope);
			throw new InvalidScopeFaultType();
		}
		try {			
			Session report = new Session(UUIDGenFactory.getUUIDGen().nextUUID(), OPERATION.Dispose,scope);
			this.getInstanceState().addSession(scope,report);
			this.getInstanceState().disposeState(scope, report); 
			return report.getId();
		} catch (NoSuchResourceException e) {
			logger.error("No resource found for this scope", e);
			throw ServiceContext.getContext().getDefaultException("No resource found for this scope", e).toFault();
		} catch (Exception e) {
			logger.error("Unable to dispose the scope: " + e.getMessage(), e);
			throw ServiceContext.getContext().getDefaultException("Unable to dispose the scope: " + e.getMessage(), e).toFault();
		}
		
	}
	
	
	public synchronized void createScope(CreateScopeParameters params) 
		throws InvalidScopeFaultType, InvalidOptionsFaultType, GCUBEFault {
		GCUBEScope scope = ScopeUtils.validate(params.getTargetScope().trim());		
		if (ScopeUtils.exists(scope, this)) {
			logger.warn("Target scope " + scope.toString()+ " already exits and cannot be re-created");
			throw new InvalidScopeFaultType();	
		}
		try {
			scope.getServiceMap();		
		} catch (MalformedScopeExpressionException e) {	
			throw new InvalidScopeFaultType();									
		} catch (GCUBEScopeNotSupportedException e) {
			//the service map for this scope does not exist
			params.getServiceMap();
		}
		//String map = params.getServiceMap();
		try {
			this.getInstanceState().createState(scope);
			ScopeUtils.addToInstance(scope,this);
		} catch (Exception e) {
			logger.error("Failed to create the scope: " + scope.toString(), e);
			throw ServiceContext.getContext().getDefaultException("Unable to add this Resource Managet to the scope: " + e.getMessage(), e).toFault();
		}
		if (params.getOptionsParameters() != null) {
			params.getOptionsParameters().setTargetScope(params.getTargetScope());
			this.changeScopeOptions(params.getOptionsParameters());		
		}
	}
	
	/**
	 * Changes some options on the scope
	 * 
	 * @param options the new options to change
	 * @throws GCUBEFault if any of the input options is not valid
	 */
	public void changeScopeOptions(OptionsParameters options) throws InvalidOptionsFaultType, InvalidOptionsFaultType, GCUBEFault {
		PublishedScopeResource scoperesource = null;
		GCUBEScope scope = ScopeUtils.validate(options.getTargetScope().trim());
		if (!ScopeUtils.exists(scope, this)) {
			logger.warn("Target scope " + scope.toString()+ " does not exists and cannot be modified");
			throw new InvalidScopeFaultType();	
		}
		try {
			scoperesource = this.getInstanceState().getPublishedScopeResource(scope);
		} catch (NoSuchResourceException e) {
			logger.error("No resource found for this scope", e);
			throw ServiceContext.getContext().getDefaultException("No resource found for this scope", e).toFault();			
		} catch (Exception e) {
			logger.error("Change Scope Options fault: ", e);
			throw ServiceContext.getContext().getDefaultException("Change Scope Options fault: ", e).toFault();					
		}
		//add the new options
		for (ScopeOption option : options.getScopeOptionList()) {
			if (option == null) continue;
			logger.trace("ScopeOption received: " + option.getName() +"="+ option.getValue());
			try {
				scoperesource.setOption(option.getName().trim(), option.getValue().trim());				
			} catch (UnknownScopeOptionException e) {
				logger.warn("Unknown option: " + option.getName());
				throw new InvalidOptionsFaultType();
			} catch (Exception e) {
				logger.warn("Unable to read option: " + option.getName());
				throw new InvalidOptionsFaultType();
			}
		}
		//and publish the scope resource
		try {			
			scoperesource.publish();
		} catch (Exception e) {
			logger.error("Unable to publish the ScopeResouce", e);
			throw ServiceContext.getContext().getDefaultException("Unable to publish the ScopeResouce", e).toFault();
		}
	}
	
	
}

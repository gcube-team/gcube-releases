package org.gcube.vremanagement.resourcemanager.impl.operators;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.stubs.binder.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceList;
import org.gcube.vremanagement.resourcemanager.stubs.binder.SoftwareList;

/**
 * A Resources Operator that coordinates the adding of resources to the scope
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class AddResourcesOperator extends Operator {

	protected final GCUBELog logger = new GCUBELog(this, ServiceContext.getContext());	
	
	private AddResourcesParameters resourceList;
	
	private OperatorConfig configuration;
	
	/**
	 * Creates a new operator to manage the input resource list
	 * 
	 * @param scopeState
	 * @param target
	 * @param operationID
	 */
	public AddResourcesOperator(ScopeState scopeState, OperatorConfig configuration, AddResourcesParameters resourceList){
		this.resourceList = resourceList;		
		this.configuration = configuration;
		this.scopeState = scopeState;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void exec() throws Exception {
		// deploy the services, if any. software from portType
		SoftwareList software  = resourceList.getSoftware();
		if ((software == null) || (software.getSoftware() == null) || (software.getSoftware().length == 0)) {
			logger.warn("The list of services to deploy is empty");		
		} else {
			try {	
				new DeploySoftwareOperator(scopeState, configuration, software, ACTION.ADD).run();
			} catch (Exception e) {
				logger.error("Unable to activate the deployment of the given service(s)", e);
				throw new Exception("Unable to activate the deployment of the given service(s)", e);
			}			
		}
		
		//add the resources to the PublishedScopeResource, if any
		ResourceList resources = resourceList.getResources();
		if ((resources == null) || (resources.getResource() == null) || (resources.getResource().length == 0)) 
			logger.warn("The list of resource to add is empty");
		else {
			try {
				new ScopedResourceManagerOperator(scopeState, configuration, resources, ACTION.ADD).run();
			}catch (Exception e) {
				logger.error("Unable to manage the given resource(s)", e);
				throw new Exception("Unable to manage the given resource(s)", e);
			}	
		}
		//save the session
		this.configuration.session.save();

	}

}

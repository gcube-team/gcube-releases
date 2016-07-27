package org.gcube.vremanagement.resourcemanager.porttypes;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.contexts.StatefulPortTypeContext;
import static org.gcube.vremanagement.resourcemanager.impl.contexts.StatefulPortTypeContext.*;
import org.gcube.vremanagement.resourcemanager.impl.state.InstanceState;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;

/**
 * Base portType for all Resource Manager portTypes
 * @author manuele simi (CNR)
 *
 */
abstract class ResourceManagerPortType extends GCUBEPortType {

	/**
	 * Object logger.
	 */
	protected final GCUBELog logger = new GCUBELog(this, ServiceContext.getContext());


	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();

	}
	
	/**
	 * Gets the instance state
	 * 
	 * @return the {@link InstanceState}
	 * @throws NoSuchResourceException
	 * @throws ResourceException
	 */
	protected InstanceState getInstanceState() throws NoSuchResourceException, ResourceException{
		return (InstanceState) StatefulPortTypeContext.getContext().getWSHome().find(StatefulPortTypeContext.getContext().makeKey(SINGLETON_RESOURCE_KEY));	
	}

	/**
	 * Adds the scope to the current service's instance
	 * @param scope
	 * @throws ResourceException
	 */
	protected void addScopeToInstanceState(GCUBEScope scope) throws ResourceException {
		StatefulPortTypeContext.getContext().getWSHome().create(StatefulPortTypeContext.getContext().makeKey(SINGLETON_RESOURCE_KEY));
	}
}

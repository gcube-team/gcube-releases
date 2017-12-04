package org.gcube.vremanagement.resourcemanager.impl.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.vremanagement.resourcemanager.impl.contexts.StatefulPortTypeContext;
import org.globus.wsrf.ResourceException;

/**
 * 
 * Home for stateful resource
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ResourceManagerHome extends GCUBEWSHome {

	@Override
	public GCUBEStatefulPortTypeContext getPortTypeContext() {		
		return StatefulPortTypeContext.getContext();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.core.state.GCUBEWSHome#onReuse(org.gcube.common.core.state.GCUBEWSResource)
	 */
	@Override
	protected void onReuse(GCUBEWSResource resource) throws ResourceException {
		super.onReuse(resource);
		try {
			((InstanceState) resource).reuseState(resource.getServiceContext().getScope());
		} catch (Exception e) {
			throw new ResourceException("unable to reuse resource in scope " + resource.getServiceContext().getScope().toString());
		}
	}



}

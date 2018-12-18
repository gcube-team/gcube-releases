package org.gcube.data.tm.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.data.tm.context.TBinderContext;
import org.globus.wsrf.ResourceException;

/**
 * Extends {@link GCUBEWSHome} for the singleton {@link TBinderResource}.
 * @author Fabio Simeoni
 */
public class TBinderHome extends GCUBEWSHome{

	/**{@inheritDoc}*/
	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return TBinderContext.getContext(); 
	}
	
	@Override
	/**{@inheritDoc}*/
	//override to synchronize
	protected synchronized void onInitialisation() throws Exception {
		super.onInitialisation();
	}
	
	@Override
	/**{@inheritDoc}*/
	//override to synchronize
	public synchronized GCUBEWSResource create(GCUBEWSResourceKey id, Object... params) throws ResourceException {
		return super.create(id, params);
	}
	
	//supports testing
	public TBinderResource load() throws Exception {
		
		TBinderResource resource = new TBinderResource();
		preInitialise(resource);
		resource.setID(TBinderContext.getContext().key());
		persistenceDelegate.load(resource,false);
		return resource;

	}
}

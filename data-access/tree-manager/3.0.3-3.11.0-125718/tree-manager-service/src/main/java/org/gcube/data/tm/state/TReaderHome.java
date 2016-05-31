package org.gcube.data.tm.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.data.tm.context.TReaderContext;

/**
 * Extends {@link GCUBEWSHome} for the singleton {@link TBinderResource}.
 * @author Fabio Simeoni
 */
public class TReaderHome extends GCUBEWSHome {

	/**{@inheritDoc}*/
	public GCUBEStatefulPortTypeContext getPortTypeContext() { return TReaderContext.getContext(); }
	
	//supports testing
	public TReaderResource load(GCUBEWSResourceKey id) throws Exception {
		
		TReaderResource resource = new TReaderResource();
		preInitialise(resource);
		resource.setID(id);
		persistenceDelegate.load(resource,false);
		return resource;

	}
}

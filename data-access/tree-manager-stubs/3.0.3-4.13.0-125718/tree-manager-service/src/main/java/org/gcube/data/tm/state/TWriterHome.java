package org.gcube.data.tm.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.data.tm.context.TWriterContext;

/**
 * Extends {@link GCUBEWSHome} for the singleton {@link TBinderResource}.
 * @author Fabio Simeoni
 */
public class TWriterHome extends GCUBEWSHome {

	/**{@inheritDoc}*/
	public GCUBEStatefulPortTypeContext getPortTypeContext() { return TWriterContext.getContext(); }

	//supports testing
	public TWriterResource load(GCUBEWSResourceKey id) throws Exception {
		
		TWriterResource resource = new TWriterResource();
		preInitialise(resource);
		resource.setID(id);
		persistenceDelegate.load(resource,false);
		return resource;

	}
	
}

/**
 * 
 */
package org.gcube.data.tm.state;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.state.GCUBELocalHome;
import org.gcube.data.tm.context.ServiceContext;

/**
 * The home of {@link SourceResource}s.
 * @author Fabio Simeoni
 *
 */
public class SourceHome extends GCUBELocalHome {

	/**{@inheritDoc}*/
	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
	
	//supports testing
	public SourceResource load(String id) throws Exception {
		
		SourceResource resource = new SourceResource();
		preInitialise(resource);
		resource.setID(id);
		persistenceDelegate.load(resource,false);
		return resource;
	}

}

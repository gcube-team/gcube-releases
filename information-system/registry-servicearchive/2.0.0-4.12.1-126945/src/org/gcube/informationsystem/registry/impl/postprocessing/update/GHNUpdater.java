package org.gcube.informationsystem.registry.impl.postprocessing.update;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.postprocessing.remove.AvailablePurgers;
import org.gcube.informationsystem.registry.impl.postprocessing.remove.Purger;

public class GHNUpdater implements Updater<GCUBEHostingNode>  {

	protected final GCUBELog logger = new GCUBELog(GHNUpdater.class);

	@Override
	public void update(GCUBEResource resource, GCUBEScope scope) {
		if (! (resource instanceof GCUBEHostingNode)) 
			return;
		GCUBEHostingNode node = (GCUBEHostingNode) resource;
		logger.trace("The gHN is currently in status " + node.getNodeDescription().getStatus().toString());
		if ((node.getNodeDescription().getStatus() == GHNContext.Status.DOWN) 
			||(node.getNodeDescription().getStatus() == GHNContext.Status.FAILED)) {
				logger.trace("Looking for purger for "+ GCUBEHostingNode.TYPE);
				Purger<?> purger = AvailablePurgers.getPurger(GCUBEHostingNode.TYPE);
				if (purger != null) {
					try {
						logger.debug("Applying purger for "  + GCUBEHostingNode.TYPE);
						purger.purge(node.getID(), scope);
					} catch (Exception e) {
						logger.error("Error while removing the profiles related to the resource",	e);
					}
				} else
					logger.trace("No purger found");	
			}		
	}

	@Override
	public String getName() {
		return GCUBEHostingNode.TYPE;
	}

}

package org.gcube.informationsystem.registry.impl.postprocessing.update;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.postprocessing.remove.AvailablePurgers;
import org.gcube.informationsystem.registry.impl.postprocessing.remove.Purger;

public class RIUpdater implements Updater<GCUBERunningInstance> {

	protected final GCUBELog logger = new GCUBELog(RIUpdater.class);

	@Override
	public void update(GCUBEResource resource, GCUBEScope scope) {
		if (! (resource instanceof GCUBERunningInstance)) 
			return;
		GCUBERunningInstance ri = (GCUBERunningInstance) resource;
		logger.trace("The Running Instance is currently in state " + ri.getDeploymentData().getState());
		if ((ri.getDeploymentData().getState().equals(GCUBEServiceContext.Status.DOWN.toString())) 
			||(ri.getDeploymentData().getState().equals(GCUBEServiceContext.Status.FAILED.toString()))) {
			logger.trace("Looking for purger for "+ GCUBERunningInstance.TYPE);
			Purger<?> purger = AvailablePurgers.getPurger(GCUBERunningInstance.TYPE);
			if (purger != null) {
				try {
					logger.debug("Applying purger for "  + GCUBERunningInstance.TYPE);
					purger.purge(ri.getID(), scope);
				} catch (Exception e) {
					logger.error("Error while removing the profiles related to the resource",	e);
				}
			} else
				logger.trace("No purger found");	
		}

	}

	@Override
	public String getName() {
		return GCUBERunningInstance.TYPE;
	}
}

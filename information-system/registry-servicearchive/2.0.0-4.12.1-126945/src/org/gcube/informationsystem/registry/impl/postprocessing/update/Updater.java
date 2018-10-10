package org.gcube.informationsystem.registry.impl.postprocessing.update;

import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;

public interface Updater <RESOURCE extends GCUBEResource> {

	public void update(GCUBEResource resource, GCUBEScope scope);
	
	/**
	 * Gets the type of resource managed by the purgerE
	 * @return the type
	 */
	public String getName();
}

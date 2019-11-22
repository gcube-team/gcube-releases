package org.gcube.data.publishing.gCatFeeder.service.engine;

import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.catalogues.CataloguePlugin;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.CataloguePluginNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;

public interface CatalogueControllersManager {

	
	public Set<String> getAvailableControllers();
	public CataloguePlugin getPluginById(String collectorId) throws CataloguePluginNotFound;
	
	public void initInScope() throws InternalError;
}

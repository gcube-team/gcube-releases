package org.gcube.data.publishing.gCatFeeder.service.engine;

import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.service.model.fault.CollectorNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.gcube.data.publishing.gCatfeeder.collectors.CollectorPlugin;

public interface CollectorsManager {

	public Set<String> getAvailableCollectors();
	public CollectorPlugin<?> getPluginById(String collectorId) throws CollectorNotFound;
	
	public void initInScope() throws InternalError;
}

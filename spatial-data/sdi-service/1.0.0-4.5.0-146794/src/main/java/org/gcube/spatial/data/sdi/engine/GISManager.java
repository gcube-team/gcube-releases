package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;

public interface GISManager {

	public GeoServerClusterConfiguration getConfiguration() throws ConfigurationNotFoundException;
	
}

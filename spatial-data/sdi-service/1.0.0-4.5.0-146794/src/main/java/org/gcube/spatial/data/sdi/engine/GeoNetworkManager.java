package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkConfiguration;

public interface GeoNetworkManager {

	public GeoNetworkConfiguration getConfiguration() throws ConfigurationNotFoundException;
	
}

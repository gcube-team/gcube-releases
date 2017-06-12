package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;

public interface ThreddsManager {

	public ThreddsConfiguration getConfiguration() throws ConfigurationNotFoundException;
	
}

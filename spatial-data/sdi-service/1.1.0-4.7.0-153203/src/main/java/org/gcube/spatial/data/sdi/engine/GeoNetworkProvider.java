package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.sdi.engine.impl.faults.ClientInitializationException;

public interface GeoNetworkProvider {

	public GeoNetworkAdministration getGeoNetwork() throws ClientInitializationException;
	
}

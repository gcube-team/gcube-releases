package org.gcube.spatial.data.sdi.engine;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.gn.extension.GeoNetworkClient;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;
import org.gcube.spatial.data.sdi.model.services.GeoNetworkServiceDefinition;

public interface GeoNetworkManager extends GeoServiceManager<GeoNetworkDescriptor,GeoNetworkServiceDefinition>{


	public GeoNetworkClient getClient() throws ConfigurationNotFoundException;
	public GeoNetworkClient getClient(GeoNetworkDescriptor descriptor);
	
	
}

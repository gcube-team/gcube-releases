package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Singleton;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.sdi.engine.GeoNetworkProvider;
import org.gcube.spatial.data.sdi.engine.impl.faults.ClientInitializationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class GeoNetworkProviderImpl implements GeoNetworkProvider {

	@Override
	public GeoNetworkAdministration getGeoNetwork() throws ClientInitializationException{
		log.debug("Getting GeoNetwork .. ");
		try{
			return GeoNetwork.get();
		}catch(Exception e){
			throw new ClientInitializationException(e);
		}
	}

}

package org.gcube.spatial.data.sdi.test;

import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.sdi.engine.GeoNetworkProvider;
import org.gcube.spatial.data.sdi.engine.impl.GeoNetworkProviderImpl;
import org.gcube.spatial.data.sdi.engine.impl.faults.ClientInitializationException;
import org.glassfish.hk2.api.Factory;


public class GeoNetworkProviderFactory implements Factory<GeoNetworkProvider>{

	private static class GeoNetworkTestProvider extends GeoNetworkProviderImpl{
		
		@Override
		public GeoNetworkAdministration getGeoNetwork() throws ClientInitializationException {
			TokenSetter.set("/gcube/devsec");
			return super.getGeoNetwork();
		}
		
		
	}
	
	
	@Override
	public GeoNetworkProvider provide() {
		return new GeoNetworkTestProvider();
	}
	
	@Override
	public void dispose(GeoNetworkProvider arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

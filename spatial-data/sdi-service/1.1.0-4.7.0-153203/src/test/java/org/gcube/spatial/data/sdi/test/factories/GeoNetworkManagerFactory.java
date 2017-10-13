package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.impl.GeoNetworkManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkConfiguration;
import org.gcube.spatial.data.sdi.test.TestCommon;
import org.gcube.spatial.data.sdi.test.TokenSetter;
import org.glassfish.hk2.api.Factory;

public class GeoNetworkManagerFactory implements Factory<GeoNetworkManager>{

	private GeoNetworkManager manager;
	
	public GeoNetworkManagerFactory() {
		manager=new GeoNetworkManagerImpl(){
			@Override
			public GeoNetworkConfiguration getConfiguration() throws ConfigurationNotFoundException {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getConfiguration();
			}
		};
	}
	
	@Override
	public void dispose(GeoNetworkManager arg0) {
		// TODO Auto-generated method stub
	
	}
	
	@Override
	public GeoNetworkManager provide() {
		return manager;
	}
}

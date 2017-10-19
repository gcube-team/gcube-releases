package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.impl.GISManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;
import org.gcube.spatial.data.sdi.test.TestCommon;
import org.gcube.spatial.data.sdi.test.TokenSetter;
import org.glassfish.hk2.api.Factory;

public class GISManagerFactory implements Factory<GISManager>{

	private GISManager manager;
	
	public GISManagerFactory() {
		manager=new GISManagerImpl(){
			@Override
			public GeoServerClusterConfiguration getConfiguration() throws ConfigurationNotFoundException {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getConfiguration();
			}
		};
	}
	
	
	@Override
	public void dispose(GISManager arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public GISManager provide() {
		return manager;
	}
}

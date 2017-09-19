package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.engine.impl.SDIManagerImpl;
import org.gcube.spatial.data.sdi.model.ScopeConfiguration;
import org.gcube.spatial.data.sdi.model.health.HealthReport;
import org.gcube.spatial.data.sdi.test.TestCommon;
import org.gcube.spatial.data.sdi.test.TokenSetter;
import org.glassfish.hk2.api.Factory;

public class SDIManagerFactory implements Factory<SDIManager>{
	
	SDIManager manager=null;
	
	
	public SDIManagerFactory() {
		manager=new SDIManagerImpl(new GeoNetworkManagerFactory().provide(),new ThreddsManagerFactory().provide(),new GISManagerFactory().provide()){
			@Override
			public ScopeConfiguration getContextConfiguration() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getContextConfiguration();
			}
			@Override
			public HealthReport getHealthReport() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getHealthReport();
			}
		};
	}

	@Override
	public void dispose(SDIManager arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public SDIManager provide() {
		return manager;
	}
}

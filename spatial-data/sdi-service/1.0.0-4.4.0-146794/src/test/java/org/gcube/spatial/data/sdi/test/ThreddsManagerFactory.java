package org.gcube.spatial.data.sdi.test;

import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.ThreddsManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;
import org.glassfish.hk2.api.Factory;

public class ThreddsManagerFactory implements Factory<ThreddsManager>{

	private ThreddsManager manager;
	
	
	public ThreddsManagerFactory() {
		manager=new ThreddsManagerImpl(){
			@Override
			public ThreddsConfiguration getConfiguration() throws ConfigurationNotFoundException {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getConfiguration();
			}
		};
	}
	
	
	@Override
	public ThreddsManager provide() {
		return manager;
	}
	
	@Override
	public void dispose(ThreddsManager arg0) {
		// TODO Auto-generated method stub
		
	}
}

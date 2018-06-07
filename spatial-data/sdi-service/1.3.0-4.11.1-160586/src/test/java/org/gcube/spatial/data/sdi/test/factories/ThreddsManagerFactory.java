package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.ThreddsManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.ThreddsController;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;
import org.gcube.spatial.data.sdi.test.TestCommon;
import org.gcube.spatial.data.sdi.test.TokenSetter;
import org.glassfish.hk2.api.Factory;

public class ThreddsManagerFactory implements Factory<ThreddsManager>{

	private ThreddsManager manager;
	
	
	public ThreddsManagerFactory() {
		manager=new ThreddsManagerImpl(){
			@Override
			protected AbstractCluster<ThreddsDescriptor, ThreddsController> getCluster() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getCluster();
			}
			
			@Override
			protected ISModule getRetriever() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getRetriever();
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

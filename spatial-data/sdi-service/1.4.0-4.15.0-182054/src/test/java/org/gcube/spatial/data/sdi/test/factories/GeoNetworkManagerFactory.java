package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.impl.GeoNetworkManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoNetworkController;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;
import org.gcube.spatial.data.sdi.test.TestCommon;
import org.gcube.spatial.data.sdi.test.TokenSetter;
import org.glassfish.hk2.api.Factory;

public class GeoNetworkManagerFactory implements Factory<GeoNetworkManager>{

	private GeoNetworkManager manager;
	
	public GeoNetworkManagerFactory() {
		manager=new GeoNetworkManagerImpl(new RoleManagerFactory().provide()){
			@Override
			protected ISModule getRetriever() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getRetriever();
			}
			
			@Override
			protected AbstractCluster<GeoNetworkDescriptor, GeoNetworkController> getCluster() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getCluster();
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

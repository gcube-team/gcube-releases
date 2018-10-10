package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.impl.GISManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoServerCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoServerController;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.service.GeoServerDescriptor;
import org.gcube.spatial.data.sdi.test.TestCommon;
import org.gcube.spatial.data.sdi.test.TokenSetter;
import org.glassfish.hk2.api.Factory;

public class GISManagerFactory implements Factory<GISManager>{

	private GISManager manager;
	
	public GISManagerFactory() {
		manager=new GISManagerImpl(){
			
			@Override
			protected ISModule getRetriever() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getRetriever();
			}
			
			@Override
			protected AbstractCluster<GeoServerDescriptor, GeoServerController> getCluster() {
				TokenSetter.set(TestCommon.TEST_SCOPE);
				return super.getCluster();
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

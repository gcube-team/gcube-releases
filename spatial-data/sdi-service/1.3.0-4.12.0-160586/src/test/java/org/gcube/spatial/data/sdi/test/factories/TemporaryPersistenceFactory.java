package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.TemporaryPersistence;
import org.gcube.spatial.data.sdi.engine.impl.TemporaryPersistenceImpl;
import org.glassfish.hk2.api.Factory;

public class TemporaryPersistenceFactory implements Factory<TemporaryPersistence>{

	
	@Override
	public void dispose(TemporaryPersistence arg0) {
		arg0.shutdown();
	}
	
	@Override
	public TemporaryPersistence provide() {
		TemporaryPersistenceImpl temp=new TemporaryPersistenceImpl();
		
			temp.init();
		
		return temp;
	}
	
}

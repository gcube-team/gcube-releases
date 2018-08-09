package org.gcube.spatial.data.sdi.test.factories;

import org.gcube.spatial.data.sdi.engine.RoleManager;
import org.gcube.spatial.data.sdi.engine.impl.RoleManagerImpl;
import org.glassfish.hk2.api.Factory;

public class RoleManagerFactory implements Factory<RoleManager>{

	@Override
	public RoleManager provide() {
		return new RoleManagerImpl();
	}
	
	@Override
	public void dispose(RoleManager arg0) {
		// TODO Auto-generated method stub
		
	}
}

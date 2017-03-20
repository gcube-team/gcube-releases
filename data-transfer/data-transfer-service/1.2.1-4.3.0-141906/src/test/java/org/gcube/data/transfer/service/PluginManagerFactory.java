package org.gcube.data.transfer.service;

import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.impl.PluginManagerImpl;
import org.glassfish.hk2.api.Factory;

public class PluginManagerFactory implements Factory<PluginManager> {

	static PluginManagerImpl impl=new PluginManagerImpl();
	
	
	@Override
	public void dispose(PluginManager arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PluginManager provide() {
		return impl;
	}

}

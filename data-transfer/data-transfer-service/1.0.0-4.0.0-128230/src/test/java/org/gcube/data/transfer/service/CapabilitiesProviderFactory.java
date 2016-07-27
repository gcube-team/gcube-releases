package org.gcube.data.transfer.service;

import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;
import org.gcube.data.transfer.service.transfers.engine.impl.CapabilitiesProviderImpl;
import org.glassfish.hk2.api.Factory;

public class CapabilitiesProviderFactory implements Factory<CapabilitiesProvider> {
	
	@Override
	public void dispose(CapabilitiesProvider instance) {
		
	}
	
	@Override
	public CapabilitiesProvider provide() {
		return new CapabilitiesProviderImpl();
	}

}

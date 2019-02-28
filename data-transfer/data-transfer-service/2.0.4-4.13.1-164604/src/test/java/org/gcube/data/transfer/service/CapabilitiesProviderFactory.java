package org.gcube.data.transfer.service;

import java.util.Collections;
import java.util.HashSet;

import org.gcube.data.transfer.model.PluginDescription;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.TransferOptions;
import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.glassfish.hk2.api.Factory;

public class CapabilitiesProviderFactory implements Factory<CapabilitiesProvider> {
	
	@Override
	public void dispose(CapabilitiesProvider instance) {
		
	}
	
	@Override
	public CapabilitiesProvider provide() {
		return new CapabilitiesProvider(){
			@Override
			public TransferCapabilities get() {
				return new TransferCapabilities("12345", "localhost", 80, 
						Collections.singleton((TransferOptions)HttpDownloadOptions.DEFAULT),
						new HashSet<PluginDescription>(PluginManager.get().getInstalledPlugins().values())
						,Collections.singleton("data-transfer-service"));
			}
		};
	}

}

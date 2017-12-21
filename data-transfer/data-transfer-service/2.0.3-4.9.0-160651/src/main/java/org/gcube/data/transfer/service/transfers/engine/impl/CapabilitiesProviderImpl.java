package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.transfer.model.PluginDescription;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.TransferOptions;
import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;

import lombok.Synchronized;


@Singleton
public class CapabilitiesProviderImpl implements CapabilitiesProvider {

	
	
	private TransferCapabilities capabilities=null;
	
	private PersistenceProvider persistenceProvider;
	private PluginManager pluginManager;
	
	
	@Inject
	public CapabilitiesProviderImpl(PersistenceProvider persistenceProvider) {
		this.persistenceProvider = persistenceProvider;
		this.pluginManager=PluginManager.get();
	}

	@Override @Synchronized
	public TransferCapabilities get() {
		if(capabilities==null)capabilities=getCapabilities();
		return capabilities;
	}

	private TransferCapabilities getCapabilities(){
		ApplicationContext context=ContextProvider.get();		
		ContainerConfiguration configuration=context.container().configuration();
		
		String hostName=configuration.hostname();
		String id=context.profile(GCoreEndpoint.class).id();
		Integer port=configuration.port();
		
		HashSet<TransferOptions> meansOfTransfer=new HashSet<TransferOptions>();
		meansOfTransfer.add(HttpDownloadOptions.DEFAULT);
		
		
		HashSet<PluginDescription> plugins=new HashSet<PluginDescription>(pluginManager.getInstalledPlugins().values());
		
		
		return new TransferCapabilities(id,hostName,port,meansOfTransfer,plugins,persistenceProvider.getAvaileblContextIds());
	}
	
	
}

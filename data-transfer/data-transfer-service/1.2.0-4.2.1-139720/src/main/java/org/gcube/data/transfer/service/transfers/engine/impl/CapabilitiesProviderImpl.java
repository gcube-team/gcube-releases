package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.Collections;
import java.util.HashMap;
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
	public CapabilitiesProviderImpl(PersistenceProvider persistenceProvider,PluginManager pluginManager) {
		super();
		this.persistenceProvider = persistenceProvider;
		this.pluginManager=pluginManager;
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
	
//	private static String getHostname() throws Exception {
//        String OS = System.getProperty("os.name").toLowerCase();
//        log.debug("Getting hostname..");
//        String hostName=null;
//        if (OS.indexOf("win") >= 0) {
//        	log.debug("Detected windows..");        	
//            hostName=System.getenv("COMPUTERNAME");
//            if(hostName==null || hostName.equals("")){
//            	log.debug("System env not found, trying via hostname command..");
//            	hostName=execReadToString("hostname");
//            }
//        } else 
//            if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0) {
//            	log.debug("Detected linux..");
//                hostName= System.getenv("HOSTNAME");
//                if(hostName==null || hostName.equals("")){
//                	log.debug("System env not found, trying via hostname command..");
//                	hostName=execReadToString("hostname -f");
//                }
//                if(hostName==null || hostName.equals("")){
//                	log.debug("Hostname command didn't work, trying via hostname file..");
//                	hostName=execReadToString("cat /etc/hostname");
//                }                
//            }else throw new Exception("OS not detected");
//        return hostName;
//    }
	
	

//    public static String execReadToString(String execCommand) throws IOException {
//        Process proc = Runtime.getRuntime().exec(execCommand);
//        try (InputStream stream = proc.getInputStream()) {
//            try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
//                return s.hasNext() ? s.next() : "";
//            }
//        }
//    }
	
}

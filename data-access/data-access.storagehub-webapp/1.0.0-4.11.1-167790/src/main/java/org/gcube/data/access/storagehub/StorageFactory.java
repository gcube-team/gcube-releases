package org.gcube.data.access.storagehub;

import java.util.Map;
import java.util.WeakHashMap;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageFactory {

	public final static String SERVICE_NAME 				= "home-library";	
	public final static String SERVICE_CLASS 				= "org.gcube.portlets.user";
	
	private static Map<String, IClient> clientUserMap = new WeakHashMap<String, IClient>();
	
	private static Logger log = LoggerFactory.getLogger(StorageFactory.class);
	
	public static IClient getGcubeStorage(){
		String login = AuthorizationProvider.instance.get().getClient().getId();
		if (!clientUserMap.containsKey(login)){
			IClient storage = new StorageClient(SERVICE_CLASS, SERVICE_NAME,
					login, AccessType.SHARED, MemoryType.PERSISTENT).getClient();	
			log.info("******* Storage activateProtocol for Storage **********");
			Handler.activateProtocol();	
			clientUserMap.put(login, storage);
			return storage;
		} else return clientUserMap.get(login);
	}

}

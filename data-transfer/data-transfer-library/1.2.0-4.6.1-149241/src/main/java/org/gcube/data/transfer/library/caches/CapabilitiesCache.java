package org.gcube.data.transfer.library.caches;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import org.gcube.data.transfer.library.client.Client;
import org.gcube.data.transfer.model.TransferCapabilities;

@Slf4j
public class CapabilitiesCache extends TTLCache<TransferCapabilities> {

	private static CapabilitiesCache instance=null;
	
//	@Synchronized
//	public static CapabilitiesCache getInstance(){
//		if(instance==null)instance=new CapabilitiesCache();
//		return instance;
//	}
	
	private CapabilitiesCache(){
		super(5*60*1000l,2*60*1000l,"Capabilities");
	}
	
	
	@Override
	protected TransferCapabilities getNew(String id) throws Exception{
		log.debug("Getting capabilties for host "+id);
		return new Client(id).getCapabilties();
	}

}

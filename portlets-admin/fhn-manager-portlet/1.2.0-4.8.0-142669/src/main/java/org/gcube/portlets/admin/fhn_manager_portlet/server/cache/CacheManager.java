package org.gcube.portlets.admin.fhn_manager_portlet.server.cache;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.server.Context;
import org.gcube.portlets.admin.fhn_manager_portlet.server.UserInformation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheManager {

	private static final Map<String,Cache> caches=new HashMap<String,Cache>();
	
	
	public static synchronized Cache getCache(UserInformation userInfo){
		String currentScope=userInfo.getContext();
		log.debug("Accessing cache for scope {} ",currentScope);
		if(!caches.containsKey(currentScope)){
			log.info("Creating new cache for current scope {} ",currentScope);
			caches.put(currentScope,new Cache(Context.get())); 
		}
		return caches.get(currentScope);				
	}
	
	
}

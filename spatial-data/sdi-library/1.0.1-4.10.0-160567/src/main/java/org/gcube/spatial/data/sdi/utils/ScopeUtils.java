package org.gcube.spatial.data.sdi.utils;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScopeUtils {

	public static String getCurrentScope(){
		try{
			String token=SecurityTokenProvider.instance.get();
			log.debug("Token is : "+token);
			if(token==null) throw new Exception("Security Token is null");
			AuthorizationEntry entry = authorizationService().get(token);
			return entry.getContext();
		}catch(Exception e ){
			log.debug("Unable to resolve token, checking scope provider..",e);
			return ScopeProvider.instance.get();
		}
	}
	
	
	public static String getCurrentCaller(){
		try{
			String token=SecurityTokenProvider.instance.get();
			log.debug("Token is : "+token);
			if(token==null) throw new Exception("Security Token is null");
			AuthorizationEntry entry = authorizationService().get(token);
			return entry.getClientInfo().getId();
		}catch(Exception e ){
			log.debug("Unable to resolve token, checking scope provider..",e);
			return "Unidentified data-transfer user";
		}
	}
}
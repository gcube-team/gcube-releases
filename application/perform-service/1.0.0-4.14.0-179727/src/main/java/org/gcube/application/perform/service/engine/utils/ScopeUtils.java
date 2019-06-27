package org.gcube.application.perform.service.engine.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ScopeUtils {

	private static final Logger log= LoggerFactory.getLogger(ScopeUtils.class);

	
	
	public static String getCurrentScope(){
		//		try{
		//			String token=SecurityTokenProvider.instance.get();
		//			log.debug("Token is : "+token);
		//			if(token==null) throw new Exception("Security Token is null");
		//			AuthorizationEntry entry = authorizationService().get(token);
		//			return entry.getContext();
		//		}catch(Exception e ){
		//			log.debug("Unable to resolve token, checking scope provider..",e);
		//			return ScopeProvider.instance.get();
		//		}

		String scope=ScopeProvider.instance.get();
		if(scope!=null) {
			log.debug("Found scope provider {}, skipping token",scope);
			return scope;
		}else{
			try{
				log.debug("Scope provider not set, reverting to token");
				String token=SecurityTokenProvider.instance.get();
				log.debug("Token is : "+token);
				if(token==null) throw new Exception("Security Token is null");
				AuthorizationEntry entry = authorizationService().get(token);
				return entry.getContext();
			}catch(Exception e){
				throw new RuntimeException("Unable to evaluate scope ",e);				
			}
		}
	}

	
	public static String getCaller() {
		return SecurityTokenProvider.instance.get();
	}
	
	
	public static String getClientId(String token) throws ObjectNotFound, Exception {
		AuthorizationEntry entry = authorizationService().get(token);
		return entry.getClientInfo().getId();
	}
}

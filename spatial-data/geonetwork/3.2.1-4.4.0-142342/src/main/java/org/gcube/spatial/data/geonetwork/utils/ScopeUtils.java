package org.gcube.spatial.data.geonetwork.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.HashSet;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScopeUtils {

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

	public static String getCurrentScopeName(){
		String current=getCurrentScope();
		return current.substring(current.lastIndexOf('/')+1);
	}

	public static ArrayList<String> getParentScopes(){
		String currentScope=getCurrentScope();
		String[] splitted=currentScope.substring(1).split("/");
		ArrayList<String> toReturn=new ArrayList<String>();
		for(int i=0;i<splitted.length-1;i++){
			toReturn.add(splitted[i]);
		}
		return toReturn;
	}
}

package org.gcube.data.publishing.gCatFeeder.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextUtils {

	private static final Logger log= LoggerFactory.getLogger(ContextUtils.class);
	
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


	public static String getCurrentScope(){

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
		return getScopeName(getCurrentScope());		
	}

	public static String getScopeName(String scope) {
		return scope.substring(scope.lastIndexOf('/')+1); 
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

	public static String getParentScope(String scope) {		
		if(scope.lastIndexOf("/")==0) return null; // NO PARENT
		else return scope.substring(0, scope.lastIndexOf("/"));
	}
	
	public static ArrayList<String> getParentScopes(String scope){		
		String[] splitted=scope.substring(1).split("/");
		ArrayList<String> toReturn=new ArrayList<String>();
		for(int i=0;i<splitted.length-1;i++){
			toReturn.add(splitted[i]);
		}
		return toReturn;
	}
	
	
}

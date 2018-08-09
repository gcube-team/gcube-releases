package org.gcube.data.analysis.statisticalmanager.util;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.HashSet;
import java.util.Set;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScopeUtils {

	@Data
	@AllArgsConstructor
	@ToString
	@EqualsAndHashCode
	public static class ScopeBean{
		String scopeProviderValue;
		String tokenValue;
	}
	
	public static ScopeBean getCurrentScopeBean(){
		return new ScopeBean(ScopeProvider.instance.get(),SecurityTokenProvider.instance.get());
	}
	
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
				String token=getToken();
				log.debug("Token is : "+token);
				if(token==null) throw new Exception("Security Token is null");
				AuthorizationEntry entry = authorizationService().get(token);
				return entry.getContext();
			}catch(Exception e){
				throw new RuntimeException("Unable to evaluate scope ",e);				
			}
		}
	}

	public static String getToken(){
		return SecurityTokenProvider.instance.get();
	}
	
	public static String getCurrentScopeName(){
		String current=getCurrentScope();
		return current.substring(current.lastIndexOf('/')+1);
	}

	public static Set<String> getParentScopes(){
		String currentScope=getCurrentScope();
		String[] splitted=currentScope.substring(1).split("/");
		HashSet<String> toReturn=new HashSet<String>();
		for(int i=0;i<splitted.length-1;i++){
			toReturn.add(splitted[i]);
		}
		return toReturn;
	}
	
	public static void setAuthorizationSettings(ScopeBean bean){
		log.debug("Setting {}",bean);
		ScopeProvider.instance.set(bean.scopeProviderValue);
		try{
			SecurityTokenProvider.instance.set(bean.tokenValue);		
		}catch(Throwable e){
			log.warn("Unable to set token, bean is :"+bean,e);
			SecurityTokenProvider.instance.reset();
		}
	}
	
	public static void cleanAuthorizationSettings(){
		ScopeProvider.instance.reset();
		SecurityTokenProvider.instance.reset();
	}
}

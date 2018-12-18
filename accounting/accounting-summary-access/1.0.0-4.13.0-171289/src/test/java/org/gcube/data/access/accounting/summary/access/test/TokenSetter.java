package org.gcube.data.access.accounting.summary.access.test;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.Properties;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenSetter{

	private static Properties props=null;

	static{

	}


	public static synchronized void set(String scope){
		try{
			if(props==null) {
				props=new Properties();
				try {
					props.load(TokenSetter.class.getResourceAsStream("/tokens.properties"));
				} catch (Exception e) {
					throw new RuntimeException("YOU NEED TO SET TOKEN FILE IN CONFIGURATION");
				}
			}
			if(!props.containsKey(scope)) throw new Exception("No token found for scope : "+scope);
			SecurityTokenProvider.instance.set(props.getProperty(scope));
		}catch(Throwable e){
			log.trace("Unable to set token for scope "+scope,e);
		}
		ScopeProvider.instance.set(scope);
	}


	public static void setToken(String token){
		try{
			AuthorizationEntry entry = authorizationService().get(token);
			ScopeProvider.instance.set(entry.getContext());
			SecurityTokenProvider.instance.set(token);
		}catch(Throwable t) {
			throw new RuntimeException("Unable to set token "+token,t);
		}
	}

	public static String getCurrentToken() {
		return SecurityTokenProvider.instance.get();
	}
}
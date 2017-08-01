package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;

public class TokenManager {

	String username;
	String scope;
	String token;
	
	public String getScope(){
		return scope;
	}

	public String getUserName(){
		return username;
	}
	
	public String getToken(){
		return token;
	}
	
	public void getCredentials() {
		try{
			System.out.println("Retrieving token credentials");
			//scope = ScopeProvider.instance.get();
			System.out.println("Credentials from the GHN: scope: "+scope);
			//get username from SmartGears
			ClientInfo tokenInfo = (UserInfo) AuthorizationProvider.instance.get().getClient();
			username = tokenInfo.getId();
			token = SecurityTokenProvider.instance.get();
			AuthorizationEntry entry = authorizationService().get(token);
			scope = entry.getContext();
			
			System.out.println("Credentials from the GHN: user: "+username+" , "+scope+" , "+token);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Error Retrieving token credentials: "+e.getLocalizedMessage());
				scope = null;
				username= null;
				
			}
			if ((scope==null || username==null) && ConfigurationManager.isSimulationMode()){
				scope = ConfigurationManager.defaultScope;
				username = ConfigurationManager.defaultUsername;
			}
			System.out.println("Retrieved scope: "+scope+" Username: "+username +" Token "+token+" SIMULATION MODE: "+ConfigurationManager.isSimulationMode());

	}

}

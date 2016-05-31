package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;

public class TokenManager {

	String username;
	String scope;
	
	
	public String getScope(){
		return scope;
	}

	public String getUserName(){
		return username;
	}
	
	public void getCredentials() {
		try{
			System.out.println("Retrieving token credentials");
			scope = ScopeProvider.instance.get();
			System.out.println("Credentials from the GHN: scope: "+scope);
			//get username from SmartGears
			UserInfo token = AuthorizationProvider.instance.get();
			username = token.getUserName();
			System.out.println("Credentials from the GHN: user: "+username);
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
			System.out.println("Retrieved scope: "+scope+" Username: "+username +" SIMULATION MODE: "+ConfigurationManager.isSimulationMode());

	}

}

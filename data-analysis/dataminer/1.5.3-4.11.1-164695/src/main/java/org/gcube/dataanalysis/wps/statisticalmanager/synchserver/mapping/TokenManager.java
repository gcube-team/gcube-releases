package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenManager {

	private static final Logger LOGGER= LoggerFactory.getLogger(TokenManager.class);

	String username;
	String scope;
	String token;
	String tokenQualifier;
	
	public String getScope(){
		return scope;
	}

	public String getUserName(){
		return username;
	}

	public String getToken(){
		return token;
	}
	
	public String getTokenQualifier() {
		return tokenQualifier;
	}

	public void getCredentials() {
		try{
			LOGGER.debug("Retrieving token credentials");
			//get username from SmartGears
			username = AuthorizationProvider.instance.get().getClient().getId();
			token = SecurityTokenProvider.instance.get();
			AuthorizationEntry entry = authorizationService().get(token);
			scope = entry.getContext();
			tokenQualifier = entry.getQualifier();

		}catch(Exception e){
			LOGGER.error("Error Retrieving token credentials ",e);
			scope = null;
			username= null;

		}
		if ((scope==null || username==null) && ConfigurationManager.isSimulationMode()){
			scope = ConfigurationManager.defaultScope;
			username = ConfigurationManager.defaultUsername;
		}
		LOGGER.debug("Retrieved scope: {} Username: {} Token {} SIMULATION MODE: {} ",scope, username, token, ConfigurationManager.isSimulationMode());

	}

}

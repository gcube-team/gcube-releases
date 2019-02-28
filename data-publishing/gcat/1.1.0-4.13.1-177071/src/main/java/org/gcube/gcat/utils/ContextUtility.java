package org.gcube.gcat.utils;

import javax.ws.rs.InternalServerErrorException;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.ClientType;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) 
 */
public class ContextUtility {
	
	private static Logger logger = LoggerFactory.getLogger(ContextUtility.class);
	
	public static void setContext(String token) throws ObjectNotFound, Exception{
		SecurityTokenProvider.instance.set(token);
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		ClientInfo clientInfo = authorizationEntry.getClientInfo();
		logger.debug("User : {} - Type : {}", clientInfo.getId(), clientInfo.getType().name());
		String qualifier = authorizationEntry.getQualifier();
		Caller caller = new Caller(clientInfo, qualifier);
		AuthorizationProvider.instance.set(caller);
		ScopeProvider.instance.set(getCurrentContext());
	}
	
	public static String getCurrentContext() {
		try {
			String token = SecurityTokenProvider.instance.get();
			return Constants.authorizationService().get(token).getContext();
		}catch (Exception e) {
			String context = ScopeProvider.instance.get();
			if(context!=null) {
				return context;
			}
			throw new InternalServerErrorException(e);
		}
	}
	
	public static ClientInfo getClientInfo() {
		try {
			Caller caller = AuthorizationProvider.instance.get();
			if(caller!=null){
				return caller.getClient();
			}else{
				String token = SecurityTokenProvider.instance.get();
				AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
				return authorizationEntry.getClientInfo();
			}
		}catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	private static final String GET_USERNAME_ERROR = "Unable to retrieve user";
	
	public static String getUsername() {
		try {
			return getClientInfo().getId();
		} catch (Exception e) {
			logger.error(GET_USERNAME_ERROR);
			throw new InternalServerErrorException(GET_USERNAME_ERROR, e);
		}
	}
	
	public static boolean isApplication() {
		try {
			ClientInfo clientInfo = getClientInfo();
			return clientInfo.getType() == ClientType.EXTERNALSERVICE ;
		}catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
}

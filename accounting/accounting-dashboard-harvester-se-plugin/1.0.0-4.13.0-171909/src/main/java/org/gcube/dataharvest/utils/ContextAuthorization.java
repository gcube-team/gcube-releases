package org.gcube.dataharvest.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.resourcemanagement.support.server.managers.context.ContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextAuthorization {
	
	private static Logger logger = LoggerFactory.getLogger(ContextAuthorization.class);
	
	public static final String USERNAME = "USERNAME";
	public static final String DEFAULT_USERNAME = "luca.frosini";
	
	public static final String SERVICE_NAME = "SERVICE_NAME";
	public static final String DEFAULT_SERVICE_NAME = "accounting-harvester";
	
	/**
	 * Contains Context full name as key and Token as Value
	 */
	protected Map<String,String> contextToToken;
	
	/**
	 * Contains Token as key and Context full name as Value
	 */
	protected Map<String,String> tokenToContext;
	
	/**
	 * Contains  Properties used to generate tokens
	 */
	public ContextAuthorization() throws Exception {
		this.contextToToken = new HashMap<>();
		this.tokenToContext = new HashMap<>();
		retrieveContextsAndTokens();
	}
	
	protected void retrieveContextsAndTokens() throws Exception {
		
		String initialToken = SecurityTokenProvider.instance.get();
		
		try {
			
			Properties properties = AccountingDataHarvesterPlugin.getProperties().get();
			
			LinkedHashMap<String,ScopeBean> map = ContextManager.readContexts();
			for(String scope : map.keySet()) {
				try {
					String context = map.get(scope).toString();
					logger.info("Going to generate Token for Context {}", context);
					UserInfo userInfo = new UserInfo(properties.getProperty(USERNAME, DEFAULT_USERNAME),
							new ArrayList<>());
					String userToken = authorizationService().generateUserToken(userInfo, context);
					SecurityTokenProvider.instance.set(userToken);
					String generatedToken = authorizationService()
							.generateExternalServiceToken(properties.getProperty(SERVICE_NAME, DEFAULT_SERVICE_NAME));
					
					logger.trace("Token for Context {} is {}", context, generatedToken);
					
					contextToToken.put(context, generatedToken);
					tokenToContext.put(generatedToken, context);
					
				} catch(Exception e) {
					logger.error("Error while elaborating {}", scope, e);
					throw e;
				} finally {
					SecurityTokenProvider.instance.reset();
				}
				
			}
		} catch(Exception ex) {
			throw ex;
		} finally {
			SecurityTokenProvider.instance.set(initialToken);
		}
	}
	
	public String getTokenForContext(String contextFullName) {
		return contextToToken.get(contextFullName);
	}
	
	public String getContextFromToken(String token) {
		return tokenToContext.get(token);
	}
	
	public SortedSet<String> getContexts() {
		return new TreeSet<String>(contextToToken.keySet());
	}
	
}

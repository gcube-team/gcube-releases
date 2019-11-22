package org.gcube.portlets.user.tokengenerator.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.shared.TokenBean;
import org.gcube.portlets.user.tokengenerator.shared.TokenType;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TokenServiceImpl extends RemoteServiceServlet implements TokenService {

	//private final static Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
	private static final Log logger = LogFactoryUtil.getLog(TokenServiceImpl.class);
	private final static GroupManager groupManager = new LiferayGroupManager();
	private final static String APP_TOKEN_ENABLED_SESSION = "SHOW_APPLICATION_TOKEN";

	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			logger.trace("Development Mode ON");
			return false;
		}			
	}

	/**
	 * Retrieve the current user by using the portal manager
	 * @return a GcubeUser object
	 */
	private GCubeUser getCurrentUser(HttpServletRequest request){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser user = pContext.getCurrentUser(request);
		logger.debug("Returning user " + user);
		return user;
	}

	/**
	 * Retrieve the current scope by using the portal manager
	 * @param b 
	 * @return a GcubeUser object
	 */
	private String getCurrentContext(HttpServletRequest request){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		String context = pContext.getCurrentScope(request);
		logger.debug("Returning context " + context);
		return context;
	}

	/**
	 * Get current user token
	 * @param request
	 * @return
	 */
	public static String getCurrentSecurityToken(HttpServletRequest request) {
		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();

		String token = pContext.getCurrentUserToken(pContext.getCurrentScope(request), pContext.getCurrentUser(request).getUserId());
		logger.debug("Returning token " + token);

		return token;
	}

	@Override
	public TokenBean getServiceToken() {
		String username = getCurrentUser(getThreadLocalRequest()).getUsername();
		String context = getCurrentContext(getThreadLocalRequest());
		if(!isWithinPortal()){
			logger.debug("Returing test token since you are running in test mode");
			return new TokenBean(TokenType.CONTEXT, username, getCurrentSecurityToken(getThreadLocalRequest()), context, null, null);
		}
		try{
			long currentGroupId = groupManager.getGroupIdFromInfrastructureScope(context);
			String contextName = groupManager.getGroup(currentGroupId).getGroupName();
			String token = SecurityTokenProvider.instance.get();
			if(token != null && !token.isEmpty()){				
				logger.debug("Token was already set");
			}else{
				token = tryGetElseCreateToken(username, context);
			}
			return new TokenBean(TokenType.CONTEXT, username, token, context, contextName, null);
		}catch(Exception e){
			logger.error("Unable to ask token for user " + username + " in scope " + context, e);
		}
		return null;
	}

	/**
	 * First check to retrieve the token, else create it
	 * @param username
	 * @param context
	 * @return the user token for the context
	 */
	private String tryGetElseCreateToken(String username, String context) {
		String token = null;
		try{
			try{
				logger.debug("Checking if token for user " + username + " in context " + context + " already exists...");
				token = authorizationService().resolveTokenByUserAndContext(username, context);
				logger.debug("It exists!");
			}catch(ObjectNotFound e){
				logger.info("Creating token for user " + username + " and context " + context);
				token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
				logger.debug("received token: "+ token.substring(0, 5) + "***********************");
			}
		}catch(Exception e){
			logger.error("Failed both token retrieval and creation", e);
		}
		return token;
	}

	@Override
	public List<TokenBean> getQualifiedTokens() {

		String username = getCurrentUser(getThreadLocalRequest()).getUsername();
		String context = getCurrentContext(getThreadLocalRequest());
		List<TokenBean> toReturn = new ArrayList<TokenBean>();

		try{
			if(!isWithinPortal()){

				logger.debug("Returning test qualified tokens");
				toReturn = new ArrayList<TokenBean>();
				toReturn.add(new TokenBean(TokenType.QUALIFIED, username, UUID.randomUUID().toString(), context, null, "aaaa"));
				toReturn.add(new TokenBean(TokenType.QUALIFIED, username, UUID.randomUUID().toString(), context, null, "bbbb"));
				toReturn.add(new TokenBean(TokenType.QUALIFIED, username, UUID.randomUUID().toString(), context, null, "cccc"));
				toReturn.add(new TokenBean(TokenType.QUALIFIED, username, UUID.randomUUID().toString(), context, null, "dddd"));

			}else{
				Map<String, String> keys = authorizationService().retrieveApiKeys();
				Iterator<Entry<String, String>> iteratorMap = keys.entrySet().iterator();
				toReturn = new ArrayList<TokenBean>();
				while (iteratorMap.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) iteratorMap
							.next();
					toReturn.add(new TokenBean(TokenType.QUALIFIED, username, entry.getValue(), context, null, entry.getKey()));
				}
				logger.debug("Returning qualified tokens " + toReturn);
			}
		}catch(Exception e){
			logger.error("Failed to retrieve user's qualified tokens. Returning null", e);
			return null;
		}

		return toReturn;
	}

	@Override
	public TokenBean createQualifiedToken(String qualifier) {
		String username = getCurrentUser(getThreadLocalRequest()).getUsername();
		String context = getCurrentContext(getThreadLocalRequest());
		try{
			if(!isWithinPortal()){
				return new TokenBean(TokenType.QUALIFIED, username, UUID.randomUUID().toString(), null, null, qualifier);
			}else{
				if(SecurityTokenProvider.instance.get() == null){
					SecurityTokenProvider.instance.set(tryGetElseCreateToken(username, context));
				}
				String createdToken = authorizationService().generateApiKey(qualifier);
				logger.debug("Qualified token create is " + createdToken.substring(0, 5) + "***********************");
				return new TokenBean(TokenType.QUALIFIED, username, createdToken, context, null, qualifier);
			}
		}catch(Exception e){
			logger.error("Failed to create qualified token for user", e);
		}

		return null;
	}

	@Override
	public TokenBean createApplicationToken(String applicationIdentifier) {

		GCubeUser user = getCurrentUser(getThreadLocalRequest());
		String context = getCurrentContext(getThreadLocalRequest());

		logger.info("Request coming for generating an application token having has identifier " + applicationIdentifier +". "
				+ "User is " + user.getUsername() + " and context " + context);

		try{
			String token = authorizationService().generateExternalServiceToken(applicationIdentifier);
			logger.info("Token generated " + token.substring(0, 6) + "***************");
			return new TokenBean(TokenType.APPLICATION, user.getUsername(), token, context, null, applicationIdentifier);
		}catch(Exception e){
			logger.error("Failed application token creation...", e);
		}
		return null;
	}

	@Override
	public List<TokenBean> getApplicationTokens() {

		String username = getCurrentUser(getThreadLocalRequest()).getUsername();
		String context = getCurrentContext(getThreadLocalRequest());
		List<TokenBean> toReturn = new ArrayList<TokenBean>();

		try{
			if(!isWithinPortal()){

				logger.debug("Returning test application tokens");
				toReturn = new ArrayList<TokenBean>();
				toReturn.add(new TokenBean(TokenType.APPLICATION, username, UUID.randomUUID().toString(), context, null, "aaaa"));
				toReturn.add(new TokenBean(TokenType.APPLICATION, username, UUID.randomUUID().toString(), context, null, "bbbb"));
				toReturn.add(new TokenBean(TokenType.APPLICATION, username, UUID.randomUUID().toString(), context, null, "cccc"));
				toReturn.add(new TokenBean(TokenType.APPLICATION, username, UUID.randomUUID().toString(), context, null, "dddd"));

			}else{
				Map<String, String> keys = authorizationService().retrieveExternalServiceGenerated();
				Iterator<Entry<String, String>> iteratorMap = keys.entrySet().iterator();
				toReturn = new ArrayList<TokenBean>();
				while (iteratorMap.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) iteratorMap
							.next();
					toReturn.add(new TokenBean(TokenType.QUALIFIED, username, entry.getValue(), context, null, entry.getKey()));
				}
				logger.debug("Returning application tokens " + toReturn);
			}
		}catch(Exception e){
			logger.error("Failed to retrieve user's application tokens. Returning null", e);
			return null;
		}

		return toReturn;
	}

	@Override
	public boolean isTokenAppVisibile() {
		if(!isWithinPortal())
			return false;
		else{
			String keyPerContext = APP_TOKEN_ENABLED_SESSION + getCurrentContext(getThreadLocalRequest());
			Boolean attribute = (Boolean)getThreadLocalRequest().getSession().getAttribute(keyPerContext);
			if(attribute != null)
				return attribute;
			else{
				try{
					GCubeUser currentUser = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest());
					RoleManager rm = new LiferayRoleManager();
					GroupManager gm = new LiferayGroupManager();
					boolean isAdmin = rm.isAdmin(currentUser.getUserId());
					logger.debug("Is the user an admin? " + isAdmin);
					List<GCubeRole> roles = rm.listRolesByUserAndGroup(currentUser.getUserId(), gm.getGroupIdFromInfrastructureScope(getCurrentContext(getThreadLocalRequest())));
					boolean isVreManager = false;

					for (GCubeRole gCubeRole : roles) {
						if(gCubeRole.getRoleName().equals(GCubeRole.VRE_MANAGER_LABEL)){
							isVreManager = true;
							break;
						}
					}

					attribute = new Boolean(isAdmin | isVreManager);
					getThreadLocalRequest().getSession().setAttribute(keyPerContext, attribute);
					return attribute;
				}catch(Exception e){
					logger.error("Failed to check if user is administrator ", e);
					return false;
				}
			}

		}
	}

}

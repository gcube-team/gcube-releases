package org.gcube.portlets.user.tokengenerator.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.shared.QualifiedToken;
import org.gcube.portlets.user.tokengenerator.shared.TokenBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TokenServiceImpl extends RemoteServiceServlet implements TokenService {

	private final static Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
	public final static String TEST_USER = "test.user";
	private final static String TEST_SCOPE = "/gcube/devsec";
	private final static String TEST_TOKEN_AUTH2 = "afdaa1c6-493b-405e-801d-b219e056f564|98187548";
	private final static GroupManager groupManager = new LiferayGroupManager();

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {
			logger.warn("USER IS NULL setting test.user");
			user = getTestUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(TEST_SCOPE);
		}
		else {
			logger.info("LIFERAY PORTAL DETECTED user=" + user);
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

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
	 * Get the test user
	 * @return
	 */
	public String getTestUser(){

		String user = TEST_USER;
		//		user = "costantino.perciante";
		return user;
	}

	@Override
	public TokenBean getServiceToken() {
		ASLSession aslSession = getASLSession();
		String username = aslSession.getUsername();
		String context = aslSession.getScope();

		if (username.compareTo(TEST_USER) == 0)
			return null;

		try{

			if(!isWithinPortal()){

				logger.debug("Returing test token since you are running in test mode");
				return new TokenBean(username, TEST_TOKEN_AUTH2, TEST_SCOPE);

			}
			
			// evaluate context name 
			long currentGroupId = groupManager.getGroupIdFromInfrastructureScope(context);
			String contextName = groupManager.getGroup(currentGroupId).getGroupName();

			logger.debug("Asking token");
			logger.debug("calling service token on scope " + context + " and group name " + contextName);
			List<String> userRoles = new ArrayList<>();
			String token = authorizationService().generateUserToken(new UserInfo(username, userRoles), context);
			logger.debug("received token: "+ token.substring(0, 5) + "***********************");
			return new TokenBean(username, token, contextName);
		}catch(Exception e){
			logger.error("Unable to ask token for user " + username + " in scope " + context, e);
		}

		return null;
	}

	@Override
	public List<QualifiedToken> getQualifiedTokens() {

		List<QualifiedToken> toReturn = new ArrayList<QualifiedToken>();

		ASLSession aslSession = getASLSession();
		String username = aslSession.getUsername();

		if (username.compareTo(TEST_USER) == 0)
			return toReturn;

		try{
			if(!isWithinPortal()){

				logger.debug("Returning test qualified tokens");
				toReturn = new ArrayList<QualifiedToken>();
				toReturn.add(new QualifiedToken("aaaa", UUID.randomUUID().toString()));
				toReturn.add(new QualifiedToken("bbbb", UUID.randomUUID().toString()));
				toReturn.add(new QualifiedToken("cccc", UUID.randomUUID().toString()));
				toReturn.add(new QualifiedToken("dddd", UUID.randomUUID().toString()));

			}else{

				// a map <qualifier, token>
				Map<String, String> keys = authorizationService().retrieveApiKeys();
				Iterator<Entry<String, String>> iteratorMap = keys.entrySet().iterator();
				toReturn = new ArrayList<QualifiedToken>();
				while (iteratorMap.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) iteratorMap
							.next();
					toReturn.add(new QualifiedToken(entry.getKey(), entry.getValue()));
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
	public QualifiedToken createQualifiedToken(String qualifier) {

		ASLSession session = getASLSession();
		String username = session.getUsername();

		try{
			if(!isWithinPortal()){
				return new QualifiedToken(qualifier, UUID.randomUUID().toString());
			}else{

				if (username.compareTo(TEST_USER) == 0)
					return null;

				// note that the standard token (the one retrievable by getServiceToken()) 
				// must be set to create a new qualified token
				SecurityTokenProvider.instance.set(getServiceToken().getToken());
				String createdToken = authorizationService().generateApiKey(qualifier);
				logger.debug("Qualified token create is " + createdToken.substring(0, 5) + "***********************");
				return new QualifiedToken(qualifier, createdToken);
			}
		}catch(Exception e){
			logger.error("Failed to create qualified token for user", e);
		}

		return null;
	}

}

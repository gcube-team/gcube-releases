package org.gcube.portlets.admin.tokengeneratoradmin.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.http.conn.util.InetAddressUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.tokengeneratoradmin.client.TokenServiceAdmin;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.NodeToken;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.PortRange;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TokenServiceAdminImpl extends RemoteServiceServlet implements TokenServiceAdmin {

	private final static Logger logger = LoggerFactory.getLogger(TokenServiceAdminImpl.class);
	private final static String TEST_USER = "test.user";
	private final static String TEST_SCOPE = "/gcube/devsec";

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
	private String getTestUser(){

		String user = TEST_USER;
		//		user = "costantino.perciante";
		return user;
	}

	@Override
	public NodeToken createNodeToken(String ipaddress, short port, String context) {
		logger.debug("Request for creating new NodeToken for port " + port + " and node " + ipaddress + " in the context " + context);

		ASLSession session = getASLSession();

		if(session.getUsername().equals(TEST_USER)){
			logger.error("Session expired, you cannot create a token without beeing logged");
			return null;
		}

		if(context == null || context.isEmpty()){
			logger.error("The provided context is null or empty");
			return null;
		}

		if(!isValidIp(ipaddress)){
			logger.error("Ip Address " + ipaddress + " is not valid");
			return new NodeToken("Please check the inserted node address. It doesn't seem a valid IP/IP6 address");
		}

		// save old token
		String oldToken = SecurityTokenProvider.instance.get();
		String username = session.getUsername();

		try{

			if(!isWithinPortal())
				return new NodeToken(UUID.randomUUID().toString(), context, ipaddress, port);
			else{

				// set the authorization token of the user for the chosen scope, then generate the node one.
				String tokenUserForContext = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
				SecurityTokenProvider.instance.set(tokenUserForContext);
				String tokenNode = authorizationService().requestActivation(new ContainerInfo(ipaddress, port), context);
				return new NodeToken(tokenNode, context, ipaddress, port);
			}

		}catch(Exception e){
			logger.error("Unable to create NodeToken", e);
		}finally{
			SecurityTokenProvider.instance.set(oldToken); // set back the old token
		}

		return new NodeToken("Unable to create Node Token, retry later");
	}

	/**
	 * @param ip the ip
	 * @return check if the ip is valid ipv4 or ipv6
	 */
	private static boolean isValidIp(final String ip) {
		try{
			InetAddress ipAddress = InetAddress.getByName(ip);
			return InetAddressUtils.isIPv4Address(ipAddress.getHostAddress()) || InetAddressUtils.isIPv6Address(ipAddress.getHostAddress());
		}catch(UnknownHostException ue){
			logger.error("Not valid ip", ue);
			return false;
		}
	}

	@Override
	public PortRange getRange() {
		logger.debug("Returning range [1, 10000]");
		return new PortRange(1, 10000);
	}

	@Override
	public List<String> retrieveListContexts() {


		ASLSession session = getASLSession();

		if(session.getUsername().equals(TEST_USER)){

			logger.error("Session expired, you cannot create a token without beeing logged");
			return null;

		}

		List<String> toReturn = new ArrayList<String>();
		String currentContext = ScopeProvider.instance.get();
		logger.info("Current context is " + currentContext);

		try{

			GroupManager gm = new LiferayGroupManager();
			long currentGroupId = gm.getGroupIdFromInfrastructureScope(currentContext);
			GCubeGroup currentGroup = gm.getGroup(currentGroupId);

			// three cases
			if(gm.isVRE(currentGroupId)){

				// do nothing

			}else if(gm.isVO(currentGroupId)){

				// iterate over its vres
				List<GCubeGroup> children = currentGroup.getChildren();
				for (GCubeGroup gCubeGroup : children) {
					toReturn.add(gm.getInfrastructureScope(gCubeGroup.getGroupId()));
				}

			}else{

				// is root
				List<GCubeGroup> children = currentGroup.getChildren();
				for (GCubeGroup gCubeGroup : children) {
					toReturn.add(gm.getInfrastructureScope(gCubeGroup.getGroupId()));

					// get the vo children
					List<GCubeGroup> childrenVO = gCubeGroup.getChildren();
					for (GCubeGroup voChildren : childrenVO) {
						toReturn.add(gm.getInfrastructureScope(voChildren.getGroupId()));
					}
				}

			}

			// add the current scope too
			toReturn.add(currentContext);

			// revert
			Collections.reverse(toReturn);

		}catch(Exception e){
			logger.error("Unable to retrieve contexts", e);
		}

		return toReturn;
	}

}

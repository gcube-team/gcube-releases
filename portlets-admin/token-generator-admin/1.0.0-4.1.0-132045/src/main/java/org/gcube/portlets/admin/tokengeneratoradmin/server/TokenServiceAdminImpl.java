package org.gcube.portlets.admin.tokengeneratoradmin.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.http.conn.util.InetAddressUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.tokengeneratoradmin.client.TokenServiceAdmin;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.NodeToken;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.PortRange;
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
	public NodeToken createNodeToken(String ipaddress, short port) {
		logger.debug("Request for creating new NodeToken for port " + port + " and node " + ipaddress);

		try{

			ASLSession session = getASLSession();

			if(session.getUsername().equals(TEST_USER)){

				logger.error("Session expired, you cannot create a token without beeing logged");
				return null;

			}

			String context = session.getScope();
			String contextName = session.getGroupName();
			if(!isWithinPortal())
				return new NodeToken(UUID.randomUUID().toString(), context, ipaddress, port);
			else{

				if(isValidIp(ipaddress)){
					String token = authorizationService().requestActivation(new ContainerInfo(ipaddress, port), context);
					return new NodeToken(token, contextName, ipaddress, port);
				}else{
					logger.error("Ip Address " + ipaddress + " is not valid");
					return new NodeToken("Please check the inserted node address. It doesn't seem a valid IP/IP6 address");
				}

			}

		}catch(Exception e){
			logger.error("Unable to create NodeToken", e);
		}

		return new NodeToken("Unable to create Node Token, retry later");
	}

	//	@Override
	//	public String getCurrentUser() {
	//
	//		return getASLSession().getUsername();
	//
	//	}

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

}

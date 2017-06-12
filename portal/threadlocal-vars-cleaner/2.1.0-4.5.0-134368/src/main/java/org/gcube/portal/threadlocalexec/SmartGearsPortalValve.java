package org.gcube.portal.threadlocalexec;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;
/**
 * 
 * @author Massimiliano Assante, CNR ISTI
 * @author Lucio Lelii, CNR ISTI
 *
 */
public class SmartGearsPortalValve extends ValveBase  {
	private static final Logger _log = LoggerFactory.getLogger(SmartGearsPortalValve.class);
	private final static String DEFAULT_ROLE = "OrganizationMember";
	private final static String LIFERAY_POLLER_CONTEXT = "poller/receive";


	@Override
	public void invoke(Request req, Response resp) throws IOException,	ServletException {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		AuthorizationProvider.instance.reset();
		//_log.trace("SmartGearsPortalValve SecurityTokenProvider and AuthorizationProvider reset OK");
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request =  (HttpServletRequest) req;
			if (!req.getRequestURL().toString().endsWith(LIFERAY_POLLER_CONTEXT)) { //avoid calling gCube auth service for liferay internal poller
				PortalContext context = PortalContext.getConfiguration();
				String scope = context.getCurrentScope(request);
				String username = getCurrentUsername(request);
				if (scope != null && username != null) {
					String userToken = null;
					try {
						ScopeProvider.instance.set(scope);
						userToken = authorizationService().resolveTokenByUserAndContext(username, scope);
						SecurityTokenProvider.instance.set(userToken);
					} 
					catch (ObjectNotFound ex) {
						userToken = generateAuthorizationToken(username, scope);
						SecurityTokenProvider.instance.set(userToken);
						_log.debug("generateAuthorizationToken OK for " + username + " in scope " + scope);
					}			
					catch (Exception e) {
						_log.error("Something went wrong in generating token for " + username + " in scope " + scope);
						e.printStackTrace();
					}
					//_log.trace("Security token set OK for " + username + " in scope " + scope);
				}
			}
		}
		getNext().invoke(req, resp);
	}

	/**
	 * 
	 * @param username
	 * @param scope
	 * @throws Exception
	 */
	private static String generateAuthorizationToken(String username, String scope)  {
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		String token;
		try {
			token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return token;
	}

	/**
	 * 
	 * @param httpServletRequest the httpServletRequest object
	 * @return the instance of the user 
	 * @see GCubeUser
	 */
	public static String getCurrentUsername(HttpServletRequest httpServletRequest) {
		String userIdNo = httpServletRequest.getHeader(PortalContext.USER_ID_ATTR_NAME);
		if (userIdNo != null && userIdNo.compareTo("undefined") != 0) {
			long userId = -1;
			try {
				userId = Long.parseLong(userIdNo);
				return UserLocalServiceUtil.getUser(userId).getScreenName();
			} catch (NumberFormatException e) {
				_log.error("The userId is not a number -> " + userIdNo);
				return null;
			} catch (Exception e) {
				_log.error("The userId does not belong to any user -> " + userIdNo);
				return null;
			}
		} 
		return null;
	}
}


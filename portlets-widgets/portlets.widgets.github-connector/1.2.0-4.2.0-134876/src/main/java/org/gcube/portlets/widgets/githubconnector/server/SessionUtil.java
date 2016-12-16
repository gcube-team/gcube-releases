/**
 * 
 */
package org.gcube.portlets.widgets.githubconnector.server;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.widgets.githubconnector.server.util.ServiceCredentials;
import org.gcube.portlets.widgets.githubconnector.shared.Constants;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */

public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);

	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest) throws ServiceException {
		ServiceCredentials sCredentials = null;
		String userName = null;
		String scope = null;
		String token = null;

		if (Constants.DEBUG_MODE) {
			logger.info("No credential found in session, use test user!");
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;

			sCredentials = new ServiceCredentials(userName, scope, token);

		} else {
			logger.info("Retrieving credential in session!");
			PortalContext pContext = PortalContext.getConfiguration();
			userName = pContext.getCurrentUser(httpServletRequest)
					.getUsername();
			scope = pContext.getCurrentScope(httpServletRequest);
			token = pContext.getCurrentUserToken(httpServletRequest);
			String name = pContext.getCurrentUser(httpServletRequest)
					.getFirstName();
			String lastName = pContext.getCurrentUser(httpServletRequest)
					.getLastName();
			String fullName = pContext.getCurrentUser(httpServletRequest).getFullname();
			
			String email = pContext.getCurrentUser(httpServletRequest)
					.getEmail();
			String groupId = String.valueOf(pContext
					.getCurrentGroupId(httpServletRequest));
			String groupName = pContext.getCurrentGroupName(httpServletRequest);
			
			sCredentials = new ServiceCredentials(userName, fullName, name, lastName,
					email, scope, groupId, groupName, token);

		}

		logger.info("ServiceCredential: " + sCredentials);

		return sCredentials;
	}


}

/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.server;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.CodeFileUploadSession;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.social.Recipient;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.session.SessionConstants;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SessionUtil {

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest)
			throws StatAlgoImporterServiceException {
		return getServiceCredentials(httpServletRequest, null, null);
	}

	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest, String scopeGroupId, String currUserId)
			throws StatAlgoImporterServiceException {

		ServiceCredentials sCredentials = null;
		String userName = null;
		String scope = null;
		String token = null;
		String groupId = null;
		String groupName = null;

		if (Constants.DEBUG_MODE) {
			logger.info("No credential found in session, use test user!");
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;

			sCredentials = new ServiceCredentials(userName, scope, token);

		} else {
			logger.info("Retrieving credential in session!");
			PortalContext pContext = PortalContext.getConfiguration();
			boolean hasScopeGroupId = false;
			boolean hasCurrUserId = false;

			if (scopeGroupId != null && !scopeGroupId.isEmpty()) {
				hasScopeGroupId = true;

			} else {
				hasScopeGroupId = false;
			}

			if (currUserId != null && !currUserId.isEmpty()) {
				hasCurrUserId = true;

			} else {
				hasCurrUserId = false;
			}

			if (hasScopeGroupId) {
				scope = pContext.getCurrentScope(scopeGroupId);
			} else {
				scope = pContext.getCurrentScope(httpServletRequest);

			}
			
			logger.debug("Scope: " + scope);
			if (scope == null || scope.isEmpty()) {
				String error = "Error retrieving scope: " + scope;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			GCubeUser gCubeUser = null;

			if (hasCurrUserId) {
				try {
					gCubeUser = new LiferayUserManager().getUserById(Long
							.valueOf(currUserId));
				} catch (Exception e) {
					String error = "Error retrieving gCubeUser for: [userId= "
							+ currUserId + ", scope: " + scope + "]";
					logger.error(error, e);
					throw new StatAlgoImporterServiceException(error);
				}
			} else {
				gCubeUser = pContext.getCurrentUser(httpServletRequest);
			}
			
			if (gCubeUser == null) {
				String error = "Error retrieving gCubeUser in scope " + scope
						+ ": " + gCubeUser;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			userName = gCubeUser.getUsername();
			logger.debug("UserName: " + userName);

			if (userName == null || userName.isEmpty()) {
				String error = "Error retrieving username in scope " + scope
						+ ": " + userName;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			if (hasCurrUserId) {
				try {
					token = pContext.getCurrentUserToken(scope,
							Long.valueOf(currUserId));
				} catch (Exception e) {
					String error = "Error retrieving token for: [userId= "
							+ currUserId + ", scope: " + scope + "]";
					logger.error(error, e);
					throw new StatAlgoImporterServiceException(error);
				}

			} else {
				token = pContext.getCurrentUserToken(scope, httpServletRequest);
			}

			logger.debug("Token: " + token);
			if (token == null || token.isEmpty()) {
				String error = "Error retrieving token for " + userName
						+ " in " + scope + ": " + token;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			String name = gCubeUser.getFirstName();
			String lastName = gCubeUser.getLastName();
			String fullName = gCubeUser.getFullname();

			String userAvatarURL = gCubeUser.getUserAvatarURL();

			String email = gCubeUser.getEmail();

			if (hasScopeGroupId) {

				groupId = scopeGroupId;

				long gId;

				try {
					gId = Long.parseLong(scopeGroupId);
				} catch (Throwable e) {
					String error = "Error retrieving groupId: " + scopeGroupId;
					logger.error(error, e);
					throw new StatAlgoImporterServiceException(error);
				}

				GCubeGroup group;
				try {
					group = new LiferayGroupManager().getGroup(gId);
				} catch (Throwable e) {
					String error = "Error retrieving group: " + groupName;
					logger.error(error);
					throw new StatAlgoImporterServiceException(error);
				}

				groupName = group.getGroupName();

			} else {

				groupId = String.valueOf(pContext
						.getCurrentGroupId(httpServletRequest));

				groupName = pContext.getCurrentGroupName(httpServletRequest);

			}

			sCredentials = new ServiceCredentials(userName, fullName, name,
					lastName, email, scope, groupId, groupName, userAvatarURL,
					token);
		}

		logger.info("ServiceCredentials: " + sCredentials);

		return sCredentials;
	}

	//
	public static ArrayList<Recipient> getRecipients(
			ServletContext servletContest) {
		@SuppressWarnings("unchecked")
		ArrayList<Recipient> recipients = (ArrayList<Recipient>) servletContest
				.getAttribute(Constants.RECIPIENTS);
		return recipients;

	}

	public static ArrayList<Recipient> setRecipients(
			ServletContext servletContest, ArrayList<Recipient> recipients) {
		servletContest.setAttribute(Constants.RECIPIENTS, recipients);
		return recipients;

	}

	//
	public static FileUploadMonitor getFileUploadMonitor(HttpSession httpSession) {
		FileUploadMonitor fileUploadMonitor = (FileUploadMonitor) httpSession
				.getAttribute(SessionConstants.FILE_UPLOAD_MONITOR);
		if (fileUploadMonitor != null) {
			return fileUploadMonitor;
		} else {
			fileUploadMonitor = new FileUploadMonitor();
			httpSession.setAttribute(SessionConstants.FILE_UPLOAD_MONITOR,
					fileUploadMonitor);
			return fileUploadMonitor;
		}
	}

	public static void setFileUploadMonitor(HttpSession httpSession,
			FileUploadMonitor fileUploadMonitor) {
		FileUploadMonitor fum = (FileUploadMonitor) httpSession
				.getAttribute(SessionConstants.FILE_UPLOAD_MONITOR);
		if (fum != null) {
			httpSession.removeAttribute(SessionConstants.FILE_UPLOAD_MONITOR);
		}
		httpSession.setAttribute(SessionConstants.FILE_UPLOAD_MONITOR,
				fileUploadMonitor);

	}

	//
	public static void setCodeFileUploadSession(HttpSession httpSession,
			CodeFileUploadSession s) throws StatAlgoImporterServiceException {

		CodeFileUploadSession session = (CodeFileUploadSession) httpSession
				.getAttribute(SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION);
		if (session != null)
			httpSession
					.removeAttribute(SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION);
		httpSession.setAttribute(
				SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION, s);

	}

	public static CodeFileUploadSession getCodeFileUploadSession(
			HttpSession httpSession) {
		CodeFileUploadSession fileUploadSession = (CodeFileUploadSession) httpSession
				.getAttribute(SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION);
		if (fileUploadSession == null) {
			logger.error("CodeFileUploadSession was not acquired");
		}
		return fileUploadSession;
	}

	//
	public static void setProjectSession(HttpSession httpSession,
			Project project) throws StatAlgoImporterSessionExpiredException {
		Project p = (Project) httpSession
				.getAttribute(SessionConstants.PROJECT);
		if (p != null)
			httpSession.removeAttribute(SessionConstants.PROJECT);
		httpSession.setAttribute(SessionConstants.PROJECT, project);

	}

	public static Project getProjectSession(HttpSession httpSession) {
		Project project = (Project) httpSession
				.getAttribute(SessionConstants.PROJECT);
		if (project == null) {
			logger.error("Project was not acquired");
		}
		return project;
	}

}

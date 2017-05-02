/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.server;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.CodeFileUploadSession;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.social.Recipient;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.session.SessionConstants;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
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

	/**
	 * 
	 * @param httpServletRequest
	 * @return
	 * @throws TDGWTServiceException
	 */
	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest)
			throws StatAlgoImporterServiceException {
		return getServiceCredentials(httpServletRequest, null);
	}

	/**
	 * 
	 * @param httpServletRequest
	 * @param scopeGroupId
	 * @return
	 * @throws TDGWTServiceException
	 */
	public static ServiceCredentials getServiceCredentials(
			HttpServletRequest httpServletRequest, String scopeGroupId)
			throws StatAlgoImporterServiceException {

		ServiceCredentials sCredentials = null;
		String userName = null;
		String scope = null;
		String token = null;
		String groupId = null;
		String groupName = null;

		if (Constants.DEBUG_MODE) {
			logger.info("No credential found in session, use test user!");
			/*
			 * InfoLocale infoLocale = getInfoLocale(httpServletRequest, null);
			 * Locale locale = new Locale(infoLocale.getLanguage());
			 * 
			 * ResourceBundle messages = ResourceBundle.getBundle(
			 * StatAlgoImporterServiceMessagesConstants.TDGWTServiceMessages,
			 * locale);
			 */
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;

			logger.info("Set SecurityToken: " + token);
			SecurityTokenProvider.instance.set(token);
			logger.info("Set ScopeProvider: " + scope);
			ScopeProvider.instance.set(scope);

			sCredentials = new ServiceCredentials(userName, scope, token);

		} else {
			logger.info("Retrieving credential in session!");
			PortalContext pContext = PortalContext.getConfiguration();
			boolean hasScopeGroupId = false;

			if (scopeGroupId != null && !scopeGroupId.isEmpty()) {
				hasScopeGroupId = true;

			} else {
				hasScopeGroupId = false;
			}

			if (hasScopeGroupId) {
				scope = pContext.getCurrentScope(scopeGroupId);
			} else {
				scope = pContext.getCurrentScope(httpServletRequest);
			}

			if (scope == null || scope.isEmpty()) {
				String error = "Error retrieving scope: " + scope;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			GCubeUser gCubeUser = pContext.getCurrentUser(httpServletRequest);

			if (gCubeUser == null) {
				String error = "Error retrieving gCubeUser in scope " + scope
						+ ": " + gCubeUser;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			userName = gCubeUser.getUsername();

			if (userName == null || userName.isEmpty()) {
				String error = "Error retrieving username in scope " + scope
						+ ": " + userName;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			token = pContext.getCurrentUserToken(scope, userName);

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
				logger.info("Set SecurityToken: " + token);
				SecurityTokenProvider.instance.set(token);
				logger.info("Set ScopeProvider: " + scope);
				ScopeProvider.instance.set(scope);

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
	public static FileUploadMonitor getFileUploadMonitor(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws Exception {
		SessionOp<FileUploadMonitor> sessionOp = new SessionOp<>();
		FileUploadMonitor fileUploadMonitor = sessionOp.get(httpRequest,
				serviceCredentials,
				SessionConstants.FILE_UPLOAD_MONITOR,FileUploadMonitor.class);
		return fileUploadMonitor;
	}

	public static void setFileUploadMonitor(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials,
			FileUploadMonitor fileUploadMonitor) {
		SessionOp<FileUploadMonitor> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials,
				SessionConstants.FILE_UPLOAD_MONITOR,
				fileUploadMonitor);
	}

	//
	public static CodeFileUploadSession getCodeFileUploadSession(
			HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<CodeFileUploadSession> sessionOp = new SessionOp<>();
		CodeFileUploadSession fileUploadSession = sessionOp.get(httpRequest,
				serviceCredentials,
				SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION);
		return fileUploadSession;
	}
	
	public static void setCodeFileUploadSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials,
			CodeFileUploadSession codeFileUploadSession) {
		SessionOp<CodeFileUploadSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials,
				SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION,
				codeFileUploadSession);
	}

	

	//
	public static Project getProjectSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<Project> sessionOp = new SessionOp<>();
		Project project = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.PROJECT);
		return project;
	}
	
	public static void setProjectSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, Project project) {
		SessionOp<Project> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials,
				SessionConstants.PROJECT, project);

	}

	

}

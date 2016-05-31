/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.server;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.CodeFileUploadSession;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.social.Recipient;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.session.SessionConstants;
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

	public static ASLSession getAslSession(HttpSession httpSession)
			throws StatAlgoImporterSessionExpiredException {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession session;
		if (username == null) {
			logger.warn("no user found in session, use test user");
			/*throw new AccountingManagerSessionExpiredException("Session Expired!");*/
			
			
			// Remove comment for Test
			username = Constants.DEFAULT_USER;
			String scope = Constants.DEFAULT_SCOPE;

			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, username);
			session = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);
			session.setScope(scope);
		
		} else {
			session = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);

		}

		logger.info("SessionUtil: aslSession " + session.getUsername() + " "
				+ session.getScope());

		return session;

	}
	
	
	//
	public static ArrayList<Recipient> getRecipients(ServletContext servletContest) {
		@SuppressWarnings("unchecked")
		ArrayList<Recipient> recipients=(ArrayList<Recipient>)servletContest.getAttribute(Constants.RECIPIENTS);
		return recipients;
		
	}
	
	public static ArrayList<Recipient> setRecipients(ServletContext servletContest, ArrayList<Recipient> recipients) {
		servletContest.setAttribute(Constants.RECIPIENTS,recipients);
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
			CodeFileUploadSession s) throws StatAlgoImporterSessionExpiredException {

		CodeFileUploadSession session = (CodeFileUploadSession) httpSession
				.getAttribute(SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION);
		if (session != null)
			httpSession
					.removeAttribute(SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION);
		httpSession.setAttribute(
				SessionConstants.IMPORT_CODE_FILE_UPLOAD_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
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
			httpSession
					.removeAttribute(SessionConstants.PROJECT);
		httpSession.setAttribute(
				SessionConstants.PROJECT, project);
		
	}

	public static Project getProjectSession(
			HttpSession httpSession) {
		Project project = (Project) httpSession
				.getAttribute(SessionConstants.PROJECT);
		if (project == null) {
			logger.error("Project was not acquired");
		}
		return project;
	}

	
	
	
}

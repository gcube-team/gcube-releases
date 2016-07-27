package org.gcube.portlets.user.csvimportwizard.ws.server;

import java.io.InputStream;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationState;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportServiceException;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSessionManager;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportStatus;
import org.gcube.portlets.user.csvimportwizard.server.local.Util;
import org.gcube.portlets.user.csvimportwizard.ws.client.rpc.ImportWizardWSService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ImportWizardWSServiceImpl extends RemoteServiceServlet implements ImportWizardWSService {

	protected Logger logger = Logger.getLogger(ImportWizardWSServiceImpl.class);

	protected ASLSession getCurrentSession() throws CSVImportServiceException
	{
		try {
			HttpSession httpSession = this.getThreadLocalRequest().getSession();

			String username = (String) httpSession
					.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
			ASLSession session;
			if (username == null) {
				logger.warn("no user found in session, using test one");
				username = "federico.defaveri";
				String scope = "/gcube/devsec/devVRE";

				httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, username);
				session = SessionManager.getInstance().getASLSession(
						httpSession.getId(), username);
				session.setScope(scope);

			} else {
				session= SessionManager.getInstance().getASLSession(
						httpSession.getId(), username);
				
			}
			
			logger.info("SessionUtil: aslSession "+session.getUsername()+" "+session.getScope());
			return session;
			
		}catch (Exception e) {
			e.printStackTrace();
			throw new CSVImportServiceException("User session expired");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void startWorkspaceUpload(String sessionId, final String workspaceItemId) throws CSVImportServiceException {
		logger.trace("startWorkspaceUpload sessionId: "+sessionId+" workspaceItemId: "+workspaceItemId);

		final CSVImportSession importSession = CSVImportSessionManager.getInstance().getSession(sessionId);
		final ASLSession session = getCurrentSession();

		Thread importer = new Thread(new Runnable() {

			public void run() {
				getFileFromWorkspace(session, importSession, workspaceItemId);
			}
		});
		
		importer.start();

	}

	/**
	 * {@inheritDoc}
	 */
	public OperationProgress getWorkspaceUploadStatus(String sessionId) throws CSVImportServiceException {
		CSVImportSession session = CSVImportSessionManager.getInstance().getSession(sessionId);
		OperationProgress progress = session.getUploadProgress();
		return progress;
	}

	protected void getFileFromWorkspace(ASLSession session, CSVImportSession importSession, String workspaceItemId)
	{
		try {
			Workspace userWorkspace = HomeLibrary.getUserWorkspace(session.getUsername());
			WorkspaceItem workspaceItem = userWorkspace.getItem(workspaceItemId);

			ExternalFile csvFile = (ExternalFile)workspaceItem;
			InputStream is = csvFile.getData();

			Util.setImportFile(importSession, is, workspaceItem.getName(), csvFile.getMimeType());

			logger.trace("changing state");
			importSession.getUploadProgress().setState(OperationState.COMPLETED);
		} catch (Exception e) {
			importSession.getUploadProgress().setFailed("An error occured elaborating the file", Util.exceptionDetailMessage(e));
			importSession.setStatus(CSVImportStatus.FAILED);
			logger.error("Error elaborating the stream", e);
			return;
		}
	}


}

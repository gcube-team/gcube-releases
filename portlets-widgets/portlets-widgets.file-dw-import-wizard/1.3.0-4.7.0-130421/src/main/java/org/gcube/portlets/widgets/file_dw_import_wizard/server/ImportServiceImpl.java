package org.gcube.portlets.widgets.file_dw_import_wizard.server;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.data.AvailableCharsetList;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationState;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportServiceException;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.FileUtil;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessionManager;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessions;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportStatus;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.StatisticalFileTarget;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.Target;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.TargetRegistry;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.local.Util;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//import org.apache.log4j.ConsoleAppender;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//
//import org.apache.log4j.SimpleLayout;

public class ImportServiceImpl extends RemoteServiceServlet implements
		ImportService {

	private static final long serialVersionUID = 1733737412247481074L;

	protected Logger logger = Logger.getLogger("");

	//
	// static {
	// ConsoleAppender ca = new ConsoleAppender(new SimpleLayout());
	// ca.setThreshold(Level.ALL);
	// ca.activateOptions();
	//
	// }

	/**
	 * {@inheritDoc}
	 * 
	 * @throws CSVImportServiceException
	 */
	@Override
	public String createSessionId(String targetId, String type)
			throws ImportServiceException {
		// logger.debug("createSessionId targetId: " + targetId);
		logger.log(Level.SEVERE, "createSessionId targetId: " + targetId);

		FileType typ = FileType.GENERAL;
		java.util.logging.Logger log = java.util.logging.Logger
				.getLogger("logger");
		log.log(java.util.logging.Level.SEVERE, "******type is: " + type);
		if (type.equals(" GENERAL")) {
			log.log(java.util.logging.Level.SEVERE,
					"is not general, is darwincore");
			typ = FileType.DARWINCORE;
		}

		try {
			return ImportSessionManager.getInstance()
					.createImportSession(targetId, typ).getId();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An error occurred creating the session",
					e);
			// logger.debug("An error occurred creating the session", e);

			throw new ImportServiceException(
					"An error occurred creating the session" + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OperationProgress getLocalUploadStatus(String sessionId) {
		logger.log(Level.SEVERE, "getLocalUploadStatus sessionId: " + sessionId);
		ImportSessions session = ImportSessionManager.getInstance().getSession(
				sessionId);
		return session.getUploadProgress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AvailableCharsetList getAvailableCharset(String sessionId) {
		String defaultEncoding = Charset.defaultCharset().displayName();
		ArrayList<String> charsetList = new ArrayList<String>(Charset
				.availableCharsets().keySet());
		return new AvailableCharsetList(charsetList, defaultEncoding);
	}

	@Override
	public OperationProgress getImportStatus(String sessionId) {
		logger.log(Level.SEVERE, "getImportStatus sessionId: " + sessionId);
		ImportSessions session = ImportSessionManager.getInstance().getSession(
				sessionId);
		OperationProgress progress = session.getImportProgress();
		logger.log(Level.SEVERE, "progress: " + progress);
		return progress;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ImportServiceException
	 */
@Override
	public void startImport(String sessionId, boolean[] columnToImportMask)
			throws ImportServiceException {

		logger.log(Level.SEVERE, "startImport sessionId: " + sessionId);
		final ImportSessions session = ImportSessionManager.getInstance()
				.getSession(sessionId);
		Target Target = session.getTarget();
		HttpSession httpSession = getThreadLocalRequest().getSession();
		File taxa = null;
		File vernacular = null;
		

		Thread th1 = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.log(Level.SEVERE, "Inside thread");
				session.getImportProgress().setTotalLenght(100);
				session.getImportProgress().setState(OperationState.INPROGRESS);
				Random r = new Random();
				for (int i = 0; i < 97  ; i=i+2) {
					try {
						logger.log(Level.SEVERE, "sleep");

						Thread.sleep(3500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						i=97;
						session.getImportProgress().setState(OperationState.COMPLETED);
						break;
						
					}

					session.getImportProgress().setElaboratedLenght(i);
				}
			}
		});
	
		th1.start();
		logger.log(Level.SEVERE, "after thread");

		try {
			if (session.getType() == FileType.DARWINCORE) {
				logger.log(Level.SEVERE, "Type of file is Darwincore");

				ArrayList<String> generatedFile = checkDWCA(session);
				
				th1.interrupt();
				logger.log(Level.SEVERE, "created  vernacular and taxafiles");
				if (generatedFile.size() == 2) {
					taxa = new File(generatedFile.get(0));
					vernacular = new File(generatedFile.get(1));
					session.setGeneratedTaxaFile(taxa);
					session.setGeneratedVernacular(vernacular);
				} else
					throw new ImportServiceException("Malformed darwin core");
			}
			else
			{
				th1.interrupt();
					
			}
		} catch (Exception e) {
			th1.interrupt();
			session.getImportProgress().setState(OperationState.COMPLETED);
			logger.log(Level.SEVERE,
					"ERROR...GENERATED FILE FROM DARWIN CORE HAD A PROBLEMS");
			throw new ImportServiceException(
					" Error in file processing: error in transforming "
							+ "the DwCA into Authority Files .");

		}
		th1.interrupt();
		Target.importFile(httpSession, session.getFile(), session.getName(),
				session.getType(), taxa, vernacular, columnToImportMask,
				session.getImportProgress());
	}


@Override
	public void init() {
		TargetRegistry.getInstance().add(new StatisticalFileTarget());
	}

	@Override
	public void updateFileType(String id, FileType type) {

		ImportSessionManager.getInstance().getSession(id).setType(type);
	}

	private String getUser()

	{
		HttpSession httpSession = getThreadLocalRequest().getSession();
		String user = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		logger.log(Level.SEVERE, "user is " + user);
		return user;
	}

	// @Override
	public ArrayList<String> checkDWCA(ImportSessions session) throws Exception {

		return FileUtil.checkDWCA(session.getFile(), getUser());

	}

	@Override
	public OperationProgress getWorkspaceUploadStatus(String sessionId) {
		ImportSessions session = ImportSessionManager.getInstance().getSession(sessionId);
		OperationProgress progress = session.getUploadProgress();
		return progress;
	}

	protected ASLSession getCurrentSession() throws Exception
	{
		try {
			HttpSession httpSession = this.getThreadLocalRequest().getSession();

			String username = (String) httpSession
					.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
			ASLSession session;
			if (username == null) {
				
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
			throw new Exception("User session expired");
		}
	}
	protected void getFileFromWorkspace(ASLSession session, ImportSessions importSession, String workspaceItemId)
	{
		try {
			Workspace userWorkspace = HomeLibrary.getUserWorkspace(session.getUsername());
			WorkspaceItem workspaceItem = userWorkspace.getItem(workspaceItemId);

			ExternalFile file = (ExternalFile)workspaceItem;
			InputStream is = file.getData();

			Util.setImportFile(importSession, is, workspaceItem.getName(), file.getMimeType());

//			Util.(importSession, is, workspaceItem.getName(), csvFile.getMimeType());

			importSession.getUploadProgress().setState(OperationState.COMPLETED);
		} catch (Exception e) {
			importSession.getUploadProgress().setFailed("An error occured elaborating the file", Util.exceptionDetailMessage(e));
			importSession.setStatus(ImportStatus.FAILED);
			return;
		}
	}
	

	
	@Override
	public void startWorkspaceUpload(String sessionId, final String workspaceItemId) throws Exception {

		final ImportSessions importSession = ImportSessionManager.getInstance().getSession(sessionId);
		final ASLSession session = getCurrentSession();

		Thread importer = new Thread(new Runnable() {

			public void run() {
				getFileFromWorkspace(session, importSession, workspaceItemId);
			}
		});
		
		importer.start();
		
	}

}

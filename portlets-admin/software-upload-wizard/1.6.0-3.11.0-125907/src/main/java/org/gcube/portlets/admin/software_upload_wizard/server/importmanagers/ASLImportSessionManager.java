package org.gcube.portlets.admin.software_upload_wizard.server.importmanagers;


import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.data.ImportSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ASLImportSessionManager implements ImportSessionManager {
	
	private static final String IMPORT_SESSION_CODE = "ImportSession";
	
	private static final Logger log = LoggerFactory.getLogger(ASLImportSessionManager.class);
	
	private ASLSessionManager manager;
	
	@Inject
	public ASLImportSessionManager(ASLSessionManager manager) {
		this.manager=manager;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.softwaremanagementwidget.server.managers.ImportSessionManager#getImportSession()
	 */
	@Override
	public ImportSession getImportSession(){
		ImportSession importSession = (ImportSession) manager.getASLSession().getAttribute(IMPORT_SESSION_CODE);
		log.trace("Retrieved import session with id: " + importSession.getSessionId().getId());
		return importSession;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.softwaremanagementwidget.server.managers.ImportSessionManager#setImportSession(org.gcube.portlets.user.softwaremanagementwidget.server.data.ImportSession)
	 */
	@Override
	public void setImportSession(ImportSession importSession){
		manager.getASLSession().setAttribute(IMPORT_SESSION_CODE, importSession);
		log.debug("Saved import session with id: " + importSession.getSessionId().getId());
	}
}
package org.gcube.portlets.admin.software_upload_wizard.server.importmanagers;

import org.gcube.portlets.admin.software_upload_wizard.server.data.ImportSession;

public interface ImportSessionManager {

	/**
	 * Recover the ImportSession object from the ASLSession.
	 * If a session was not created previously, return null.
	 * @param sessionId 
	 * @return
	 */
	public abstract ImportSession getImportSession();

	public abstract void setImportSession(ImportSession importSession);

}
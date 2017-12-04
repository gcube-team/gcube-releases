package org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc;



import java.io.InputStream;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.data.AvailableCharsetList;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessions;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ImportService")
public interface ImportService extends RemoteService {
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Utility {
		
		private static ImportServiceAsync instance;
		
		public static ImportServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(ImportService.class);
			}
			return instance;
		}

		
	}
	
	/**
	 * Create a new session import id.
	 * @param targetId the CSV target to use.
	 * @return the session id.
	 */
	public String createSessionId(String targetId,String type) throws ImportServiceException;
	
	/**
	 * Returns the current file upload state, used by local upload.
	 * @param sessionId the CSV session id.
	 * @return the upload state.
	 */
	public OperationProgress getLocalUploadStatus(String sessionId) throws ImportServiceException;
	
	
	/**
	 * @param sessionId
	 * @return
	 */
	public AvailableCharsetList getAvailableCharset(String sessionId) throws ImportServiceException;
	
	
//	/**
//	 * Configures the CSV parser and return the resulting CSV header.
//	 * @param sessionId the import session id.
//	 * @param encoding the file encoding.
//	 * @param headerPresence header presence.
//	 * @param delimiter the char used has delimiter.
//	 * @param comment the char used has comment.
//	 * @return a list of CSV file fields.
//	 * @throws CSVImportServiceException 
//	 */
//	public ArrayList<String> configureCSVParser(String sessionId, String encoding, HeaderPresence headerPresence, char delimiter, char comment) throws CSVImportServiceException;
//	
//	
//	/**
//	 * Checks the entire CSV.
//	 * @param sessionId the import session id.
//	 * @param errorsLimit the maximum number of errors to check.
//	 * @return the error list.
//	 * @throws CSVImportServiceException 
//	 */
//	public ArrayList<CSVRowError> checkCSV(String sessionId, long errorsLimit) throws CSVImportServiceException;
	
	public void startImport(String sessionId, boolean[] columnToImportMask) throws ImportServiceException;
	
	/**
	 * Returns the current file import state.
	 * @param sessionId the CSV session id.
	 * @return the import state.
	 */
	public OperationProgress getImportStatus(String sessionId) throws ImportServiceException;
	public void init();
	
	public void updateFileType(String id, FileType type);

	OperationProgress getWorkspaceUploadStatus(String id);

	void startWorkspaceUpload(String id, String workspaceItemId) throws Exception;
}

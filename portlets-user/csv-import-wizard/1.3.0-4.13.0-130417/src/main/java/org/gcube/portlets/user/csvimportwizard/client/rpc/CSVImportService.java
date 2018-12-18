package org.gcube.portlets.user.csvimportwizard.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.data.AvailableCharsetList;
import org.gcube.portlets.user.csvimportwizard.client.data.CSVRowError;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("CSVImportService")
public interface CSVImportService extends RemoteService {
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		
		private static CSVImportServiceAsync instance;
		
		public static CSVImportServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(CSVImportService.class);
			}
			return instance;
		}
	}
	
	/**
	 * Create a new session import id.
	 * @param targetId the CSV target to use.
	 * @return the session id.
	 */
	public String createCSVSessionId(String targetId) throws CSVImportServiceException;
	
	/**
	 * Returns the current file upload state, used by local upload.
	 * @param sessionId the CSV session id.
	 * @return the upload state.
	 */
	public OperationProgress getLocalUploadStatus(String sessionId) throws CSVImportServiceException;
	
	
	/**
	 * @param sessionId
	 * @return
	 */
	public AvailableCharsetList getAvailableCharset(String sessionId) throws CSVImportServiceException;
	
	
	/**
	 * Configures the CSV parser and return the resulting CSV header.
	 * @param sessionId the import session id.
	 * @param encoding the file encoding.
	 * @param hasHeader <code>true</code> if the CSV have an header.
	 * @param delimiter the char used has delimiter.
	 * @param comment the char used has comment.
	 * @return a list of CSV file fields.
	 * @throws CSVImportServiceException 
	 */
	public ArrayList<String> configureCSVParser(String sessionId, String encoding, boolean hasHeader, char delimiter, char comment) throws CSVImportServiceException;
	
	
	/**
	 * Checks the entire CSV.
	 * @param sessionId the import session id.
	 * @param errorsLimit the maximum number of errors to check.
	 * @return the error list.
	 * @throws CSVImportServiceException 
	 */
	public ArrayList<CSVRowError> checkCSV(String sessionId, long errorsLimit) throws CSVImportServiceException;
	
	public void startImport(String sessionId, boolean[] columnToImportMask) throws CSVImportServiceException;
	
	/**
	 * Returns the current file import state.
	 * @param sessionId the CSV session id.
	 * @return the import state.
	 */
	public OperationProgress getImportStatus(String sessionId) throws CSVImportServiceException;
}

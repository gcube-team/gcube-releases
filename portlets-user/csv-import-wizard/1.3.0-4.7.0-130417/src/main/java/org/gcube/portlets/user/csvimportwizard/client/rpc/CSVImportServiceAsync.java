package org.gcube.portlets.user.csvimportwizard.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.data.AvailableCharsetList;
import org.gcube.portlets.user.csvimportwizard.client.data.CSVRowError;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CSVImportServiceAsync {
	
	/**
	 * Create a new session import id.
	 * @param targetId the CSV target to use.
	 * @param callback the callback to return the session id.
	 */
	public void createCSVSessionId(String targetId, AsyncCallback<String> callback);
	
	/**
	 * Returns the current file upload state, used by local upload.
	 * @param sessionId the CSV session id.
	 * @param callback the callback to return the upload state.
	 */
	public void getLocalUploadStatus(String sessionId, AsyncCallback<OperationProgress> callback);
	
	
	/**
	 * @param sessionId
	 */
	public void getAvailableCharset(String sessionId, AsyncCallback<AvailableCharsetList> callback);
	
	
	/**
	 * Configures the CSV parser and return the resulting CSV header.
	 * @param sessionId the import session id.
	 * @param encoding the file encoding.
	 * @param hasHeader <code>true</code> if the CSV have an header.
	 * @param delimiter the char used has delimiter.
	 * @param comment the char used has comment.
	 * @param callback the callback to return a list of CSV file fields.
	 * @throws CSVImportServiceException 
	 */
	public void configureCSVParser(String sessionId, String encoding, boolean hasHeader, char delimiter, char comment, AsyncCallback<ArrayList<String>> callback);
	
	
	/**
	 * Checks the entire CSV.
	 * @param sessionId the import session id.
	 * @param errorsLimit the maximum number of errors to check.
	 * @param callback the callback to return the error list.
	 * @throws CSVImportServiceException 
	 */
	public void checkCSV(String sessionId, long errorsLimit, AsyncCallback<ArrayList<CSVRowError>> callback);
	
	public void startImport(String sessionId, boolean[] columnToImportMask, AsyncCallback<Void> callback);
	
	/**
	 * Returns the current file import state.
	 * @param sessionId the CSV session id.
	 * @param callback the callback to return the import state.
	 */
	public void getImportStatus(String sessionId, AsyncCallback<OperationProgress> callback);
}

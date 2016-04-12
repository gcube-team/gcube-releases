package org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc;


import org.gcube.portlets.widgets.file_dw_import_wizard.client.data.AvailableCharsetList;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ImportServiceAsync {
	
	void createSessionId(String targetId,String type, AsyncCallback<String> callback);
	
	
	/**
	 * Returns the current file upload state, used by local upload.
	 * @param sessionId the CSV session id.
	 * @param callback the callback to return the upload state.
	 */
	public void getLocalUploadStatus(String sessionId, AsyncCallback<OperationProgress> callback);
	
	
	void getAvailableCharset(
			String sessionId,
			AsyncCallback<AvailableCharsetList> callback);
//	
//	/**
//	 * Configures the CSV parser and return the resulting CSV header.
//	 * @param sessionId the import session id.
//	 * @param encoding the file encoding.
//	 * @param headerPresence header presence.
//	 * @param delimiter the char used has delimiter.
//	 * @param comment the char used has comment.
//	 * @param callback the callback to return a list of CSV file fields.
//	 * @throws CSVImportServiceException 
//	 */
//	public void configureCSVParser(String sessionId, String encoding, HeaderPresence headerPresence, char delimiter, char comment, AsyncCallback<ArrayList<String>> callback);
//	
//	
//	/**
//	 * Checks the entire CSV.
//	 * @param sessionId the import session id.
//	 * @param errorsLimit the maximum number of errors to check.
//	 * @param callback the callback to return the error list.
//	 * @throws CSVImportServiceException 
//	 */
//	public void checkCSV(String sessionId, long errorsLimit, AsyncCallback<ArrayList<CSVRowError>> callback);
//	
	public void startImport(String sessionId, boolean[] columnToImportMask, AsyncCallback<Void> callback);
	
	/**
	 * Returns the current file import state.
	 * @param sessionId the CSV session id.
	 * @param callback the callback to return the import state.
	 */
	public void getImportStatus(String sessionId, AsyncCallback<OperationProgress> callback);


	void init(AsyncCallback<Void> callback);


	void updateFileType(String id, FileType type, AsyncCallback<Void> callback);


	void getWorkspaceUploadStatus(String id,
			AsyncCallback<OperationProgress> callback);


	void startWorkspaceUpload(String id, String workspaceItemId,
			AsyncCallback<Void> asyncCallback);
}

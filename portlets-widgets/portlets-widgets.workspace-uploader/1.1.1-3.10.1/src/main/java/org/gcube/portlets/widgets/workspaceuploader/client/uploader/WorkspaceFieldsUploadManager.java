/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploadFile;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

/**
 * The Class WorkspaceFieldsUpload.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 20, 2015
 */
public class WorkspaceFieldsUploadManager {
	
	//FORM FIELD TO UPLOAD INTO WORKSPACE
	private String jsonKeys;
	public static final String FILE_DELEMITER = ";";
	private List<WorkspaceUploaderItem> fakeUploaders = new ArrayList<WorkspaceUploaderItem>();
	
	
	public WorkspaceFieldsUploadManager() {
	}

	/**
	 * Checks if is limit exceeded.
	 *
	 * @param numbOfFiles the numb of files
	 * @return true, if is limit exceeded
	 */
	public boolean isLimitExceeded(int numbOfFiles){
		
		if (numbOfFiles > ConstantsWorkspaceUploader.LIMIT_UPLOADS) {
			Window.alert("Multiple upload limit is "
					+ ConstantsWorkspaceUploader.LIMIT_UPLOADS
					+ " files");
			return true;
		}
		
		return false;
	} 
	
	/**
	 * Adds the new submit to monitor.
	 */
	public void addNewSubmitToMonitor(){
		GWT.log("addNewSubmitToMonitor...");
		for (final WorkspaceUploaderItem workspaceUploaderItem : fakeUploaders) {
			UploaderMonitor.getInstance().addNewSubmit(workspaceUploaderItem, workspaceUploaderItem.getFile().getFileName());
			UploaderMonitor.getInstance().pollWorkspaceUploader(workspaceUploaderItem);
		}
	}

	
	/**
	 * Creates the json key for files.
	 *
	 * @return the string
	 */
	public void createJsonKeyForFiles() {

		try {
			JSONObject productObj = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			productObj.put(ConstantsWorkspaceUploader.JSON_CLIENT_KEYS, jsonArray);
//			GWT.log("Creating json keys on fakeUploaders: "+fakeUploaders.toString());
			
			for (int i = 0; i < fakeUploaders.size(); i++) {
				WorkspaceUploaderItem file = fakeUploaders.get(i);
				JSONObject obj = new JSONObject();
				obj.put(file.getClientUploadKey(), new JSONString(file.getFile().getFileName()));
				jsonArray.set(i, obj);
			}
			
			jsonKeys = productObj.toString();
			GWT.log("updated jsonKeys: "+jsonKeys);
		} catch (Exception e) {
			GWT.log("error " + e.getMessage());
			jsonKeys = null;
		}
	}
	
	/**
	 * Generate new upload client keys.
	 *
	 * @param filesSelected the files selected
	 * @param parentId            the parent id
	 * @return the list
	 */
	public void generateFakeUploaders(String filesSelected, String parentId) {
		
		if(filesSelected==null || filesSelected.isEmpty())
			return;
		
		String[] files = filesSelected.split(FILE_DELEMITER);
		
		// NORMALIZE FILE NAMES
		for (int i = 0; i < files.length; i++) {
			String normalizedFileName = files[i];
			if (normalizedFileName.contains("\\")) {
				files[i] = normalizedFileName.substring(normalizedFileName.lastIndexOf("\\") + 1); // remove
																				// C:\fakepath\
																				// if
																				// exists
			}
		}
		
		GWT.log("generating fake uploaders on: "+files.toString());
		fakeUploaders = new ArrayList<WorkspaceUploaderItem>(files.length);
		for (int i = 0; i < files.length; i++) {
			WorkspaceUploaderItem fakeItem = new WorkspaceUploaderItem();
			fakeItem.setClientUploadKey(GenerateUUID.get());
			fakeItem.setUploadStatus(UPLOAD_STATUS.WAIT);
			WorkspaceUploadFile fakeFile = new WorkspaceUploadFile();
			fakeFile.setFileName(files[i]);
			fakeFile.setParentId(parentId);
			fakeItem.setFile(fakeFile);
			fakeUploaders.add(fakeItem);
		}
		
		GWT.log("fakeUploaders generated: "+fakeUploaders.toString());
	}
	

	/**
	 * @return the jsonKeys
	 */
	public String getJsonKeys() {
		return jsonKeys;
	}

}

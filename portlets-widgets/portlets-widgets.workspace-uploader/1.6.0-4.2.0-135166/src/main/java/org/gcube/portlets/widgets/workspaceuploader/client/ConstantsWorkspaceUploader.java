package org.gcube.portlets.widgets.workspaceuploader.client;

import com.google.gwt.core.client.GWT;


/**
 * The Class ConstantsWorkspaceUploader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public class ConstantsWorkspaceUploader {

	public static final String WORKSPACE_UPLOADER_SERVLET_MODIFIED = GWT.getModuleBaseURL() + "workspaceUploadServlet";
	
	public static final String WORKSPACE_UPLOADER_SERVLET_STREAM__MODIFIED = GWT.getModuleBaseURL() + "workspaceUploadServletStream";
	
	public static final String WORKSPACE_UPLOADER_SERVICE_MODIFIED = GWT.getModuleBaseURL() + "workspaceUploaderService";
	
	public static final String WORKSPACE_UPLOADER_WS_UTIL_MODIFIED = GWT.getModuleBaseURL() + "workspaceUploaderWsUtil";

	public static final String SERVER_ERROR = "Sorry, an error has occurred on the server when";
	public static final String TRY_AGAIN = "Try again";

	// UPLOAD SERVLET PARAMETERS
	public static final String CURR_GROUP_ID = "currGroupId";
	public static final String IS_OVERWRITE = "isOverwrite";
	public static final String UPLOAD_TYPE = "uploadType";
	public static final String ID_FOLDER = "idFolder";
	public static final String UPLOAD_FORM_ELEMENT = "uploadFormElement";

	public static final String CLIENT_UPLOAD_KEYS = "client_upload_keys";
	public static final String CANCEL_UPLOAD = "cancel_upload";
	public static final String JSON_CLIENT_KEYS = "ClientKeys";

	public static final String MY_UPLOADS = "My Uploads";
	
	public static final int LIMIT_UPLOADS = 50;
	
	
	public static final String FOLDER_PARENT_ID = "FOLDER_PARENT_ID";
	public static final String ITEM_NAME = "ITEM_NAME";
}

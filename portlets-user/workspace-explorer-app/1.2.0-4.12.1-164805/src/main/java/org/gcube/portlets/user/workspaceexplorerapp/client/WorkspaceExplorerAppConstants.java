package org.gcube.portlets.user.workspaceexplorerapp.client;

import org.gcube.portlets.user.workspaceexplorerapp.client.rpc.WorkspaceExplorerAppService;
import org.gcube.portlets.user.workspaceexplorerapp.client.rpc.WorkspaceExplorerAppServiceAsync;

import com.google.gwt.core.client.GWT;



/**
 * The Class WorkspaceExplorerAppConstants.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 23, 2016
 */
public class WorkspaceExplorerAppConstants {
	public static final String SPECIAL_FOLDERS_NAME = "MySpecialFolders";

	public static final String VRE_FOLDERS_LABEL = "My VRE Folders";

	public static final String WORKSPACE_EXPLORER_CAPTION = "Workspace Explorer";

	public static final String WORKSPACE_EXPLORER_SAVE_AS_CAPTION = "Workspace Explorer Save As...";

	public static final String HOME_LABEL = "Home";

	public static final String WORKSPACE_MY_SPECIAL_FOLDERS_PATH = "/Workspace/MySpecialFolders";

	// DIALOGS
	public static final String SAVE = "Save";
	public static final String AUTO = "auto";
	public static final String SELECT = "Select";
	public static final WorkspaceExplorerAppServiceAsync workspaceNavigatorService = GWT.create(WorkspaceExplorerAppService.class);

	public static final String APPLICATION_DIV = "workspaceEplorerApplicationDiv";
	public static final String VALIDATEITEM = "validateitem";
	public static final String IDS = "ids";
	public static final String IDS_SEPARATOR = ";";
	public static final String DOWNLOAD_WORKSPACE_SERVICE = GWT.getModuleBaseURL() + "DownloadServlet";
	public static final String REDIRECTONERROR = "redirectonerror";
}

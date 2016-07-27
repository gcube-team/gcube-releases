package org.gcube.portlets.widgets.wsexplorer.client;

import org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService;
import org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerServiceAsync;

import com.google.gwt.core.client.GWT;

/**
 * The Class WorkspaceExplorerConstants.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 9, 2015
 */
public class WorkspaceExplorerConstants {
	public static final String SPECIAL_FOLDERS_NAME = "MySpecialFolders";

	public static final String VRE_FOLDERS_LABEL = "My VRE Folders";

	public static final String WORKSPACE_EXPLORER_CAPTION = "Workspace Explorer";

	public static final String WORKSPACE_EXPLORER_SAVE_AS_CAPTION = "Workspace Explorer Save As...";

	public static final String HOME_LABEL = "Home";

	public static final String WORKSPACE_MY_SPECIAL_FOLDERS_PATH = "/Workspace/MySpecialFolders";

	// DIALOGS
	public static final String SAVE = "Save";
	public static final String MAX_HEIGHT_DIALOG = "500px";
	public static final String HEIGHT_EXPLORER_PANEL = "400px";
	public static final String AUTO = "auto";
	public static final int WIDHT_DIALOG = 730;

	public static final int STATIC_BOOTSTRAP_ZINDEX_MODAL_VALUE = 1040;

	public static final String SELECT = "Select";

	public static final WorkspaceExplorerServiceAsync workspaceNavigatorService = GWT.create(WorkspaceExplorerService.class);
}

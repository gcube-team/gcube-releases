package org.gcube.portlets.user.workspace.client;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ConstantsPortlet {

	// ToolBar button
	public static final String CATEGORIZE = "";

	public static final String REFRESH = "Refresh";
	public static final String ADDFOLDER = "New Folder";
	public static final String DELETEITEM = "Delete";
	public static final String RENAMEITEM = "Rename";
	public static final String UPLOADFILE = "Upload";
	public static final String DOWNLOADITEM = "Download";
	public static final String UPLOADARCHIVE = "Upload Archive";
	public static final String CHANGEPERMISSION = "Change Permissions";

	//USED IN HTTP GET AS PARAMETERS
	public static final String GET_SEARCH_PARAMETER ="search";
	public static final String GET_ITEMID_PARAMETER ="itemid";
	public static final String GET_OPERATION_PARAMETER ="operation";
	public static final String GET_VALIDATE_SESSION="validatesession";

	// Div Gwt
	public static final String WORKSPACEDIV = "workspaceDiv";

	// Panels Names
	public static final String WORKSPACE = "Workspace";
	public static final String EXPLORER = "Explorer";
	public static final String DETAILS = "Details";
	public static final String RESULT = "Result";

	public enum ViewSwitchTypeInResult {
		List, Icons, Group, Messages
	};

	// Messages
	public static final String FIELDVALIDATEERROR = "The field must be alphanumeric";

	// Filter Panel
	public static final String SEARCH = "Search";
	public static final String SAVE = "Save";
	public static final String CANCEL = "Cancel";
	public static final String SEARCHBYNAME = "Search by name";
	public static final String SEARCHINMESSAGE = "Search in messages";
	public static final String VIEWSPACE = "";//"Filter by Space";
	public static final String PREVIEW = "Preview";
	public static final String OPEN = "Open";

	public static final String TITLEACCESSWEBDAV = "Desktop Access";

	//COOKIE SETTINGS
	public static final String GCUBE_COOKIE_WORKSPACE_GRID_VIEW_SETTING = "GCUBE-Cookie-WorkspaceGridViewSetting";
	public static final String GCUBE_COOKIE_WORKSPACE_AVAILABLE_FEATURES = "GCUBE-Cookie-WorkspaceAvailableFeatures";
	public static final int COOKIE_EXPIRE_DAYS = 30;
	public static final long MILLISECS_PER_DAY = 1000L * 60L * 60L * 24L;


	public static final int NORTH_HEIGHT = 82;

}

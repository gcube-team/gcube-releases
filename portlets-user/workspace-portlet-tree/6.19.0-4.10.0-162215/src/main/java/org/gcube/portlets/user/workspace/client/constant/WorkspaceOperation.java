package org.gcube.portlets.user.workspace.client.constant;

public enum WorkspaceOperation {

	INSERT_FOLDER("INS", "INS"), // Insert Folder
	ADD_ITEM("ADD", "ADD"), // Add Folder
	REMOVE("REM", "REM"),
	UPLOAD_FILE("UPL", "UPL"),
	DOWNLOAD("DWL", "DWL"),
	PREVIEW("PRW", "PRW"),
	RENAME("RNM", "RNM"),
	INSERT_SHARED_FOLDER("ISHF", "ASHF"), // Insert shared Folder
	PUBLISH_ON_DATA_CATALOGUE("PODC", "PODC"),
	PUBLISH_ON_THREDDS("POTD", "POTD"),
	SHARE("SHR", "SHR"), // SHARE
	UNSHARE("USHR", "USHR"), // UNSHARE
	UPLOAD_ARCHIVE("UPA", "UPA"), // Upload Archive
	LINK("LNK", "LNK"),
	SHOW("SHW", "SHW"), // SHOW
	ADD_URL("CLK", "CLK"), // Add Url
//	OPEN_REPORT_TEMPLATE("ORT", "ORT"), // Open report template
//	OPEN_REPORT("ORP", "ORP"), // Open report
	SENDTO("SDT", "SDT"), // Send to
	GET_NEW_MESSAGES("DWM", "DWM"), // Get all new messages
	CREATE_NEW_MESSAGE("CNM", "CNM"), // Create new message
	MARK_AS_READ("MKR", "MKR"), // Mark as read
	MARK_AS_UNREAD("MKNR", "MKNR"), // Mark as not read
	DELETE_MESSAGE("DLM", "DLM"), // Delete message
	FORWARD_MESSAGE("FWM", "FWM"), // Forward message
	COPY("CPI", "CPI"), // copy item
	PASTE("PSI", "PSI"), // paste
	REFRESH_FOLDER("RFH", "RFH"), // Refresh
	WEBDAV_URL("WDV", "WDV"), // WebDav
	GET_INFO("GTI", "GTI"), // GET INFO
	HISTORY("HST", "HST"), //HISTORY
	ACCREAD("ACR", "ACR"), //ACCOUNTING READ
	SHARE_LINK("SLK", "SLK"), //SHARE URL LINK
	PUBLIC_LINK("PLK", "PLK"), //PUBLIC LINK
//	ADD_ADMINISTRATOR("AAD", "AAD"), // ADD_ADMINISTRATOR
	VRE_CHANGE_PERIMISSIONS("CHP", "CHP"),
	EDIT_PERMISSIONS("EDP", "EDP"), //EDIT PERMISSIONS
	FOLDER_LINK("FRL", "FRL"), //FOLDER LINK
	FOLDER_LINK_REMOVE("FPR","FPR"),
	VERSIONING("VRN", "VRN"); //VERSIONING


	private String id; // ID CONTEXT MENU
	private String name;

	WorkspaceOperation(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}

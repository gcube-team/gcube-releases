package org.gcube.portlets.user.messages.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * 
 */
public class ConstantsPortletMessages {

	public static final Logger messagesLogger = Logger.getLogger("Messages");
	
	// Div Gwt
	public static final String PORTLETDIV = "MESSAGES_DIV";

	public static final String DOWNLOAD_WORKSPACE_SERVICE = GWT.getModuleBaseURL() + "DownloadAttachService";
	// Panels Names
	
	public static final String EXPLORER = "Explorer";
	
	public static final int DEFAULT_HEIGHT = 600;
	
	//********************** COPIED FROM WORKSPACE
	
	//MESSAGE DIALOG BOX constants
	public static final String MESSAGE_CONFIRM_DELETE_ITEM = "Are you sure you want to delete item";
	public static final String MESSAGE_CONFIRM_DELETE_SMART_FOLDER = "Are you sure you want to delete smart folder";
	public static final String MESSAGE_ADD_SMART_FOLDER = "Add Smart Folder";
	public static final String MESSAGE_ADD_FOLDER = "Add Folder";
	public static final String MESSAGE_ADD = "Add";
	public static final String MESSAGE_ADD_FOLDER_IN = "Add Folder in: ";
	public static final String MESSAGE_ITEM_NAME = "Item Name";
	public static final String MESSAGE_RENAME = "Rename";
	public static final String MESSAGE_DELETE_ITEM = "Delete Item";
	public static final String MESSAGE_DELETE = "Delete";
	public static final String MESSAGE_UPLOAD_FILE = "Upload File";
	public static final String MESSAGE_UPLOAD_ARCHIVE = "Upload Archive";
	public static final String MESSAGE_FOLDER_NAME = "Folder Name";
	public static final String MESSAGE_SMART_FOLDER_NAME = "Smart Folder Name";
	public static final String LOADING = "Loading";
	public static final String LOADINGSTYLE = "x-mask-loading";
	public static final String VALIDATINGOPERATION = "Validating operation";
	public static final String MESSAGE_DOWNLOAD_ITEM = "Download Item";
	public static final String MESSAGE_PREVIEW = "Preview";
	public static final String MESSAGE_SHOW = "Show";
	public static final String MESSAGE_OPEN_URL = "Open Url";
	public static final String MESSAGE_ADD_URL = "Add Url";
	public static final String MESSAGE_ADD_URL_IN = "Add Url in:";
	public static final String MESSAGE_OPEN_REPORT_TEMPLATE = "Open R. Template";
	public static final String MESSAGE_OPEN_REPORT = "Open Report";
	public static final String MESSAGE_SEND_TO = "Send to";
	public static final String MESSAGE_SENT_IN_DATE = "Sent in date";
	public static final String MESSAGE_ADD_CONTACT = "Add Contact";
	public static final String MESSAGE_ADD_SUBJECT = "Add Subject";
	public static final String MESSAGE_ERROR_OCCURED = "Sorry an error has occurred while processing your request";
	public static final String INFO = "Info";
	public static final String MESSAGE_SEND_TO_OK = "Your message has been successfully delivered";
	public static final String COPYITEM = "Copy";
	public static final String PASTEITEM = "Paste";
	public static final String MESSAGE_REFRESH_FOLDER = "Refresh Folder";
	public static final String LISTATTACHMENTSNAMES = "Attachments Names";
	public static final String MAPCONTACTSTO = "MapContactsTo";
	public static final String STATUS = "Status";
	public static final String LOADER = "Loader";
	public static final String DOWNLOADSTATE = "DownloadState";
	public static final String NUMFAILS = "numfails";
	public static final String NUMREQUESTS = "numrequests";
	public static final String MESSAGE_WEBDAV_URL = "View Url WebDAV ";
	public static final String URL_WEBDAV = "WebDAV URL";
	public static final String MESSAGE_NAME_FORCE_APHANUMERIC = "Field name must be alphanumeric and not contain special chars!";
	public static final String MESSAGE_SEARCH_FORCE_APHANUMERIC = "Field search must be alphanumeric and not contain special chars!";
	public static final String MESSAGE_CREATE_NEW_MESSAGE = "Create New";
	public static final String FORWARDTO = "Forward to";
	public static final String MESSAGETYPE = "Message Type";
	public static final String WEBDAVURLLINKREADMORE = "https://gcube.wiki.gcube-system.org/gcube/index.php/Acces_workspace_from_Desktop_(via_WebDAV)";
	
	public static final String TITLEACCESSWEBDAV = "Access from Desktop";
	public static final String ACCESSWEBDAVMSG = "Files and folders can be managed directly from the file explorer of your desktop operating system.";
	
	public static final String FILEUPLOADHEADER = "File upload in: ";
	public static final String FILE = "File";
	public static final String ERROR = "Error";
	public static final String NOFILESPECIFIED = "No file specified";
	public static final String PROGRESS = "Progress";
	public static final String SAVINGYOURFILE = "Saving your file";
	public static final String PREVIEWOF = "Preview of: ";
	
	public static final String ARCHIVEUPLOADHEADER = "Archive upload in: ";
	public static final String ARCHIVE = "Archive";
	
	public static final String DIALOG_DESCRIPTION = "Description";
	public static final String DIALOG_NAME = "Name";
	public static final String DIALOG_URL = "Url";
	public static final String ERRORURLNOTREACHABLE = "Error: url is not reachable!";
	
	//FILE MODEL e Smart Folder constants
	public static final String ROOT = "root";
	public static final String NAME = "Name";
	public static final String TYPE = "Type";
	public static final String ICON = "Icon";
	public static final String SHORTCUTCATEGORY = "Category";
//	public static final String GRIDCOLUMNTYPE = "Type";
	public static final String IDENTIFIER = "identifier";
	public static final String FOLDERITEMTYPE = "folderItemType";
	public static final String PARENT = "parent";
	public static final String ISDIRECTORY = "isDirectory";
	public static final String ISSHARED = "isShared";
	public static final String LASTMODIFIED = "lastModified";	
	public static final String DESCRIPTION = "description";
	public static final String OWNER = "owner";
	public static final String QUERY = "query";
	public static final String FOLDERNOTLOAD = "notload";
	public static final String FOLDERLOADED = "loaded";
	
	//GRID COLUMNS constants
	public static final String GRIDCOLUMNCREATIONDATE = "Creation Date";
	public static final String SIZE = "Size";
	public static final String EMPTY = "EMPTY";
	
	//ID CONTEXT MENU
	public static final String INS = "INS"; //Insert Folder
	public static final String ADD = "ADD";
	public static final String REM = "REM";
	public static final String UPL = "UPL"; //Upload File
	public static final String DWL = "DWL";
	public static final String PRW = "PRW";
	public static final String RNM = "RNM";
	public static final String UPA = "UPA"; //Upload Archive
	public static final String LNK = "LNK"; 
	public static final String SHW = "SHW";
	public static final String CLK = "CLK"; //Add Url
	public static final String ORT = "ORT";	//Open report template
	public static final String ORP = "ORP"; //Open report
	public static final String SDT = "SDT"; //Send to
	public static final String DWM = "DWN"; //Get all new messages
	public static final String MKR = "MKR"; //Mark as read
	public static final String MKNR = "MKNR"; //Mark as not read
	public static final String DLM = "DLM"; //Delete message
	public static final String FWM = "FWM"; //Forward message
	public static final String CPI = "CPI"; //copy item
	public static final String PSI = "PSI"; //paste
	public static final String RFH = "RFH"; //Refresh
	public static final String WDV = "WDV"; //WebDav
	public static final String CNM = "CNM"; //Create new message
	
	//ID CONTEXT MENU IN GRID
	public static final String OPM = "OPM"; //Open message
	public static final String SVA = "SVA"; //Save attachs
	public static final String MESSAGE_SAVE_ATTACHS = "Save Attachments";
	
	//Toolbar Item Name
	public static final String TREE = "Tree";
	public static final String SMARTFOLDER = "Smart Folder";
	public static final String MESSAGES = "Messages";
	public static final String SHORTCUTVIEW = "Shortcut View";
	public static final String TREEVIEW = "Tree View";

	//Used in set value in session
	public static final String IDTEMPLATE = "idtemplate";
	public static final String TEMPLATECREATION = "template-creation";
	public static final String IDREPORT = "idreport";
	public static final String REPORTGENERATION = "report-generation";

	//Info contact constant
	public static final String LOGIN = "login";
	public static final String FULLNAME = "From";
	
	//GRID COLUMN ADD CONTACT
	public static final String GRIDCOLUMNLOGIN = "Login";
	
	public static final String SUBJECT = "Subject";
	public static final String FROM = "From";
	public static final String DATE = "Date";
	public static final String NUMATTACHS = "Num Attachs";
	public static final String FROMLOGIN = "From Contact";
//	public static final String LISTCONTACTSTO = "ListContactsTo";
	public static final String LISTATTACHS = "ListAttachs";
	public static final String TEXTMESS = "TextMess";
	public static final String ISREAD = "IsRead";
	
	//Message grid and context menu
	public static final String MESSAGE_GET_NEW_MESSAGES = "Get new messages";
	public static final String MESSAGE_MARK_AS_READ = "Mark as Read";
	public static final String MESSAGE_MARK_AS_NOTREAD = "Mark as Not Read";
	public static final String MESSAGE_DELETE_MESSAGE = "Delete";
	public static final String MESSAGE_FORWARD_MESSAGE = "Forward";	
	public static final String MESSAGE_REPLY = "Reply";
	public static final String MESSAGE_REPLY_ALL = "Reply All";

	//USED in message model
	public static final String ID = "id";
	public static final String ATTACHS = "Attachs";
	public static final Object NONE = "None";
	public static final String ABSTRACTICON = "Abstract Icon";

	
	public static final String SESSION_EXPIRED_DIV = "<div class=\"session-expired\">" +
			"Ops! There were problems while retrieving your messages!" +
			"<br> Your session expired, please try to <a href=\"/c/portal/logout\">login again</a> ";
	
	
	public enum ViewSwitchType {Tree, SmartFolder, Messages};
	
	
}

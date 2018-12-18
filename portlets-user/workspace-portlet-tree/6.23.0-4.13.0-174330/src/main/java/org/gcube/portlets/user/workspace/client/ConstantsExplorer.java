package org.gcube.portlets.user.workspace.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;

//import com.google.gwt.dom.client.Element;
//import com.google.gwt.user.client.Element;

/**
 * The Class ConstantsExplorer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class ConstantsExplorer {
	public static final String CLARIN_SWITCHBOARD_ENDPOINT_FALLBACK = "https://weblicht.sfs.uni-tuebingen.de/clrs/#/d4science/";
	public static final String CLARIN_SWITCHBOARD_ENDPOINT_NAME = "CLARIN Switchboard";
	public static final String CLARIN_SWITCHBOARD_ENDPOINT_CATEGORY = "OnlineService";

	public static final String PARTHENOS_GATEWAY_HOST_NAME = "parthenos.d4science.org";
	//public static final String PARTHENOS_GATEWAY_HOST_NAME = "127.0.0.1"; //for trying in dev

//	public static final String FILE_SERVICE = GWT.getModuleBaseURL() + "fileservice";
	public static final String RPC_WORKSPACE_SERVICE = GWT.getModuleBaseURL() + "rpcWorkspace";
	public static final String UPLOAD_WORKSPACE_SERVICE = GWT.getModuleBaseURL() + "UploadService";
	public static final String LOCAL_UPLOAD_WORKSPACE_SERVICE = GWT.getModuleBaseURL() + "LocalUploadService";
	public static final String DOWNLOAD_WORKSPACE_SERVICE = GWT.getModuleBaseURL() + "DownloadService";
	public static final String IMAGE_SERVLET = GWT.getModuleBaseURL() + "ImageService";
	public static final String DOWNLOAD_WORKSPACE_FOLDER_SERVICE = "downloadfolder";
	public static final String DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_DO_ZIP = "ZIP";
	public static final String DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ZIPPING = "ZIPPING";
	public static final String DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_NOT_FOUND = "ERROR_NOT_FOUND";
	public static final String DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_DURING_COMPRESSION = "ERROR_DURING_COMPRESSION";
	public static final String DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_NOT_A_FOLDER = "ERROR_NOT_A_FOLDER";
	public static final String DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_SESSION_EXPIRED = "ERROR_SESSION_EXPIRED";


	public static final String SERVER_ERROR = "Error on";
	public static final String TRY_AGAIN = "Try again";

	public static final int TIME_BULK_CREATOR_POLLING = 300*1000; //in milliseconds

	//MESSAGE DIALOG BOX constants
	public static final String MESSAGE_CONFIRM_DELETE_ITEM = "Are you sure you want to delete item";
	public static final String MESSAGE_CONFIRM_DELETE_SMART_FOLDER = "Are you sure you want to delete smart folder";
	public static final String MESSAGE_ADD_SMART_FOLDER = "New Smart Folder";
	public static final String MESSAGE_ADD_FOLDER = "New Folder";
	public static final String MESSAGE_ADD_SHARED_FOLDER = "New Shared Folder";
	public static final String MESSAGE_DATA_CATALOGUE_PUBLISH = "Publish on Catalogue";
	public static final String MESSAGE_THREDDS_PUBLISH = "Sync with THREDDS";
	public static final String MESSAGE_CLARIN_SWITCHBOARD = "Send to Switchboard";
	public static final String MESSAGE_ADD = "Add";
	public static final String MESSAGE_ADD_FOLDER_IN = "Create a new folder in: ";
	public static final String MESSAGE_ITEM_NAME = "Item Name";
	public static final String MESSAGE_RENAME = "Rename";
	public static final String MESSAGE_DELETE_ITEM = "Delete Item";
	public static final String MESSAGE_DELETE = "Delete";
	public static final String MESSAGE_UPLOAD_FILE = "Upload File/s";
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
	public static final String MESSAGE_ADD_URL = "New Url";
	public static final String MESSAGE_ADD_URL_IN = "Create a new url in:";
	public static final String MESSAGE_OPEN_REPORT_TEMPLATE = "Open R. Template";
	public static final String MESSAGE_OPEN_REPORT = "Open Report";
	public static final String MESSAGE_SEND_TO = "Send to";
	public static final String MESSAGE_SENT_IN_DATE = "Sent in date";
	public static final String MESSAGE_ADD_CONTACT = "Add Contact";
	public static final String MESSAGE_ADD_SUBJECT = "Add Subject";
	public static final String HISTORY = "History";
	public static final String ACCREAD = "Read";
	public static final String MESSAGE_EXECUTE_DM_TASK = "Execute DM Task";
	public static final String MESSAGE_ERROR_OCCURED = "Sorry an error has occurred while processing your request";
	public static final String INFO = "Info";
	public static final String MESSAGE_SEND_TO_OK = "Your message has been successfully delivered";
	public static final String MESSAGE_SHARE_LINK = "Get Link";
	public static final String MOVE = "Move";
	public static final String COPY = "Make a Copy";
	//public static final String PASTEITEM = "Paste";
	public static final String MESSAGE_REFRESH_FOLDER = "Refresh Folder";
	public static final String MESSAGE_GET_INFO = "Get Info";
	public static final String FILE_VERSIONS = "Versions";
	public static final String MESSAGE_PUBLIC_LINK = "Get Public Link";
	public static final String MESSAGE_FOLDER_LINK = "Get Folder Link";
	public static final String MESSAGE_FOLDER_LINK_REMOVE = "Remove Folder Link";
	public static final String LISTATTACHMENTSNAMES = "Attachments Names";
	public static final String LISTCONTACTSTOSTRING = "ListContactToString";
	public static final String STATUS = "Status";
	public static final String LOADER = "Loader";
	public static final String DOWNLOADSTATE = "DownloadState";
	public static final String NUMFAILS = "numfails";
	public static final String NUMREQUESTS = "numrequests";
	public static final String MESSAGE_WEBDAV_URL = "View Url WebDAV ";
	public static final String URL_WEBDAV = "WebDAV URL";

	public static final String MESSAGE_SEARCH_FORCE_APHANUMERIC = "Field search must be alphanumeric and not contain special chars!";
	public static final String MESSAGE_CREATE_NEW_MESSAGE = "Create New Message";

	public static final String FORWARDTO = "Forward to";
	public static final String MESSAGETYPE = "Message Type";
	public static final String WEBDAVURLLINKREADMORE = "https://gcube.wiki.gcube-system.org/gcube/index.php/Acces_workspace_from_Desktop_(via_WebDAV)";
	public static final String EDIT_PERMISSIONS = "Edit Permissions";
	public static final String TITLEACCESSWEBDAV = "Access from Desktop";
	public static final String ACCESSWEBDAVMSG = "Files and folders can be managed directly from the file explorer of your desktop operating system.";
	public static final String MOVING = "Moving...";
	protected static final String UNSHARING = "Unsharing...";

	public static final String FILEUPLOADHEADER = "File upload in: ";

	public static final String REGEX_TO_WSITEM_NAME = "[^\\[\\]<>\\|?/*%$\\\\:]*$";
	public static final String REGEX_TO_WSFOLDER_NAME = "[^\\[\\]<>\\|?/*%$\\\\:]*$";
	public static final String REGEX_WSITEM_NAME_ALERT_MSG = "Field name must not contain: <>[]:\\|?/*%$ or contains / or \\";
	public static final String REGEX_WSFOLDER_NAME_ALERT_MSG = "Folder name must not contain: <>[]:\\|?/*%$ or contains / or \\";

//	public static final String FILE = "File";
//	public static final String ARCHIVE = "Archive";

	/**
 * The Enum WS_UPLOAD_TYPE.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 29, 2016
 */
public static enum WS_UPLOAD_TYPE {File, Archive};

	public static final String ERROR = "Error";
	public static final String NOFILESPECIFIED = "No file specified";
	public static final String PROGRESS = "Progress";
	public static final String SAVINGYOURFILE = "Saving your file";
	public static final String PREVIEWOF = "Preview of: ";

	public static final String ARCHIVEUPLOADHEADER = "Archive upload in: ";


	public static final String DIALOG_DESCRIPTION = "Description";
	public static final String DIALOG_NAME = "Name";
	public static final String DIALOG_URL = "Url";
	public static final String ERRORURLNOTREACHABLE = "Error: url is not reachable!";

	//FILE MODEL e Smart Folder constants
	public static final String ROOT = "root";
//	public static final String GRIDCOLUMNTYPE = "Type";


	public static final String FOLDERNOTLOAD = "notload";
	public static final String FOLDERLOADED = "loaded";
	public static final String MARKASREAD = "MARKASREAD";
	public static final String ISROOT = "ISROOT";
	public static final String ISSHAREABLE = "ISSHAREABLE";
	public static final String DIRECTORYDESCRIPTION = "DIRECTORYDESCRIPTION";


	//GRID COLUMNS constants



	public static Map<String, String> operations = new HashMap<String, String>();


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

	//GRID COLUMN ADD CONTACT
	public static final String GRIDCOLUMNLOGIN = "Login";

	public static final String SUBJECT = "Subject";
	public static final String FROM = "From";
	public static final String DATE = "Date";
	public static final String NUMATTACHS = "Num Attachs";
	public static final String FROMLOGIN = "From Contact";
	public static final String LISTCONTACTSTO = "ListContactsTo";
	public static final String LISTATTACHS = "ListAttachs";
	public static final String TEXTMESS = "TextMess";
	public static final String ISREAD = "IsRead";

	//Message grid and context menu
	public static final String MESSAGE_GET_ALL_NEW_MESSAGES = "Get all new messages";
	public static final String MESSAGE_MARK_AS_READ = "Mark as Read";
	public static final String MESSAGE_MARK_AS_NOTREAD = "Mark as Not Read";
	public static final String MESSAGE_DELETE_MESSAGE = "Delete Message";
	public static final String MESSAGE_FORWARD_MESSAGE = "Forward Message";
	public static final String MESSAGE_REPLY = "Reply";
	public static final String MESSAGE_REPLY_ALL = "Reply All";


	//USED in message model
	public static final String ID = "id";
	public static final String ATTACHS = "Attachs";
	public static final Object NONE = "None";


	//USED IN ACCOUNTINGS
	public static final String ACCOUNTING_HISTORY_OF = "Accounting history of: ";
	public static final String ACCOUNTING_READERS_OF = "Accounting readers of: ";

	public static final NumberFormat numberFormatterKB = NumberFormat.getFormat("#,##0 KB;(#,##0 KB)");

	//USED IN HTTP GET AS PARAMETER.. THIS PARAMS ARE REPLICATED IN THE CONSTANTS OF TREE WIDGET
	public static final String GET_SEARCH_PARAMETER ="search";
	public static final String GET_ITEMID_PARAMETER ="itemid";
	public static final String GET_OPERATION_PARAMETER ="operation";

	/**
	 * The Enum WsPortletInitOperation.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Sep 29, 2016
	 */
	public static enum WsPortletInitOperation {sharelink, gotofolder}; //INIT OPERATIONS
	//DEFAULT INIT OPERATION
	public static final WsPortletInitOperation DEFAULT_OPERATION = WsPortletInitOperation.gotofolder;

	/**
	 * The Enum ViewSwitchType.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Sep 29, 2016
	 */
	public enum ViewSwitchType {Tree, SmartFolder, Messages};

	//SERLVET PARAMETERS
	public static final String ERROR_ITEM_DOES_NOT_EXIST = "Item does not exist. It may have been deleted by another user";
	public static final String VALIDATEITEM = "validateitem";
	public static final String CURRENT_CONTEXT_ID = "contextID";
	public static final String REDIRECTONERROR = "redirectonerror";
	public static final String FILE_VERSION_ID = "fileversionid";

	//UPLOAD SERVLET PARAMETERS
	public static final String IS_OVERWRITE = "isOverwrite";
	public static final String UPLOAD_TYPE = "uploadType";
	public static final String ID_FOLDER = "idFolder";
	public static final String UPLOAD_FORM_ELEMENT = "uploadFormElement";


	//PROPERTY
	public static final String SPECIALFOLDERNAME = "SPECIALFOLDERNAME";
	public static final String SPECIALFOLDERNAMEPROPERTIESFILE = "specialfoldername.properties";

	public static final String MY_SPECIAL_FOLDERS = "MySpecialFolders";

	public static final int HEIGHT_DIALOG_SHARE_FOLDER = 445;


	public static final String MSG_FOLDER_LOCKED_BY_SYNC= "is under synchronization process.\nPlease be aware that some operations are not currently available and that the content might change during this process.";



	/**
	 * Log.
	 *
	 * @param txt the txt
	 */
	public static native void log(String txt) /*-{
	  console.log(txt)
	}-*/;

}

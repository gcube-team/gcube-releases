package org.gcube.portlets.user.dataminermanager.shared;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class Constants {
	public static final boolean DEBUG_MODE = false;
	public static final boolean TEST_ENABLE = false;

	public static final String APPLICATION_ID = "org.gcube.portlets.user.dataminermanager.portlet.DataMinerManager";
	public static final String DATA_MINER_MANAGER_ID = "DataMinerManagerId";
	public static final String DATA_MINER_LANG_COOKIE = "DataMinerLangCookie";
	public static final String DATA_MINER_LANG = "DataMinerLang";
	public static final String DATA_MINER_OPERATOR_ID = "OperatorId";
	
	public static final String DEFAULT_USER = "giancarlo.panichi";
	public final static String DEFAULT_SCOPE = "/gcube/devNext/NextNext";
	public final static String DEFAULT_TOKEN = "";

	public static final String DEFAULT_ROLE = "OrganizationMember";
	
	public static final String SClientMap = "DataMinerClientMap";
	public static final String DATA_MINER_SERVICE_NAME = "DataMiner";
	public static final String DATAMINER_SERVICE_CATEGORY = "DataAnalysis";

	
	public static final String TD_DATASOURCE_FACTORY_ID = "DataMinerManager";

	public static final int TIME_UPDATE_COMPUTATION_STATUS_PANEL = 5 * 1000;// 7*1000;

	
	// WPS Data Miner
	public static final String WPSServiceURL = "http://dataminer-d-d4s.d4science.org:80/wps/";
	public static final String WPSWebProcessingService = "WebProcessingService";
	public static final String WPSCancelComputationServlet = "CancelComputationServlet";
	public static final String WPSToken = "";
	public static final String WPSUser = "giancarlo.panichi";
	public static final String WPSLanguage = "en-US";
	
	/*public static final String WPSUser = "gianpaolo.coro";*/
	
	
	//DownloadFolderServlet
	public static final String DOWNLOAD_FOLDER_SERVLET= "DownloadFolderServlet";
	public static final String DOWNLOAD_FOLDER_SERVLET_ITEM_ID_PARAMETER = "itemId";
	public static final String DOWNLOAD_FOLDER_SERVLET_FOLDER_NAME_PARAMETER = "folderName";
	
	//Session
	public static final String CURR_GROUP_ID="CURR_GROUP_ID";
	public static final String CURR_USER_ID = "CURR_USER_ID";

}

package org.gcube.usecases.ws.thredds;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final SimpleDateFormat DATE_FORMAT= new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
	
	public static final String LOCK_FILE="~WS-LOCK.lock";
	public static final String GCUBE_TOKEN_HEADER="gcube-token";
	
	public static final String LAST_UPDATE_TIME="WS-SYNCH.LAST_UPDATE";

	public static final String THREDDS_PERSISTENCE="thredds";
	public static final String THREDDS_DATA_TRANSFER_BASE_URL="data-transfer-service/gcube/service/REST/"+THREDDS_PERSISTENCE+"/";
	
	
	public static final String SDI_THREDDS_BASE_URL="sdi-service/gcube/service/Thredds";
	
	
	public static final String SIS_PLUGIN_ID="SIS/GEOTK";
	
	public static class WorkspaceProperties{
		// Folder
		public static final String SYNCH_FILTER="WS-SYNCH.SYNCHRONIZATION-FILTER";
		public static final String TARGET_TOKEN="WS-SYNCH.TARGET-TOKEN";
		public static final String REMOTE_PATH="WS-SYNCH.REMOTE-PATH";
		public static final String REMOTE_PERSISTENCE="WS-SYNCH.REMOTE-PERSISTENCE";
		public static final String RELATED_CATALOG="WS-SYNCH.RELATED-CATALOG";
		public static final String VALIDATE_METADATA="WS-SYNCH.VALIDATE-METADATA";
		public static final String ROOT_FOLDER_ID="WS-SYNCH.ROOT-FOLDER-ID";
		
		
		// Common
		public static final String TBS="WS-SYNCH.TO-BE-SYNCHRONIZED";
		public static final String SYNCHRONIZATION_STATUS="WS-SYNCH.SYNCH-STATUS";
		public static final String LAST_UPDATE_TIME="WS-SYNCH.LAST-UPDATE-TIME";		
		public static final String LAST_UPDATE_STATUS="WS-SYNCH.LAST-UPDATE-STATUS";
		
		// ITEM
		public static final String METADATA_UUID="WS-SYNCH.METADATA-UUID";
	}
	
	
	
	public static class Configuration{
		public static final String SCANNER_POOL_MAX_SIZE="scanner.pool.maxSize";
		public static final String SCANNER_POOL_CORE_SIZE="scanner.pool.coreSize";
		public static final String SCANNER_POOL_IDLE_MS="scanner.pool.idle.ms";
		
		public static final String TRANSFERS_POOL_MAX_SIZE="transfers.pool.maxSize";
		public static final String TRANSFERS_POOL_CORE_SIZE="transfers.pool.coreSize";
		public static final String TRANSFERS_POOL_IDLE_MS="transfers.pool.idle.ms";
	}
	
	public static final Map<String,String> cleanedItemPropertiesMap=new HashMap<String,String>();

	public static final Map<String,String> cleanedFolderPropertiesMap=new HashMap<String,String>();
	
	public static final Map<String,String> defaultConfigurationMap=new HashMap<String,String>();
	
	static {
		cleanedItemPropertiesMap.put(Constants.WorkspaceProperties.TBS, null);
		cleanedItemPropertiesMap.put(Constants.WorkspaceProperties.LAST_UPDATE_TIME, null);
		cleanedItemPropertiesMap.put(Constants.WorkspaceProperties.LAST_UPDATE_STATUS, null);
		cleanedItemPropertiesMap.put(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS, null);
		cleanedItemPropertiesMap.put(Constants.WorkspaceProperties.METADATA_UUID, null);
		
		
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.SYNCH_FILTER, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.REMOTE_PATH, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.REMOTE_PERSISTENCE, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.TARGET_TOKEN, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.TBS, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.LAST_UPDATE_TIME, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.LAST_UPDATE_STATUS, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.RELATED_CATALOG, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.VALIDATE_METADATA, null);
		cleanedFolderPropertiesMap.put(Constants.WorkspaceProperties.ROOT_FOLDER_ID, null);
		
		
		defaultConfigurationMap.put(Configuration.SCANNER_POOL_CORE_SIZE, "1");
		defaultConfigurationMap.put(Configuration.SCANNER_POOL_MAX_SIZE, "10");
		defaultConfigurationMap.put(Configuration.SCANNER_POOL_IDLE_MS, "30000");
		
		defaultConfigurationMap.put(Configuration.TRANSFERS_POOL_CORE_SIZE, "1");
		defaultConfigurationMap.put(Configuration.TRANSFERS_POOL_MAX_SIZE, "10");
		defaultConfigurationMap.put(Configuration.TRANSFERS_POOL_IDLE_MS, "30000");
	}
	
}

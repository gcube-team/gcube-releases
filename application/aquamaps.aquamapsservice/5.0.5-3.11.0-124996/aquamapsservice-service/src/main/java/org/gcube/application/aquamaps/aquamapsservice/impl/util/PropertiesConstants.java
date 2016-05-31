package org.gcube.application.aquamaps.aquamapsservice.impl.util;

public class PropertiesConstants {
	
	//********************** PROPERTIES
	//*********	GENERAL

	public static final String ISCRAWLER_INTERVAL_MINUTES="ISCRAWLER_INTERVAL_MINUTES";
	public static final String ENABLE_SCRIPT_LOGGING="ENABLE_SCRIPT_LOGGING";
	public static final String PURGE_PENDING_OBJECTS="PURGE_PENDING_OBJECTS";
	public static final String PURGE_PENDING_HSPEC_REQUESTS="PURGE_PENDING_HSPEC_REQUESTS";
	public static final String CUSTOM_QUERY_KEEP_ALIVE_MINUTES="CUSTOM_QUERY_KEEP_ALIVE_MINUTES";
	public static final String CUSTOM_QUERY_DELETION="CUSTOM_QUERY_DELETION";
	
	//*********	ENVIRONMENT LIBRARY
	
	public static final String LOCAL_BATCH_POOL_SIZE="LOCAL_BATCH_POOL_SIZE";
	public static final String ANALYZER_BATCH_POOL_SIZE="ANALYZER_BATCH_POOL_SIZE";
	public static final String REMOTE_BATCH_POOL_SIZE="REMOTE_BATCH_POOL_SIZE";
	public static final String PROGRESS_MONITOR_INTERVAL_SEC="PROGRESS_MONITOR_INTERVAL_SEC";
	
	
	
	//********	INTERNAL WEB SERVER
	
	public static final String HTTP_SERVER_BASE_PORT="HTTP_SERVER_BASE_PORT";
	public static final String HTTP_SERVER_BASE_PATH="HTTP_SERVER_BASE_PATH";
	
	//******** 	RESOURCE MONITOR
	
	public static final String MONITOR_INTERVAL="MONITOR_INTERVAL";
	public static final String MONITOR_FREESPACE_THRESHOLD="MONITOR_FREESPACE_THRESHOLD";
	
	//********	GEOSERVER
	
	public static final String GEOSERVER_WAIT_FOR_DB_MS="GEOSERVER_WAIT_FOR_DB_MS";
	public static final String GEOSERVER_WAIT_FOR_FT="GEOSERVER_WAIT_FOR_FT";
	public static final String GEOSERVER_MAX_ATTEMPT="GEOSERVER_MAX_ATTEMPT";
	public static final String GEOSERVER_WAIT_FOR_RETRY_MINUTES="GEOSERVER_WAIT_FOR_RETRY_MINUTES";
	
	//********* WORKERS CONFIGURATION
	
	public static final String JOB_MAX_WORKERS="JOB_MAX_WORKERS";
	
	public static final String AQUAMAPS_OBJECT_MAX_WORKERS="AQUAMAPS_OBJECT_MAX_WORKERS";
	
	public static final String HSPEC_GROUP_MAX_WORKERS="HSPEC_GROUP_MAX_WORKERS";
	
	public static final String ANALYSIS_MAX_WORKERS="ANALYSIS_MAX_WORKERS";
	
	//********* DEFAULT DB VALUES
	
	public static final String INTEGER_DEFAULT_VALUE="INTEGER_DEFAULT_VALUE";
	public static final String DOUBLE_DEFAULT_VALUE="DOUBLE_DEFAULT_VALUE";
	public static final String BOOLEAN_DEFAULT_VALUE="BOOLEAN_DEFAULT_VALUE";
	
	
}

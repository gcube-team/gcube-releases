package org.gcube.application.datamanagementfacilityportlet.client.rpc;


public class Tags{

	public static final String sort="sort";
	public static final String dir="dir";
	public static final String ASC="ASC";
	public static final String START="start";
	public static final String OFFSET="offset";
	public static final String LIMIT="limit";
	
	
	
	public static final String JSONUTF8="application/json; charset=utf-8";
	public static final String IMAGE_PNG="image/png";
	public static final String TARGZ="application/octet-stream";
	public static final String TOTAL_COUNT="totalcount";
	public static final String DATA="data";
	public static final String EMPTY_JSON="{\""+DATA+"\":[],\""+TOTAL_COUNT+"\":0}";
	
	
	
	
	public static final String RESOURCE_TYPE="resource_type";
	public static final String FILE_ID="FILE_ID";
	
	
	
	//********** MONITOR PARAMS
	public static final String RESOURCE_ID="resId";
	
	
	
	//*********** Config
	
	public static final String DEFAULT_USER_PROPERTY="DEFAULT_USER";
	public static final String DEFAULT_SCOPE_PROPERTY="DEFAULT_SCOPE";
	public static final String isPortalMode="portalMode";
	public static final String DEFAULT_SERVICE_URL="DEFAULT_SERVICE_URL";
	public static final String ENVIRONMENT_CACHE_TIME_MINUTES="ENVIRONMENT_CACHE_TIME_MINUTES";
	public static final String INFRASTRUCTURE_SCOPE="INFRASTRUCTURE_SCOPE";
	
	// Response Object Tags
	
	public static final String responseGroupGeneration="RESPONSE_GROUP_GENERATION";
	public static final String errorMessage="ERROR_MESSAGE";
	public static final String REMOVE_REPONSE_MESSAGE="REMOVE_RESPONSE_MESSAGE";
	public static final String responseEdit="RESPONSE_EDIT";
	public static final String responseAnalysisSubmission="RESPONSE_ANALYSIS_SUBMISSION";
//	public static final String newsPublishResponse="PUBLISH_RESPONSE";
	
	// Session attributes
	
//	public static final String submittedObjects="SUBMITTED_OBJECTS";
	public static final String uploadedHCAFFile="UPLOADED_HCAF";
	public static final String uploadedHSPENFile="UPLOADED_HSPEN";
	public static final String currentDirectQuery="CURRENT_DIRECT_QUERY";
	
	public static final String TO_IMPORT_TYPE="TO_IMPORT_TYPE";
	
	// Servlets names
	
	public static final String submittedReportServlet="SubmittedReportServlet";
	public static final String serviceImpl="DataManagementFacilityService";
	public static final String resourceServlet="ResourceServlet";
	public static final String resourceLoadServlet="ResourceLoadServlet";
	public static final String resourceMapServlet="ResourceMapServlet";
	public static final String directQueryServlet="DirectQueryServlet";
	public static final String analysisServlet="AnalysisServlet";
	public static final String fileServlet="FileServlet";
	
	
	// CSV TARGET REGISTRY ENTRY
	
	public static final String CSV_TARGET="AQUAMAPS_DM_CSV_TARGET";
	
	
	//QUERY STRING PARAMS
	
	public static final String TO_OPEN_TABLE="tableID";
	
}

package org.gcube.spatial.data.sdi.model;

public class ServiceConstants {

	public static final String APPLICATION="SDI-Service";
	public static final String INTERFACE="SDI";
	
	public static final String SERVICE_CLASS="SDI";
	public static final String SERVICE_NAME="sdi-service";
	public static final String NAMESPACE="http://gcube-system.org/namespaces/data/sdi-service";
	
	
	public static final class Metadata{
		public static final String INTERFACE="Metadata";
		public static final String LIST_METHOD="list";
		public static final String PUBLISH_METHOD="publish";
		public static final String VALIDATE_PARAMETER="validate";
		public static final String PUBLIC_PARAMETER="public";
		public static final String STYLESHEET_PARAMETER="stylesheet";
		public static final String UPLOADED_FILE_PARAMETER="theMeta";
		public static final String METADATA_ENRICHMENTS_PARAMETER="metadataEnrichments";
		// Defaults
		
		public static final Boolean DEFAULT_VALIDATE=true;
		public static final Boolean DEFAULT_PUBLIC=false;
		public static final String DEFAULT_CATEGORY="Dataset";
		public static final String DEFAULT_STYLESHEET="_none_";
		
	}
		
	
	public static final class GeoNetwork{
		public static final String INTERFACE="GeoNetwork";
		public static final String CONFIGURATION_PATH="configuration";
		public static final String GROUPS_PATH="groups";
		
	}
	
	public static final class GeoServer{
		public static final String INTERFACE="GeoServer";
		public static final String HOST_PARAM="host";
	}

	
}

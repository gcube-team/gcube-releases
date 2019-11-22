package org.gcube.data.publishing.gCatFeeder.service;

public class ServiceConstants {

	public static final String SERVICE_NAME="gCat-Feeder"; 
	public static final String APPLICATION_PATH="/gcube/service/";
	
	
	public static interface Executions{
		
		public static final String PATH="execution";
		public static final String EXECUTION_ID_PARAMETER="executionId";
		
		
		public static final String COLLECTOR_ID_PARAMETER="collector";
		public static final String CATALOGUE_ID_PARAMETER="collector";
		
		public static final String DEFAULT_VALUE="ALL";
	}
	
	public static interface Capabilities{
		public static final String PATH="capabilities";
		
		public static final String COLLECTORS_PATH="collectors";
		
		public static final String CATALOGUES_PATH="catalogues";
	}
}

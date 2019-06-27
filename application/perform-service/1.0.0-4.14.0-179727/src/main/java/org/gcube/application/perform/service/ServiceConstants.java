package org.gcube.application.perform.service;

public interface ServiceConstants {

	public static final String SERVICE_NAME="perform-service";
	public static final String APPLICATION_PATH="/gcube/service/";
	
	public static interface Mappings{
		public static final String PATH="mappings";
		public static final String BATCHES_METHOD="batch";
		public static final String FARM_ID_PARAMETER="farmid";
		public static final String FARM_UUID_PARAMETER="farmuuid";
		public static final String BATCH_NAME_PARAMETER="name";
		public static final String BATCH_TYPE_PARAMETER="type";
		
		public static final String FARM_METHOD="farm";
		
		public static final String AUTH="statistical.manager";
	}
	
	
	public static interface Import{
		public static final String PATH="import";		
		public static final String BATCH_TYPE_PARAMETER="batch_type";
		public static final String FARM_ID_PARAMETER="farmid";
		public static final String EXCEL_FILE_PARAMETER="source";
		public static final String EXCEL_FILE_VERSION_PARAMETER="source_version";
		public static final String STATUS_PARAMETER="status";
		public static final String LAST_METHOD="last";
	}
	
	
	public static interface Performance{
		public static final String PATH="performance";
		public static final String FARM_ID_PARAMETER="farmid";
		public static final String AREA_PARAMETER="area";
		public static final String QUARTER_PARAMETER ="quarter";
		public static final String SPECIES_ID_PARAMETER="speciesid";
		public static final String BATCH_TYPE_PARAMETER="batch_type";
		public static final String PERIOD_PARAMETER="period";
		
		
		public static final String STATISTICS_PATH="statistics";
	}
}

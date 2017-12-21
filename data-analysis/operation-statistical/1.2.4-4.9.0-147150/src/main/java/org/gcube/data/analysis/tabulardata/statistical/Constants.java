package org.gcube.data.analysis.tabulardata.statistical;

public class Constants {

	//SM Service
	public static final String STATISTICAL_URI_PREFIX="statistical";
	
	public static final String DM_SERIVCE_CLASS="WPS";
	
	public static final String DM_SERVICE_NAME="DataMiner";
	
	//SM Algorithm
	public static final String OCEAN_AREA_ALGORITHM="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR";
	public static final String OCEAN_AREA_QUADRANT_ALGORITHM="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR_FROM_QUADRANT";
	public static final String CSQUARE_ALGORITHM="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.CSQUARE_COLUMN_CREATOR";
	public static final String OPERATOR_KEY="operator";
	
	
	
	
	// Expected Additional Columns
	public static final String CSQUARE_CODE_COLUMN="csquare_code";
	public static final String OCEAN_AREA_COLUMN="fao_ocean_area";
	
	// Messages
	
	public static final String ALGORITHM_NOT_FOUND="Algorithm not available, please contact your VRE Manager";
	public static final String SERVICE_NOT_FOUND="Statistical Service not available, please contact your VRE Manager";
}

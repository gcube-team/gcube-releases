package org.gcube.data.analysis.tabulardata.statistical;

import org.gcube.data.analysis.statisticalmanager.stubs.SMConstants;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;

public class Constants {

	//SM Service
	public static String STATISTICAL_URI_PREFIX="statistical";
	
	public static String STATISTICAL_SERIVCE_CLASS=SMConstants.SERVICE_CLASS;
	
	public static String STATISTICAL_SERVICE_NAME=SMConstants.SERVICE_NAME;
	
	//SM Algorithm
	public static String OCEAN_AREA_ALGORITHM="FAO_OCEAN_AREA_COLUMN_CREATOR";
	public static String OCEAN_AREA_QUADRANT_ALGORITHM="FAO_OCEAN_AREA_COLUMN_CREATOR_FROM_QUADRANT";
	public static String CSQUARE_ALGORITHM="CSQUARE_COLUMN_CREATOR";
	
	// Expected Additional Columns
	public static String CSQUARE_CODE_COLUMN="csquare_code";
	public static String OCEAN_AREA_COLUMN="fao_ocean_area";
	
	// Messages
	
	public static String ALGORITHM_NOT_FOUND="Algorithm not available, please contact your VRE Manager";
	public static String SERVICE_NOT_FOUND="Statistical Service not available, please contact your VRE Manager";
}

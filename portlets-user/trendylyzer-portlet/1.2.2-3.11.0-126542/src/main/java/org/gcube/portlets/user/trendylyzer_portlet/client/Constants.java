
package org.gcube.portlets.user.trendylyzer_portlet.client;


public class Constants {



	public static String VERSION = "1.0.0";
	public static boolean TEST_MODE = false;
	public final static String DEFAULT_USER = "angela.italiano";

	
	public static final String DEFAULT_SCOPE = "/gcube/devsec/devVRE";//"/gcube/devsec";
	
	public static final String TD_DATASOURCE_FACTORY_ID = "TrendyLyzer";
	
	public static final int TIME_UPDATE_MONITOR = 5*1000;
	public static final int TIME_UPDATE_JOBS_GRID = 10*1000;
	public static final int TIME_UPDATE_COMPUTATION_STATUS_PANEL = 7*1000;
	public static final String maskLoadingStyle = "x-mask-loading";

	
	public final static String[] classificationNames = {"User Perspective", "Computation Perspective"};
	public final static String userClassificationName = classificationNames[0];
	public final static String computationClassificationName = classificationNames[1];
	public static final String realFileTemplate = "ZZ-FILE";
	public static final String userFileTemplate = "FILE";
	
	public static final String APPLICATION_ID = "org.gcube.portlets.user.trendylyzer.portlet.TrendyLyzer";


	

}

/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client;


public class Constants {
	

public static final String TD_DATASOURCE_FACTORY_ID ="StatisticalManager";
//	public static final String TDX_DATASOURCE_FACTORY_ID = "TDDataSourceFactory";
//	public static final String URL_TD="jdbc:postgresql://node7.d.d4science.research-infrastructures.eu:5432/tabulardata";
	public static final int TIME_UPDATE_MONITOR = 30*1000;//5*1000;
	public static final int TIME_UPDATE_JOBS_GRID = 30*1000;//10*1000;
	public static final int TIME_UPDATE_COMPUTATION_STATUS_PANEL = 35*1000;//7*1000;
	public static final String maskLoadingStyle = "x-mask-loading";
	public static final String DEFAULT_SCOPE = "/gcube/devsec/devVRE";//"/gcube/devsec";

	
	public final static String[] classificationNames = {"User Perspective", "Computation Perspective"};
	public final static String userClassificationName = classificationNames[0];
	public final static String computationClassificationName = classificationNames[1];

	public static final String APPLICATION_ID = "org.gcube.portlets.widgets.statistical_manager_algorithms_widget.StatisticalManagerAlgorithmsWidget";

	public static final String realFileTemplate = "ZZ-FILE";
	public static final String userFileTemplate = "FILE";
	public static String VERSION = "1.1.0";
	public static boolean TEST_MODE = false;
	public final static String DEFAULT_USER = "fabio.sinibaldi";
	
	private static final String SM_DIV = "contentDiv";

}

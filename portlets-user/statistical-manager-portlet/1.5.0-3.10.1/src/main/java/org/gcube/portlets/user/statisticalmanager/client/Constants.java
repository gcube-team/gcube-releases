/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client;

//import org.apache.log4j.Logger;

/**
 * @author ceras
 *
 */
public class Constants {

//	public enum LOGTYPE {INFO, ERROR, LOG};
//	public static boolean localLog = true;
//	private static Logger logger = Logger.getLogger(Constants.class);

	public static String VERSION = "1.1.0";
	public static boolean TEST_MODE = true;
	public final static String DEFAULT_USER = 
				"angela.italiano";
//				"francesco.cerasuolo";
			


	public static final String DEFAULT_SCOPE = "/gcube/devsec";//"/gcube/devsec";
	
	public static final String TD_DATASOURCE_FACTORY_ID = "StatisticalManager";
	
	public static final int TIME_UPDATE_MONITOR =  5*1000;
	public static final int TIME_UPDATE_JOBS_GRID = 10*1000;
	public static final int TIME_UPDATE_COMPUTATION_STATUS_PANEL = 10*1000;//7*1000;
	public static final String maskLoadingStyle = "x-mask-loading";

	
	public final static String[] classificationNames = {"User Perspective", "Computation Perspective"};
	public final static String userClassificationName = classificationNames[0];
	public final static String computationClassificationName = classificationNames[1];
	public static final String realFileTemplate = "ZZ-FILE";
	public static final String userFileTemplate = "FILE";
	
	public static final String APPLICATION_ID = "org.gcube.portlets.user.statisticalmanager.portlet.StatisticalManager";

//	public static void log(String message) {
//		printLog(LOGTYPE.LOG, message);
//	}
//
//	public static void info(String message) {
//		printLog(LOGTYPE.INFO, message);
//	}
//
//	public static void error(String message) {
//		printLog(LOGTYPE.ERROR, message);
//	}
//The computation
//	public static void printLog(LOGTYPE logType, String message) {
//		if (Constants.localLog) {
//			if (logType==LOGTYPE.INFO)
//				System.out.println("[INFO] "+message);
//			else if (logType==LOGTYPE.ERROR)
//				System.out.println("[ERROR] "+message);
//			else
//				System.out.println("[LOG] "+message);
//		} else {
////			if (logType==LOGTYPE.INFO)
////				logger.info(message);
////			else if (logType==LOGTYPE.ERROR)
////				logger.error(message);
////			else
////				logger.debug(message);
//		}
//	}
	

}

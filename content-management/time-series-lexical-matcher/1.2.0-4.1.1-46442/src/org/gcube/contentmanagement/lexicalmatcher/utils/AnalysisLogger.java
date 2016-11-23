package org.gcube.contentmanagement.lexicalmatcher.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class AnalysisLogger {

	
	private static Logger logger;
	private static Logger hibernateLogger;
	
	public static Logger getLogger(){
		
		if (logger == null){
			setLogger("./ALog.properties");
			logger = Logger.getLogger("AnalysisLogger");
		}
		
		return logger;
	}
	//in ingresso vuole il path al file di config del log4j
	public static void setLogger(String path){
		if (logger == null){
			PropertyConfigurator.configure(path);
		}
		logger = Logger.getLogger("AnalysisLogger");
		hibernateLogger = Logger.getLogger("hibernate");
	}
	
	public static void printStackTrace(Exception e){
	    
	    int numberoflines = e.getStackTrace().length;
	    for (int i=0;i<numberoflines;i++){
		logger.error(e.getStackTrace()[i]);
	    }
	}
}

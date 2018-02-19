package org.gcube.contentmanagement.lexicalmatcher.utils;

import org.apache.log4j.Logger;

@Deprecated
public class AnalysisLogger {

	private static Logger logger = Logger.getLogger("AnalysisLogger");
	//private static Logger hibernateLogger = LoggerFactory.getLogger("HibernateLogger");

	public static Logger getLogger() {
		return logger;
	}

	// in ingresso vuole il path al file di config del log4j
	public static void setLogger(String path) {
		
	}

	public static void printStackTrace(Exception e) {
		logger.error("error ",e);
	}
}

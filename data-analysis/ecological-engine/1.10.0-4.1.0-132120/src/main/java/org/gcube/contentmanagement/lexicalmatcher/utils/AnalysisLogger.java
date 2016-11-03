package org.gcube.contentmanagement.lexicalmatcher.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootLogger;

public class AnalysisLogger {

	private static Logger logger;
	private static Logger hibernateLogger;

	public static Logger getLogger() {

		if (logger == null) {
			// setLogger("./ALog.properties");
			org.apache.log4j.BasicConfigurator.configure();
			// logger = Logger.getLogger("ConsoleAppender");
			logger = Logger.getRootLogger();
			logger.setLevel(Level.TRACE);
		}

		return logger;
	}

	// in ingresso vuole il path al file di config del log4j
	public static void setLogger(String path) {
		
		if (logger == null || (logger instanceof RootLogger)) {
			System.out.println("EcologicalEnginLibrary: setting logger to "+path);
			File f = new File(path);
			if (f.exists())
				PropertyConfigurator.configure(path);
			else {
				Properties p = new Properties();
				try {
					InputStream is = ClassLoader.getSystemResourceAsStream(path);
					p.load(is);
					is.close();
				} catch (IOException e1) {
					System.out.println("EcologicalEnginLibrary: Error in reading file"+path+" : "+e1.getLocalizedMessage());
				}
				PropertyConfigurator.configure(p);
			}
		}
		logger = Logger.getLogger("AnalysisLogger");
		hibernateLogger = Logger.getLogger("hibernate");
		
	}

	public static void printStackTrace(Exception e) {

		int numberoflines = e.getStackTrace().length;
		for (int i = 0; i < numberoflines; i++) {
			logger.error(e.getStackTrace()[i]);
		}
	}
}

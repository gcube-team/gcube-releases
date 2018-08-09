package org.gcube.dataanalysis.lexicalmatcher.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LexicalLogger {
	
	private static Logger logger = LoggerFactory.getLogger(LexicalLogger.class);

	public static Logger getLogger() {
		return logger;
	}

	public static void printStackTrace(Exception e) {

		int numberoflines = e.getStackTrace().length;
		for (int i = 0; i < numberoflines; i++) {
			logger.error(e.getMessage());
		}
	}
}

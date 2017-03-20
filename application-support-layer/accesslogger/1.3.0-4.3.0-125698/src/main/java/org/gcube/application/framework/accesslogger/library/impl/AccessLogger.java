package org.gcube.application.framework.accesslogger.library.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.application.framework.accesslogger.library.AccessLoggerI;
import org.gcube.application.framework.accesslogger.model.AccessLogEntry;
import org.gcube.application.framework.accesslogger.model.TemplateModel;

/**
 * Access logger class will be used to log all the needed entries
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class AccessLogger implements AccessLoggerI{
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(AccessLogger.class);
	
	private static final String dateFormat = "yyyyMMdd";
	private static final String logFileName = "accessLog";
	private static final String fileSeparator = File.separator;
	private static final String path = System.getProperty("java.io.tmpdir") + fileSeparator + "accessLogs";
	
	private BufferedWriter out = null;
	private Date lastWrittenDate = null;
	private static AccessLogger accessLogger = new AccessLogger();
	
	private MessageHandlingThread queueWorker = new MessageHandlingThread();
	// Queue that hosts the messages that will be written to the log file
	private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
	
	/*
	 * Private constructor of the class
	 */
	private AccessLogger() {
		try {
			logger.debug("Constructing a new access logger. Create a new file if it does not exist for the current date");
			lastWrittenDate = new Date();
			SimpleDateFormat dFormat = new SimpleDateFormat(dateFormat);
			String parsedDate = (dFormat.format(lastWrittenDate)).toString();
			// The name of the log will be like: accessLog20090827.log
			String logFileFullName = path + fileSeparator + logFileName + parsedDate + ".log";
			new File(path).mkdirs();
			File file = new File(logFileFullName);
			file.createNewFile();
			FileWriter fstream = new FileWriter(file,true);
			out = new BufferedWriter(fstream);
		} catch (Exception e) {
			logger.error("Failed to initialize access log.", e);
		}
		// start the thread that will handle the queue for writing to the access log file
		queueWorker.start();
	}

	/**
	 * Returns the singleton access logger object
	 * 
	 * @return The logger
	 */
	public static AccessLogger getAccessLogger() {
		return accessLogger;
	}
	
	/**
	 * Logs an entry to the appropriate file
	 * 
	 * @param username The username of the current user
	 * @param vre The current VRE
	 * @param entry The type of entry that will be logged
	 */
	public void logEntry(String username, String vre, AccessLogEntry entry) {
		// Get the current date. This will be used as a name for the logfile
		Date currentDate = new Date();

		TemplateModel tempModel = new TemplateModel(username, vre, entry.getType(), entry.getLogMessage(), currentDate);
		String logLineMsg = tempModel.createEntryLine();
		// add the message to the queue
		messageQueue.offer(logLineMsg);
	}
	
	

	/**
	 * Gets the proper file writer that will be used for the logging
	 * If a file already exists for the current date it will be used, otherwise a new one will be created
	 * 
	 * @return a Buffer writer to write the data
	 * @throws Exception Failed to create the writer
	 */
	private BufferedWriter getFileWriter() throws Exception {
		Date currDate = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat(dateFormat);
		String currentDate = (dFormat.format(currDate)).toString();
		String lastWrittenDate = (dFormat.format(this.lastWrittenDate)).toString();
		if (!currentDate.equals(lastWrittenDate)) {
			logger.debug("Date is: " + currentDate);
			String logFileFullName = path + fileSeparator + logFileName + currentDate + ".log";
			new File(path).mkdirs();
			File file = new File(logFileFullName);
			file.createNewFile();
			FileWriter fstream = new FileWriter(file,true);
			out.close();
		    out = new BufferedWriter(fstream);
		}
		return out;
	}
	
	/**
	 * Inner class that extends thread. Responsible for handling the message queue and writing to the log file
	 * 
	 * @author Panagiota Koltsida, NKUA
	 *
	 */
	private class MessageHandlingThread extends Thread{
		public MessageHandlingThread() {
			super();
			logger.debug("Creating a message handling object in order to handle the message queue");
		}
		
		/**
		 * Gets the appropriate file writer and writes the messages that are waiting in the queue
		 */
		public void run() {
			while(true) {
				try {
					String messageLine = messageQueue.take();
					BufferedWriter wr = getFileWriter();
					wr.write(messageLine);
					wr.flush();
				} catch (Exception e) {
					logger.error("An exception was thrown while trying to get the buffer writer.");
				}
			}	
		}
	}
}

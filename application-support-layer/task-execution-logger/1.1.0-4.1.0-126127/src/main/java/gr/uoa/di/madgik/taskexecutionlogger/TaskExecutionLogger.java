package gr.uoa.di.madgik.taskexecutionlogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import gr.uoa.di.madgik.taskexecutionlogger.exceptions.PropertiesFileRetrievalException;
import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;
import gr.uoa.di.madgik.taskexecutionlogger.utils.Constants;
import gr.uoa.di.madgik.taskexecutionlogger.utils.FileUtils;
import gr.uoa.di.madgik.taskexecutionlogger.utils.JSONConverter;


public class TaskExecutionLogger {

	/** The logger. */
	private static final Logger log = Logger.getLogger(TaskExecutionLogger.class);

	private static TaskExecutionLogger logger = null;
	private BufferedWriter out = null;
	private static String path = null;
	private static String logFileName = null;

	private MessageHandlingThread worker = new MessageHandlingThread();
	// Queue that hosts the messages that will be written to the log file
	private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();

	private TaskExecutionLogger() {
		String fName = null;
		try {
			path = FileUtils.getPropertyValue(Constants.CONFIG_FILE, Constants.PATH_PROPERTY_NAME);
			fName = FileUtils.getPropertyValue(Constants.CONFIG_FILE, Constants.FILENAME_PROPERTY_NAME);
		} catch (PropertiesFileRetrievalException e) {
			log.warn("Failed to retrieve the Path. Using system's temp directory");
			path = System.getProperty("java.io.tmpdir") + Constants.FILE_SEPARATOR + Constants.DIRECTORY_NAME;
			fName = Constants.LOGS_FILE_NAME;
		}
		log.debug("Path is -> " + path);
		new File(path).mkdirs();
		try {
			logFileName = path + Constants.FILE_SEPARATOR + fName;
			File file = new File(logFileName);
			file.createNewFile();
			FileWriter fstream = new FileWriter(file,true);
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		worker.start();
	}

	public static TaskExecutionLogger getLogger() {
		if (logger == null)
			logger = new TaskExecutionLogger();
		return logger;
	}

	public void logTask(String task) {
		log.debug("Offering message to queue....");
		log.debug(task);
		messageQueue.offer(task + Constants.LINE_SEPARATOR);
	}

	public List<String> getLogEntriesAsString() {
		try {
			File logFile = new File(logFileName);
			BufferedReader in = new BufferedReader(new FileReader(logFile));
			List<String> entries = new ArrayList<String>();
			String entry = in.readLine();
			while(entry != null){ 
				entries.add(entry); 
				entry = in.readLine();
			}
			in.close();
			return entries;
		} catch (Exception e) {
			log.debug("Failed to read the logs. An exception was thrown", e);

		}
		return null;
	}

	public List<WorkflowLogEntry> getLogEntriesAsObject() throws Exception {
		List<String> entries = getLogEntriesAsString();
		if (entries != null) {
			List<WorkflowLogEntry> workflowEntries = new ArrayList<WorkflowLogEntry>();
			for (String entry : entries) {
				workflowEntries.add((WorkflowLogEntry) JSONConverter.convertFromJSON(entry, WorkflowLogEntry.class));
			}
			return workflowEntries;
		}
		return null;
	}

	public boolean clearLogEntries() {
		File file = new File(logFileName);
		boolean isDeleted = file.delete();
		File file2 = new File(logFileName);
		if (isDeleted) {
			try {
				file2.createNewFile();
				FileWriter fstream = new FileWriter(file2,true);
				out = new BufferedWriter(fstream);
			} catch (IOException e) {
				log.debug("Failed to create the file", e);
			}
		}
		return isDeleted;

	}


	/**
	 * Inner class that extends thread. Responsible for handling the message queue and writing to the log file
	 * 
	 * @author Panagiota Koltsida, NKUA
	 *
	 */
	private class MessageHandlingThread extends Thread {
		public MessageHandlingThread() {
			super();
			log.debug("Creating a message handling object in order to handle the message queue");
		}

		/**
		 * Gets the appropriate file writer and writes the messages that are waiting in the queue
		 */
		public void run() {
			while(true) {
				try {
					String messageLine = messageQueue.take();
					out.write(messageLine);
					out.flush();
				} catch (Exception e) {
					log.error("An exception was thrown while trying to get the buffer writer.", e);
				}
			}	
		}
	}
}

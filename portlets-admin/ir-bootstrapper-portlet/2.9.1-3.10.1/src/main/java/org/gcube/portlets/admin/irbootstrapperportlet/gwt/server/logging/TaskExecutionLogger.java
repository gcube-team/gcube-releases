/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class TaskExecutionLogger {

	/** A list of log messages generated during the execution of this entity */
	private LinkedList<LogEntry> executionLog;
	
	/** The active session object */
	private ASLSession session;
	
	/** The log4j logger where messages are also written */
	private Logger logger;
	
	/**
	 * The "maximum" log level contained in this log
	 */
	private LogEntry.LogEntryLevel highestLogLevelContainedInLog;
	
	/**
	 * Class constructor
	 */
	public TaskExecutionLogger(Logger logger, ASLSession session) {
		this.executionLog = new LinkedList<LogEntry>();
		this.logger = logger;
		this.session = session;
		this.highestLogLevelContainedInLog = LogEntry.LogEntryLevel.getLowestLogLevel();
	}

	/**
	 * Adds an informational message to the execution log
	 * @param message
	 */
	synchronized public void info(String message) {
		executionLog.add(new LogEntry(message, LogEntry.LogEntryLevel.TYPE_INFORMATION));
		entryAdded(LogEntry.LogEntryLevel.TYPE_INFORMATION);
		logger.info(message);
	}

	/**
	 * Adds a warning message to the execution log
	 * @param message
	 */
	synchronized public void warn(String message) {
		this.warn(message, null);
	}

	/**
	 * Adds a warning message to the execution log
	 * @param message
	 * @param e
	 */
	synchronized public void warn(String message, Exception e) {
		executionLog.add(new LogEntry(message + ": " + e, LogEntry.LogEntryLevel.TYPE_WARNING));
		entryAdded(LogEntry.LogEntryLevel.TYPE_WARNING);
		if (e == null)
			logger.warn(message);
		else
			logger.warn(message, e);
	}
	
	/**
	 * Adds an error message to the execution log
	 * @param message
	 */
	synchronized public void error(String message) {
		this.error(message, null, false);
	}
	
	/**
	 * Adds an error message to the execution log
	 * @param message
	 * @param e
	 */
	synchronized public void error(String message, Exception e) {
		this.error(message, e, false);
	}

	/**
	 * Adds an error message to the execution log
	 * @param message
	 */
	synchronized public void error(String message, Exception e, boolean notifyAdmin) {
		executionLog.add(new LogEntry(message, LogEntry.LogEntryLevel.TYPE_ERROR));
		entryAdded(LogEntry.LogEntryLevel.TYPE_ERROR);
		if (e == null)
			logger.error(message);
		else
			logger.error(message, e);
		
		/* Notify the administrator via email, if requested */
		if (notifyAdmin) {
			 String username = this.session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
			// String userFullname = this.session.getAttribute("BootstrapperUserFullName").toString();
		    // String userEmail = this.session.getAttribute("BootstrapperUserEmail").toString(); 
		     String senderEmail = "no-reply@d4science.research-infrastructures.eu"; 
			 
			Properties props = System.getProperties();
	        String mailServiceHost = "localhost";	
	        props.put("mail.smtp.host", mailServiceHost);
	        String mailServicePort = "25";
	        props.put("mail.smtp.port", mailServicePort);
	        Session session = Session.getDefaultInstance(props, null);
	        session.setDebug(true);
	        Message mimeMessage = new MimeMessage(session);
	        
	        try {
	        	StringBuffer body = new StringBuffer();
		        body.append("User: " + username + "\n");
		        body.append("Time: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "\n");
		        body.append("Error message: " + message + "\n");
		        StringWriter sw = new StringWriter();
		        if (e != null) {
		        	e.printStackTrace(new PrintWriter(sw));
		        	body.append("Exception: " + sw.toString() + "\n");
		        }
		        else {
		        	Exception e1 = new Exception();
		        	e1.setStackTrace(Thread.currentThread().getStackTrace());
		        	e.printStackTrace(new PrintWriter(sw));
		        	body.append("Stack trace: " + sw.toString() + "\n");
		        }
		        
	            Address from = new InternetAddress(senderEmail);
	            mimeMessage.setFrom(from);
	            Address address = new InternetAddress(IRBootstrapperData.getInstance().getAdminEmail());
	            mimeMessage.addRecipient(Message.RecipientType.TO, address);
	            mimeMessage.setSubject("IRBootstrapper portlet error during task execution");
	            mimeMessage.setText(body.toString());
	            mimeMessage.setSentDate(new Date());
	            Transport.send(mimeMessage);
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	logger.error("Failed to send the email message.", ex);
	        }
		}
	}
		
	/**
	 * Invoked every time a new entry is added to the log
	 * @param entryType
	 */
	private void entryAdded(LogEntry.LogEntryLevel entryType) {
		logger.debug("checking the highest log level");
		logger.debug(highestLogLevelContainedInLog);
		if (entryType.compareTo(highestLogLevelContainedInLog) > 0) {
			highestLogLevelContainedInLog = entryType;
			logger.debug("Highest log level is updated to " + highestLogLevelContainedInLog);
		}
	}
	
	/**
	 * Returns the highest log type contained in this log
	 * @return
	 */
	public LogEntry.LogEntryLevel getHighestLogLevelContained() {
		return highestLogLevelContainedInLog;
	}
	
	/**
	 * Returns the list of entries contained in the log
	 * @return
	 */
	public LinkedList<LogEntry> getLogEntries() {
		return this.executionLog;
	}
	
	/**
	 * Returns the logger that is used in order to log messages on the server side
	 * @return
	 */
	public Logger getServerLogger() {
		return this.logger;
	}
}

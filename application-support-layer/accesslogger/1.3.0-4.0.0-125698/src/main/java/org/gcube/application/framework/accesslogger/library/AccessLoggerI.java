package org.gcube.application.framework.accesslogger.library;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

/**
 * Interface for the AccessLogger class
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public interface AccessLoggerI {
	public void logEntry(String username, String vre, AccessLogEntry entry); 
	
	
}

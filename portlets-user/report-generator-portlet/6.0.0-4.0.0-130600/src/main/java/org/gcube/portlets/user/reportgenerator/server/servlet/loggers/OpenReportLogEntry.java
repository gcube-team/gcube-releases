package org.gcube.portlets.user.reportgenerator.server.servlet.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;


/** 
 * Represents an access log entry for creating a new Template
 */
public class OpenReportLogEntry extends AccessLogEntry{
	
	private String templateName;
	
	private String templateId;

	public OpenReportLogEntry(String templateName, String templateId) {
		super("Open_Report");
		this.templateName = replaceReservedChars(templateName);
		this.templateId = replaceReservedChars(templateId);
	}
	
	@Override
	public String getLogMessage() {
		String message = "Name = " + templateName + "|ID = " + templateId;		
		return message;
	}

}
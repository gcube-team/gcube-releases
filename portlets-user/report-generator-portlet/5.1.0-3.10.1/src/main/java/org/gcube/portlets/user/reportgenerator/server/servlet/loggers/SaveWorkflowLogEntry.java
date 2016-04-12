package org.gcube.portlets.user.reportgenerator.server.servlet.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;


/** 
 * Represents an access log entry for creating a new Template
 */
public class SaveWorkflowLogEntry extends AccessLogEntry{
	
	private String templateName;
	private String author;
	private String templateId;

	public SaveWorkflowLogEntry(String templateName, String templateId, String author) {
		super("Save_Workflow_Report");
		this.author = author;
		this.templateName = replaceReservedChars(templateName);
		this.templateId = replaceReservedChars(templateId);
	}
	
	@Override
	public String getLogMessage() {
		String message = "Name = " + templateName + "|ID = " + templateId+ "|AUTHOR = " + author;			
		return message;
	}

}
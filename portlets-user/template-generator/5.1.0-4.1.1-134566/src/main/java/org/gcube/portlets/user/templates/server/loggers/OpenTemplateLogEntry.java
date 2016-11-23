package org.gcube.portlets.user.templates.server.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;


/** 
 * Represents an access log entry for creating a new Template
 */
public class OpenTemplateLogEntry extends AccessLogEntry{
	
	private String templateName;
	
	private String templateId;

	public OpenTemplateLogEntry(String templateName, String templateId) {
		super("Open_Template");
		this.templateName = replaceReservedChars(templateName);
		this.templateId = replaceReservedChars(templateId);
	}
	
	@Override
	public String getLogMessage() {
		String message = "Name = " + templateName + "|ID = " + templateId;		
		return message;
	}

}
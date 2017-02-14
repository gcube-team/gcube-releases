package org.gcube.portlets.user.reportgenerator.server.servlet.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

/** 
 * Represents an access log entry for creating a new Template
 */
public class GenerateReportLogEntry extends AccessLogEntry{
	
	private String name;
	
	private String mimetype;
	
	private String type;

	public GenerateReportLogEntry(String name, String mimetype, String type) {
		super("Generate_Report_Output");
		this.name = replaceReservedChars(name);
		this.mimetype = replaceReservedChars(mimetype);
	}
	
	@Override
	public String getLogMessage() {
		String message = "Name = " + name + "|MIMETYPE = " + mimetype+ "|TYPE = " + type;	
		return message;
	}

}
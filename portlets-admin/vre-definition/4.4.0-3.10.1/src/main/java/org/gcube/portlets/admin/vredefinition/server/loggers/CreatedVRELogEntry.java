package org.gcube.portlets.admin.vredefinition.server.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

/** 
 * Represents an access log entry for creating a new Template
 */
public class CreatedVRELogEntry extends AccessLogEntry{
	
	private String name;
	private String id;
	private String designer;
	private String manager;

	public CreatedVRELogEntry(String name, String id, String designer,  String manager) {
		super("Created_VRE");
		this.name = replaceReservedChars(name);
		this.id = replaceReservedChars(id);
		this.designer = replaceReservedChars(designer);
		this.manager = replaceReservedChars(manager);
		
	}
	
	@Override
	public String getLogMessage() {
		String message = "Name = " + name + "|ID = " + id + "|DESIGNER = " + designer+ "|MANAGER = " + manager;				
		return message;
	}

}
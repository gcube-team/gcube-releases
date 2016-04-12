package org.gcube.portlets.admin.vredefinition.server.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;


/** 
 * Represents an access log entry for OpenVREWizardLogEntry
 */
public class OpenVREWizardLogEntry extends AccessLogEntry{
	

	public OpenVREWizardLogEntry() {
		super("VRE Definition Open");
	}
	
	@Override
	public String getLogMessage() {
		String message = "VRE Definition Open";		
		return message;
	}

}
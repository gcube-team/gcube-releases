package org.gcube.portlets.user.statisticalmanager.server.accounting;

public class AccessLogEntry {
	protected String message;
	
	
	public AccessLogEntry(String message) {
		this.message=message;
	}

	public String getLogMessage() {
		return message;
	}

}

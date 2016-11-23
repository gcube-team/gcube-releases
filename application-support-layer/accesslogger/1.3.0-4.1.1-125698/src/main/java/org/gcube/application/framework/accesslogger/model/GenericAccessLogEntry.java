package org.gcube.application.framework.accesslogger.model;

/**
 * Represents a generic  access log entry
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class GenericAccessLogEntry extends AccessLogEntry {

	/*
	 * The message that will be logged
	 */
	private String message;
	
	/**
	 * Constructor
	 * 
	 * @param message The message that will be logged
	 */
	public GenericAccessLogEntry(String message) {
		super(EntryTypeConstants.GENERIC_ENTRY);
		this.message = replaceReservedChars(message);
	}
	
	/**
	 * @return The log message for a generic entry
	 */
	public String getLogMessage() {
		return message;
	}

}

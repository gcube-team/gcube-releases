package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for the first login to a VRE
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class LoginToVreAccessLogEntry extends AccessLogEntry {

	/*
	 * The message that will be logged for the first login to a vre is always the same
	 */
	private static final String message = "First login to the vre";
	
	/**
	 * Constructor
	 */
	public LoginToVreAccessLogEntry() {
		super(EntryTypeConstants.LOGIN_VRE_ENTRY);
	}

	/**
	 * @return The log message for the login to VRE entry
	 */
	public String getLogMessage() {
		return message;
	}

}

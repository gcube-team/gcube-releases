package org.gcube.application.framework.accesslogger.model;

/**
 * Abstract class representing a general entry for the logger
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public abstract class AccessLogEntry {

	protected String entryType;
	
	/**
	 * 
	 * @param entryType The type of entry
	 */
	protected AccessLogEntry(String entryType) {
		this.entryType = entryType;
	}
	
	/**
	 * 
	 * @return The type of Entry
	 */
	public String getType() {
		return this.entryType;
	}
	
	/**
	 * 
	 * @return The log message for the current entry
	 */
	public abstract String getLogMessage();
	
	public static String replaceReservedChars(String msgToReplace) {
		return msgToReplace.replaceAll(",", " ").replaceAll("->", " ").replaceAll("\\|", " ");
	}
}

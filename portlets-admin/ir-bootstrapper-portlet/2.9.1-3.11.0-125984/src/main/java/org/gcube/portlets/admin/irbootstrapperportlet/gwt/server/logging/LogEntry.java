/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class LogEntry {
	
	public enum LogEntryLevel { 
		TYPE_INFORMATION, 
		TYPE_WARNING,
		TYPE_ERROR;
		
		static LogEntryLevel getLowestLogLevel() { return TYPE_INFORMATION; }
	};
	
	
	private String message;
	private LogEntryLevel type;
	
	/**
	 * Constructs a new {@link LogEntry}.
	 * @param message the message that this entry will contain
	 * @param type the message type
	 */
	public LogEntry(String message, LogEntryLevel type) {
		this.message = message;
		this.type = type;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public LogEntryLevel getLevel() {
		return this.type;
	}
}

package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for the quick search
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class QuickSearchAccessLogEntry extends AccessLogEntry {

	/*
	 * The term that was used in search
	 */
	private String quickTerm;
	
	/**
	 * Constructor
	 * 
	 * @param term The term that was used in search
	 */
	public QuickSearchAccessLogEntry(String term) {
		super(EntryTypeConstants.QUICK_SEARCH_ENTRY);
		this.quickTerm = replaceReservedChars(term);
	}

	/**
	 * @return The log message for a Quick search entry
	 */
	public String getLogMessage() {
		String message = "";
		if (this.quickTerm != null)
			message += TemplateConstants.TERM + TemplateConstants.eqChar + this.quickTerm;
		return message;
	}
	
}

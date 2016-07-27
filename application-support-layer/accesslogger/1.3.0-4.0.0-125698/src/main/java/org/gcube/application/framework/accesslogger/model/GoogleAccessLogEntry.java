package org.gcube.application.framework.accesslogger.model;

public class GoogleAccessLogEntry extends AccessLogEntry{

	private String googleTerm;
	
	public GoogleAccessLogEntry(String term) {
		super(EntryTypeConstants.GOOGLE_SEARCH_ENTRY);
		this.googleTerm = replaceReservedChars(term);
	}

	@Override
	public String getLogMessage() {
		String message = "";
		if (this.googleTerm != null)
			message += TemplateConstants.TERM + TemplateConstants.eqChar + this.googleTerm;
		return message;
	}

}

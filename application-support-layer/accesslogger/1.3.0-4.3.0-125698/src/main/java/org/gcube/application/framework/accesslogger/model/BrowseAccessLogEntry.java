package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for the Browse of a collection
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class BrowseAccessLogEntry extends AccessLogEntry {

	/*
	 * If the performed browse was on distinct values or no
	 */
	private boolean isDistinct;
	
	/*
	 * An array which holds the name and the id of each collection that was used in the browse
	 */
	private String collections[][];
	
	/*
	 * The browse by criterion
	 */
	private String browseCriterion;
	
	
	/**
	 * Constructor
	 * 
	 * @param collectionInfo The collection's name and id that was browsed
	 * @param isDistinct Browse for distinct values or no
	 */
	public BrowseAccessLogEntry(String collectionInfo[][],String browseCriterion, boolean isDistinct) {
		super(EntryTypeConstants.BROWSE_COLLECTION_ENTRY);
		this.isDistinct = isDistinct;
		this.collections = collectionInfo;
		this.browseCriterion = browseCriterion;
	}

	/**
	 * @return The log message for a Browse search entry
	 */
	public String getLogMessage() {
		String message = "";
		message += TemplateConstants.DISTINCT + TemplateConstants.eqChar;
		if (this.isDistinct)
			message += "TRUE";
		else
			message += "FALSE";
		message += TemplateConstants.separateCharacters;
		if (this.collections != null) {
			for (int i=0; i<this.collections.length; i++) {
				// The message for a search entry that is performed on selected collections it will contain the name and id of each collection
				message += TemplateConstants.COLLECTION_NAME + TemplateConstants.eqChar + replaceReservedChars(this.collections[i][0]) + TemplateConstants.andchar + TemplateConstants.COLLECTION_ID + TemplateConstants.eqChar + replaceReservedChars(this.collections[i][1]) + TemplateConstants.separateCharacters;
			}
			if (browseCriterion != null) {
				message += TemplateConstants.BROWSE_TERM + TemplateConstants.eqChar + replaceReservedChars(this.browseCriterion) + TemplateConstants.separateCharacters;
			}
			// remove the last separate char
			message = message.substring(0, message.length() - TemplateConstants.separateCharacters.length());
		}
		return message;
	}
		

}

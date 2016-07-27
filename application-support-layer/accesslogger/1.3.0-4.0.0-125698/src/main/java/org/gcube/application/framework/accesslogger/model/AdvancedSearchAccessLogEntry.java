package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for the Advanced Search
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class AdvancedSearchAccessLogEntry extends AccessLogEntry {

	/*
	 * An array which holds the name and the id of each collection that was used in the advanced search
	 */
	private String collections[][];
	
	/*
	 * An array which holds the term and the value that was used in the advanced search
	 */
	private String termsAndValues[][];
	
	private String operator;
	
	/**
	 * Constructor
	 * 
	 * @param collections The collections that are used in the search
	 * @param termsAndValues The terms and values that are used in the search
	 * @param operator AND or OR depending on the operator that is used in search
	 */
	public AdvancedSearchAccessLogEntry(String[][] collections, String[][] termsAndValues, String operator) {
		super(EntryTypeConstants.ADVANCED_SEARCH_ENTRY);
		this.collections = collections;
		this.termsAndValues = termsAndValues;
		this.operator = operator;
	}

	/**
	 * @return The log message for an Advanced search entry
	 */
	public String getLogMessage() {
		String message = "";
		if (this.collections != null) {
			for (int i=0; i<collections.length; i++) {
				// The message for a search entry that is performed on selected collections it will contain the name and id of each collection
				message += TemplateConstants.COLLECTION_NAME + TemplateConstants.eqChar + replaceReservedChars(collections[i][0]) + TemplateConstants.andchar + TemplateConstants.COLLECTION_ID + TemplateConstants.eqChar + replaceReservedChars(collections[i][1]) + TemplateConstants.separateCharacters;
			}
			if (termsAndValues != null) {
				for (int j=0; j<termsAndValues.length; j++) {
					message += TemplateConstants.TERM + TemplateConstants.eqChar + replaceReservedChars(termsAndValues[j][0]) + TemplateConstants.andchar + TemplateConstants.VALUE + TemplateConstants.eqChar + replaceReservedChars(termsAndValues[j][1]) + TemplateConstants.separateCharacters;
				}
			}
			message += TemplateConstants.OPERATOR + TemplateConstants.eqChar + this.operator;
		}
		return message;
	}

}

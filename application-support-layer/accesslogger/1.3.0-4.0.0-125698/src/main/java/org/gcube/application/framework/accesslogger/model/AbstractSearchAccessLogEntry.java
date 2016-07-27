package org.gcube.application.framework.accesslogger.model;

public class AbstractSearchAccessLogEntry extends AccessLogEntry {
	
	/*
	 * An array which holds the name and the id of each collection that was used in the advanced search
	 */
	private String collections[][];
	
	/*
	 * An array which holds the term and the value that was used in the advanced search
	 */
	private String termsAndValues[][];
	
	/*
	 * The used operator when multiple criteria are used
	 */
	private String operator;
	
	/*
	 * The language used
	 */
	private String language;
	
	public AbstractSearchAccessLogEntry(String[][] collections, String[][] termsAndValues, String operator, String language) {
		super(EntryTypeConstants.ABSTRACT_SEARCH_ENTRY);
		this.collections = collections;
		this.termsAndValues = termsAndValues;
		this.operator = operator;
		this.language = language;
	}

	@Override
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
			message += TemplateConstants.OPERATOR + TemplateConstants.eqChar + this.operator + TemplateConstants.separateCharacters;
			message += TemplateConstants.LANGUAGE + TemplateConstants.eqChar + this.language;
		}
		return message;
	}

}

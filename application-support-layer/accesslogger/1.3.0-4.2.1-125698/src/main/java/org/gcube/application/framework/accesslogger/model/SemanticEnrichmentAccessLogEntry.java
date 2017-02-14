package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for the Semantic Enrichment
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SemanticEnrichmentAccessLogEntry extends AccessLogEntry {
	
	/*
	 * An array which holds the name and the id of each collection that was used in the advanced search
	 */
	private String collections[][];
	
	/**
	 * Constructor
	 * 
	 * @param collections The collections that are used in the search
	 * @param simpleTerm The term that is used
	 */
	public SemanticEnrichmentAccessLogEntry(String collections[][], String simpleTerm) {
		super(EntryTypeConstants.SEMANTIC_ENRICHMENT_ENTRY);
		this.collections = collections;
	}

	/**
	 * @return The log message for a Simple search entry
	 */
	public String getLogMessage() {
		String message = "";
		if (this.collections != null) {
			for (int i=0; i<this.collections.length; i++) {
				// The message for a search entry that is performed on selected collections it will contain the name and id of each collection
				message += TemplateConstants.COLLECTION_NAME + TemplateConstants.eqChar + replaceReservedChars(this.collections[i][0]) + TemplateConstants.andchar + TemplateConstants.COLLECTION_ID + TemplateConstants.eqChar + replaceReservedChars(this.collections[i][1]) + TemplateConstants.separateCharacters;
			}
			// remove the last separate char
			message = message.substring(0, message.length() - TemplateConstants.separateCharacters.length());
		}
		return message;
	}

}

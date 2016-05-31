package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for the Retrieval of a content object
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class ContentRetrievalLogEntry extends AccessLogEntry {

	private String objectID;
	
	private String objectName;
	
	/**
	 * Constructor 
	 * 
	 * @param objectID The ID of the object that is retrieved
	 * @param objectName The name of the object that is retrieved
	 */
	public ContentRetrievalLogEntry(String objectID, String objectName) {
		super(EntryTypeConstants.RETRIEVE_CONTENT_ENTRY);
		this.objectID = replaceReservedChars(objectID);
		// insert the name without containing any of ',' and '|'
		this.objectName = replaceReservedChars(objectName);//objectName.replaceAll(",", " ").replaceAll("->", " ").replaceAll("\\|", " ");
	}

	/**
	 * @return The log message for a Content retrieval entry
	 */
	public String getLogMessage() {
		String message = "";
		message += TemplateConstants.CONTENT_ID + TemplateConstants.eqChar + 
			this.objectID + TemplateConstants.separateCharacters + TemplateConstants.CONTENT_NAME +  
			TemplateConstants.eqChar + this.objectName;
		
		return message;
	}

}

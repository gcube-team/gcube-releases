package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for deleting an annotation
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class DeleteAnnotationLogEntry extends AccessLogEntry {
	
	private String annotationName;
	
	private String annotatedObjectID;

	public DeleteAnnotationLogEntry(String annotationName, String annotatedObjectID) {
		super(EntryTypeConstants.DELETE_ANNOTATIONS_ENTRY);
		this.annotationName = replaceReservedChars(annotationName);
		this.annotatedObjectID = replaceReservedChars(annotatedObjectID);
	}

	@Override
	public String getLogMessage() {
		String message = "";
		message += TemplateConstants.ANNOTATION_NAME + TemplateConstants.eqChar + this.annotationName + TemplateConstants.separateCharacters + 
		TemplateConstants.OBJECT_ID + TemplateConstants.eqChar + this.annotatedObjectID;
		
		return message;
	}

}

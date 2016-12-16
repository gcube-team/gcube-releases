package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for editing an annotation
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class EditAnnotationLogEntry extends AccessLogEntry{
	
	private String annotationType;
	
	private String annotationName;
	
	private String annotatedObjectID;

	public EditAnnotationLogEntry(String annotationType, String annotationName, String annotatedObjectID) {
		super(EntryTypeConstants.EDIT_ANNOTATIONS_ENTRY);
		this.annotationName = replaceReservedChars(annotationName);
		this.annotationType = replaceReservedChars(annotationType);
		this.annotatedObjectID = replaceReservedChars(annotatedObjectID);
	}

	@Override
	public String getLogMessage() {
		String message = "";
		message += TemplateConstants.ANNOTATION_TYPE + TemplateConstants.eqChar + this.annotationType + TemplateConstants.separateCharacters + TemplateConstants.ANNOTATION_NAME
		+ TemplateConstants.eqChar + this.annotationName + TemplateConstants.separateCharacters + TemplateConstants.OBJECT_ID + TemplateConstants.eqChar + this.annotatedObjectID;
		
		return message;
	}

}

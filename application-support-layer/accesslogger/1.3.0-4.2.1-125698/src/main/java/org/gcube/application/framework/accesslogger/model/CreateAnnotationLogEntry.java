package org.gcube.application.framework.accesslogger.model;

/**
 * Represents an access log entry for creating a new annotation
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class CreateAnnotationLogEntry extends AccessLogEntry{
	
	private String annotationType;
	
	private String annotatedObjectID;
	
	private String annotatedObjectName;
	
	private String annotationName;

	public CreateAnnotationLogEntry(String annotationType, String annotationName, String annotatedObjectID, String annotatedObjectName) {
		super(EntryTypeConstants.CREATE_ANNOTATIONS_ENTRY);
		this.annotationType = annotationType;
		this.annotationName = replaceReservedChars(annotationName);
		this.annotatedObjectID = replaceReservedChars(annotatedObjectID);
		this.annotatedObjectName = replaceReservedChars(annotatedObjectName);
	}
	
	@Override
	public String getLogMessage() {
		String message = "";
		message += TemplateConstants.ANNOTATION_TYPE + TemplateConstants.eqChar + this.annotationType + TemplateConstants.separateCharacters + TemplateConstants.ANNOTATION_NAME
		+ TemplateConstants.eqChar + this.annotationName + TemplateConstants.separateCharacters + TemplateConstants.OBJECT_ID + TemplateConstants.eqChar + this.annotatedObjectID;
		
		message += TemplateConstants.separateCharacters + TemplateConstants.OBJECT_NAME + TemplateConstants.eqChar + this.annotatedObjectName;
		
		return message;
	}

}

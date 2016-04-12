package org.gcube.application.framework.contentmanagement.exceptions;

public class OCRException extends Exception{
	
	public OCRException(Throwable cause) {
		super("Error while performing OCR", cause);
	}

}

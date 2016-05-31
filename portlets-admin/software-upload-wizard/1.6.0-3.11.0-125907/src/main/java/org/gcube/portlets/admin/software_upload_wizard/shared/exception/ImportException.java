package org.gcube.portlets.admin.software_upload_wizard.shared.exception;

public class ImportException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7348913366535052419L;
	
	public ImportException() {
		super("Import exception");
	}
	
	public ImportException(String message) {
		super(message);
	}
	
}

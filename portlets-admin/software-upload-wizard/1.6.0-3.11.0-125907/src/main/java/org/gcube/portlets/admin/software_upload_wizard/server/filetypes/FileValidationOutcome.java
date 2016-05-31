package org.gcube.portlets.admin.software_upload_wizard.server.filetypes;

public class FileValidationOutcome {
	
	private boolean valid;
	private String details;
	
	public FileValidationOutcome(boolean valid, String details) {
		super();
		this.valid = valid;
		this.details = details;
	}

	public boolean isValid() {
		return valid;
	}

	public String getDetails() {
		return details;
	}

}

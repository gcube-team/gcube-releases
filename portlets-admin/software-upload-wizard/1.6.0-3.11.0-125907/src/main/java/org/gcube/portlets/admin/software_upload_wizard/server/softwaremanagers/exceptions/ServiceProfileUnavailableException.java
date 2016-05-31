package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.exceptions;

public class ServiceProfileUnavailableException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3791733489922379416L;

	public ServiceProfileUnavailableException() {
		super("Service Profile is unavailable for the software type");
	}
}

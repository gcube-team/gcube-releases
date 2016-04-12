package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.exceptions;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

public class SoftwareTypeNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1845609192146103371L;

	public SoftwareTypeNotFoundException(SoftwareTypeCode code) {
		super("There is no available software type with the specified code: " + code);
	}
}

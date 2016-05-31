package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers;

import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;

public interface ISoftwareSubmissionManager {
	
	public IOperationProgress submitSoftware() throws Exception;

}

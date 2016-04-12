package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers;

import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;

public interface ISoftwareSubmissionTask extends Runnable {

	public IOperationProgress getOperationProgress();

	public void setTargetRepository(IMavenRepositoryInfo targetRepository);
	
}

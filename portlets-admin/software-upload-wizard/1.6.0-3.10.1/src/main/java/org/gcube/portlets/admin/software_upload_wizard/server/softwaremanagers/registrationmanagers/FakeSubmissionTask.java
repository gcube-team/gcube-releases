package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers;

import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationState;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeSubmissionTask implements ISoftwareSubmissionTask {

	private static final Logger log = LoggerFactory.getLogger(FakeSubmissionTask.class);

	private IOperationProgress operationProgress = new OperationProgress();
	private IMavenRepositoryInfo targetRepository;

	@Override
	public void run() {

		try {
			operationProgress.setProgress(100, 0);
			operationProgress.setDetails("Initializing software registration");
			long currentProgress;
			operationProgress.setDetails("Working...");
			while ((currentProgress = operationProgress.getElaboratedLenght()) < 100) {
				long newProgress = currentProgress + 20;
				operationProgress.setProgress(100, newProgress);
				log.trace("New progress: " + newProgress + "/100");
				Thread.sleep(2000);
			}
			log.debug("Software submission completed");

			operationProgress.setState(OperationState.COMPLETED);
			operationProgress.setDetails("Software registered successfully.");
		} catch (Exception e) {
			log.error("An error occurred while executing fake software submission", e);
			operationProgress.setState(OperationState.FAILED);
			operationProgress.setDetails("An error occurred during software submission. Check server logs.");
		}
	}

	@Override
	public IOperationProgress getOperationProgress() {
		return operationProgress;
	}

	@Override
	public void setTargetRepository(IMavenRepositoryInfo targetRepository) {
		this.targetRepository = targetRepository;
	}

}

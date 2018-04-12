package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectInfoMessenger {
	private static Logger logger = LoggerFactory.getLogger(ProjectInfoMessenger.class);

	private ProjectMessenger projectMessenger = null;
	private WorkflowMessenger workflowMessenger = null;

	public ProjectInfoMessenger() {
		logger.trace("Initialized default contructor for ProjectInfoMessenger");

	}

	public ProjectInfoMessenger(ProjectMessenger project, WorkflowMessenger workflow) {
		logger.trace("Initializing ProjectInfoMessenger...");

		this.projectMessenger = project;
		this.workflowMessenger = workflow;
		logger.trace("Initialized ProjectInfoMessenger");

	}

	public ProjectMessenger getProjectMessenger() {
		return projectMessenger;
	}

	public void setProjectMessenger(ProjectMessenger project) {
		this.projectMessenger = project;
	}

	public WorkflowMessenger getWorkflowMessenger() {
		return workflowMessenger;
	}

	public void setWorkflowMessenger(WorkflowMessenger workflow) {
		this.workflowMessenger = workflow;
	}
}

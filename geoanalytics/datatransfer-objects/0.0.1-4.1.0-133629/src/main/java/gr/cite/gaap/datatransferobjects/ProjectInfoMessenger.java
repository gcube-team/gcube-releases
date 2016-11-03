package gr.cite.gaap.datatransferobjects;

public class ProjectInfoMessenger {
	private ProjectMessenger projectMessenger = null;
	private WorkflowMessenger workflowMessenger = null;

	public ProjectInfoMessenger() {
	}

	public ProjectInfoMessenger(ProjectMessenger project, WorkflowMessenger workflow) {
		this.projectMessenger = project;
		this.workflowMessenger = workflow;
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

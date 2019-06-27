package gr.cite.geoanalytics.dataaccess.entities.workflow.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.Criticality;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.WorkflowTaskStatus;

public interface WorkflowDao extends Dao<Workflow, UUID>
{
	public List<WorkflowTask> getWorkflowTasks(Workflow w);
	public List<WorkflowTask> getWorkflowTasks(Workflow w, Criticality c);
	public List<WorkflowTask> getWorkflowTasks(Workflow w, Criticality c, WorkflowTaskStatus s);
	public List<Workflow> getByProject(Project p);
}

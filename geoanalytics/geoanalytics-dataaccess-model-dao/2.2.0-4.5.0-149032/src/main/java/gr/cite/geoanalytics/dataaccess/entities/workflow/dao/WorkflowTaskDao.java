package gr.cite.geoanalytics.dataaccess.entities.workflow.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;

public interface WorkflowTaskDao extends Dao<WorkflowTask, UUID>
{
	public long countDocuments(WorkflowTask wt);
	public List<Document> getDocuments(WorkflowTask wt);
}

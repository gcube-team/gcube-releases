package gr.cite.geoanalytics.dataaccess.entities.workflow.dao;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocument;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocumentPK;

public interface WorkflowTaskDocumentDao extends Dao<WorkflowTaskDocument, WorkflowTaskDocumentPK>
{
	public WorkflowTaskDocument find(WorkflowTask wt, Document d);
	public void deleteByWorkflowTask(WorkflowTask wt);
	public void deleteByDocument(Document d);	
}

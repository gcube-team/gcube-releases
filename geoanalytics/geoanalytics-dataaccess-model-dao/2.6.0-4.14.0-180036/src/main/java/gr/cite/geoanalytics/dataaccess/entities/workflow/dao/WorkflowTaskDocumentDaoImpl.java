package gr.cite.geoanalytics.dataaccess.entities.workflow.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocument;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocumentPK;

@Repository
public class WorkflowTaskDocumentDaoImpl extends JpaDao<WorkflowTaskDocument, WorkflowTaskDocumentPK> implements WorkflowTaskDocumentDao {
	
	@Override
	public WorkflowTaskDocument find(WorkflowTask wt, Document d) {
		Query query = entityManager.createQuery("from WorkflowTaskDocument wtd where wtd.workflowTask = :wt and wtd.document = :d", 
				WorkflowTaskDocument.class);
		query.setParameter("wt", wt);
		query.setParameter("d", d);
		
		try {
			return (WorkflowTaskDocument)query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	
	@Override
	public void deleteByWorkflowTask(WorkflowTask wt) {
		Query query = entityManager.createQuery("delete WorkflowTaskDocument wtd where wtd.workflowTask = :wt");
		query.setParameter("wt", wt);
		query.executeUpdate();
	}
	
	@Override
	public void deleteByDocument(Document d) {
		Query query = entityManager.createQuery("delete WorkflowTaskDocument wtd where wtd.document = :d");
		query.setParameter("d", d);
		query.executeUpdate();
	}

	@Override
	public WorkflowTaskDocument loadDetails(WorkflowTaskDocument wtd) {
		wtd.getCreator().getName();
		wtd.getDocument().getId();
		wtd.getWorkflowTask().getId();
		return wtd;
	}
}

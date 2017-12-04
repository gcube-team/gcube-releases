package gr.cite.geoanalytics.dataaccess.entities.workflow.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;

@Repository
public class WorkflowTaskDaoImpl extends JpaDao<WorkflowTask, UUID> implements WorkflowTaskDao
{

	@Override
	public long countDocuments(WorkflowTask wt) {
		TypedQuery<Long> query = entityManager.createQuery(
				"select count(wtd) from WorkflowTaskDocument wtd where wtd.workflowTask = :wt",
				Long.class);
		query.setParameter("wt", wt);
		
		return query.getSingleResult();
	}

	@Override
	public List<Document> getDocuments(WorkflowTask wt) {
		TypedQuery<Document> query = entityManager.createQuery(
				"select wtd.document from WorkflowTaskDocument wtd where wtd.workflowTask = :wt",
				Document.class);
		query.setParameter("wt", wt);
	
		return query.getResultList();
	}

	@Override
	public WorkflowTask loadDetails(WorkflowTask wt) {
		wt.getCreator().getName();
		if(wt.getPrincipal() != null)
			wt.getPrincipal().getName();
		wt.getWorkflow().getId();
		return wt;
	}

}

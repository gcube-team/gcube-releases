package gr.cite.geoanalytics.dataaccess.entities.workflow.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.Criticality;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.WorkflowTaskStatus;

@Repository
public class WorkflowDaoImpl extends JpaDao<Workflow, UUID> implements WorkflowDao {

	@Override
	public List<Workflow> getByProject(Project p) {
		TypedQuery<Workflow> query = entityManager.createQuery("from Workflow w where w.project = :p", Workflow.class);
		query.setParameter("p", p);
		
		return query.getResultList();
	}
	
	@Override
	public List<WorkflowTask> getWorkflowTasks(Workflow w) {
		TypedQuery<WorkflowTask> query = entityManager.createQuery("from WorkflowTask wt where wt.workflow = :w", WorkflowTask.class);
		query.setParameter("w", w);
	
		return query.getResultList();
	}
	
	@Override
	public List<WorkflowTask> getWorkflowTasks(Workflow w, Criticality c) {
		TypedQuery<WorkflowTask> query = 
				entityManager.createQuery("from WorkflowTask wt where wt.workflow = :w and wt.critical = :c", WorkflowTask.class);
		query.setParameter("w", w);
		query.setParameter("c", c.criticalityCode());
	
		return query.getResultList();
	}
	
	@Override
	public List<WorkflowTask> getWorkflowTasks(Workflow w, Criticality c, WorkflowTaskStatus s) {
		TypedQuery<WorkflowTask> query = 
				entityManager.createQuery("from WorkflowTask wt where wt.workflow = :w and wt.critical = :c and wt.status = :s", WorkflowTask.class);
		query.setParameter("w", w);
		query.setParameter("c", c.criticalityCode());
		query.setParameter("s", s.statusCode());
	
		return query.getResultList();
	}

	@Override
	public Workflow loadDetails(Workflow w) {
		w.getCreator().getName();
		w.getProject().getId();
		if(w.getTemplate() != null)
			w.getTemplate().getId();
		return w;
	}
}

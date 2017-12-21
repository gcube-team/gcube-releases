package gr.cite.geoanalytics.dataaccess.entities.document.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeShape;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;

@Repository
public class DocumentDaoImpl extends JpaDao<Document, UUID> implements DocumentDao {
	private static Logger log = LoggerFactory.getLogger(DocumentDaoImpl.class);

	@Override
	public long totalSize() {
		TypedQuery<Long> query = entityManager.createQuery("select sum(d.size) from Document d", Long.class);
		
		return query.getSingleResult();
	}
	
	@Override
	public List<Document> findByCreatorAndCustomer(Principal principal, Tenant tenant) {
		TypedQuery<Document> query = entityManager.createQuery(
				"from Document d where d.creator = :principal and d.tenant = :tenant", 
				Document.class);
		query.setParameter("principal", principal);
		query.setParameter("tenant", tenant);
		
		return query.getResultList();
	}
	
	@Override
	public List<Document> findByCreator(Principal principal) {
		TypedQuery<Document> query = entityManager.createQuery("from Document d where d.creator = :principal", Document.class);
		query.setParameter("principal", principal);
		
		return query.getResultList();
	}
	
	@Override
	public List<Document> findByCustomer(Tenant tenant) {
		TypedQuery<Document> query = entityManager.createQuery("from Document d where d.tenant = :tenant", Document.class);
		query.setParameter("tenant", tenant);
		
		return query.getResultList();
	}

	@Override
	public List<UUID> listDocuments()
	{
		TypedQuery<UUID> query = entityManager.createQuery("select d.id from Document d", UUID.class);
		
		return query.getResultList();
	}

	@Override
	public List<Document> getDocumentsOfProject(Project project)
	{
		TypedQuery<Document> query = entityManager.createQuery(
				"select pd.document from ProjectDocument pd where pd.project = :p", 
				Document.class);
		query.setParameter("p", project);
		
		return query.getResultList();
	}

	@Override
	public List<Document> getDocumentsOfShape(Shape shape)
	{
		TypedQuery<Document> query = entityManager.createQuery(
				"select sd.document from ShapeDocument sd, GeocodeShape tts where sd.geocodeShape = tts and tts.shape = :s", 
				Document.class);
		query.setParameter("s", shape);
		
		return query.getResultList();
	}
	
	@Override
	public Document getDocumentOfShape(Shape s, GeocodeSystem t)
	{
		TypedQuery<Document> query = entityManager.createQuery(
				"select sd.document from ShapeDocument sd, GeocodeShape tts, Geocode tt "
				+ "where sd.geocodeShape = tts and tts.geocode = tt and tt.taxonomy = :t and tts.geocodeShape = :tts", 
				Document.class);
		query.setParameter("s", s);
		query.setParameter("t", t);
		
		return query.getSingleResult();
	}
	
//	@Override
//	public Document getDocumentOfShape(Shape s, GeocodeShape tts)
//	{
//		TypedQuery<Document> query = entityManager.createQuery(
//				"select sd.document from ShapeDocument sd, where d Geocode tt "
//				+ "where sd.geocodeShape = :tts", 
//				Document.class);
//		query.setParameter("tts", tts);
//		
//		return query.getSingleResult();
//	}
	
	@Override
	public List<Document> getDocumentsOfWorkflowTask(WorkflowTask wt)
	{
		TypedQuery<Document> query = entityManager.createQuery(
				"select wftd.document from WorkflowTaskDocument wftd where wftd.workflowTask = :wt", 
				Document.class);
		query.setParameter("wt", wt);
		
		return query.getResultList();
	}
	
	@Override
	public Project findProjectOfDocument(Document d) throws Exception
	{
		TypedQuery<Project> query = entityManager.createQuery(
				"select pd.project from ProjectDocument pd where pd.document = :d",
				Project.class);
		query.setParameter("d", d);

		List<Project> res = query.getResultList();
		if (res.size() > 1)
			throw new Exception("More than one projects found for document "
					+ d.getId()); // superfluous check
		if (res.isEmpty())
			return null;
		return res.get(0);
	}
	
//	@Override
//	public List<GeocodeShape> findShapesOfDocument(Document d) throws Exception
//	{
//		TypedQuery<GeocodeShape> query = entityManager.createQuery(
//				"select sd.geocodeShape from ShapeDocument sd where sd.document = :d",
//				GeocodeShape.class);
//		query.setParameter("d", d);
//
//		return query.getResultList();
//	}
//	
//	@Override
//	public GeocodeShape findShapeOfDocument(Document d, GeocodeSystem t) throws Exception
//	{
//		TypedQuery<GeocodeShape> query = entityManager.createQuery(
//				"select sd.geocodeShape "+
//				"from ShapeDocument sd, Geocode tt, GeocodeShape tts, GeocodeSystem t  "+
//				"where tt.geocodeSystem = :t and tts.geocode = tt and sd.geocodeShape = tts and sd.document = :d",
//				GeocodeShape.class);
//		query.setParameter("d", d);
//		query.setParameter("t", t);
//
//		List<GeocodeShape> res = query.getResultList();
//		if (res.size() > 1)
//			throw new Exception("More than one tt-shapes found for document "
//					+ d.getId() + " and taxonomy " + t.getId());
//		if (res.isEmpty())
//			return null;
//		return res.get(0);
//	}
	
	@Override
	public List<WorkflowTask> findWorkflowTasksOfDocument(Project p, Document d) throws Exception
	{
		TypedQuery<WorkflowTask> query = entityManager.createQuery(
				"select wftd.workflowTask from WorkflowTaskDocument wftd, WorkflowTask wft, Workflow w, Project p " +
				"where wftd.document = :d and wft.workflow=w and w.project=:p",
				WorkflowTask.class);
		query.setParameter("d", d);
		query.setParameter("p", p);

		List<WorkflowTask> res = query.getResultList();
		List<WorkflowTask> fil = new ArrayList<WorkflowTask>();
		Set<UUID> ids = new HashSet<UUID>();
		for(WorkflowTask r : res)
		{
			if(!ids.contains(r.getId()))
			{
				fil.add(r);
				ids.add(r.getId());
			}
		}
		return fil;
	}
	
	@Override
	public long countWorkflowTasksOfDocument(Project p, Document d) throws Exception
	{
		TypedQuery<Long> query = entityManager.createQuery(
				"select count(distinct wftd.workflowTask) from WorkflowTaskDocument wftd, WorkflowTask wft, Workflow w, Project p " +
		        "where wftd.document = :d and wft.workflow=w and w.project=:p",
				Long.class);
		query.setParameter("d", d);
		query.setParameter("p", p);

		return query.getSingleResult();
	}

	@Override
	public List<Document> searchDocuments(List<String> terms)
	{
		List<Document> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("from Document d");

		if(!terms.isEmpty()) queryB.append(" where ");
		int j = 0;
		for(int i=0; i<terms.size(); i++)
		{
			queryB.append("lower(d.name) like :term" + (j++));
			queryB.append(" or lower(d.description) like :term" + (j++));
			if(i < terms.size()-1)
				queryB.append(" or ");
		}
		TypedQuery<Document> query = entityManager.createQuery(queryB.toString(), Document.class);
		j = 0;
		for(int i=0; i<terms.size(); i++)
		{
			String lower = terms.get(i).toLowerCase();
			query.setParameter("term"+(j++), "%"+lower+"%");
			query.setParameter("term"+(j++), "%"+lower+"%");
		}

		result = query.getResultList();
		
		log.debug("Documents by name/description pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Document d : (List<Document>) result) 
				log.debug("Document (" + d.getId() + ")");
		}
		
		return result;
	}

	@Override
	public List<Document> searchDocumentsOfProject(List<String> terms, Project project)
	{
		List<Document> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("from Document d, ProjectDocument pd");

		if(!terms.isEmpty()) queryB.append(" where ");
		queryB.append("pd.project = : p and pd.document=d and(");
		int j = 0;
		for(int i=0; i<terms.size(); i++)
		{
			queryB.append("lower(d.name) like :term" + j);
			j++;
			queryB.append(" or lower(d.description) like :term" + j);
			j++;
			if(i < terms.size()-1)
				queryB.append(" or ");
		}
		queryB.append(")");
		TypedQuery<Document> query = entityManager.createQuery(queryB.toString(), Document.class);
		query.setParameter("p", project);
		j = 0;
		for(int i=0; i<terms.size(); i++)
		{
			String lower = terms.get(i).toLowerCase();
			query.setParameter("term"+(j++), "%"+lower+"%");
			query.setParameter("term"+(j++), "%"+lower+"%");
		}

		result = query.getResultList();
		
		log.debug("Documents of project " + project.getId() + " by name/description pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Document d : (List<Document>) result) 
				log.debug("Document (" + d.getId() + ")");
		}
		
		return result;
	}
	
	@Override
	public List<Document> searchDocumentsOfCreator(List<String> terms, Principal creator) {
		List<Document> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("from Document d");

		if(!terms.isEmpty()) queryB.append(" where ");
		queryB.append("pd.creator = :creator and(");
		int j = 0;
		for(int i=0; i<terms.size(); i++) {
			queryB.append("lower(d.name) like :term" + j);
			j++;
			queryB.append(" or lower(d.description) like :term" + j);
			j++;
			if(i < terms.size()-1)
				queryB.append(" or ");
		}
		queryB.append(")");
		TypedQuery<Document> query = entityManager.createQuery(queryB.toString(), Document.class);
		query.setParameter("creator", creator);
		j = 0;
		for(int i=0; i<terms.size(); i++) {
			String lower = terms.get(i).toLowerCase();
			query.setParameter("term"+(j++), "%"+lower+"%");
			query.setParameter("term"+(j++), "%"+lower+"%");
		}

		result = query.getResultList();
		
		log.debug("Documents of principals " + creator.getId() + " by name/description pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Document d : (List<Document>) result) 
				log.debug("Document (" + d.getId() + ")");
		}
		
		return result;
	}

	@Override
	public Document loadDetails(Document doc) {
		doc.getCreator().getName();
		if(doc.getTenant() != null)
			doc.getTenant().getId();
		return doc;
	}

}

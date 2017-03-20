package gr.cite.geoanalytics.dataaccess.entities.document.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeShape;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;

public interface DocumentDao extends Dao<Document, UUID>
{
	public long totalSize();
	
	public List<Document> findByCreator(Principal principal);
	public List<Document> findByCustomer(Tenant c);
	public List<Document> findByCreatorAndCustomer(Principal principal, Tenant c);
	
	public Project findProjectOfDocument(Document d) throws Exception;
//	public List<GeocodeShape> findShapesOfDocument(Document d) throws Exception;
//	public GeocodeShape findShapeOfDocument(Document d, GeocodeSystem t) throws Exception;
	
	public List<WorkflowTask> findWorkflowTasksOfDocument(Project p, Document d) throws Exception;
	public long countWorkflowTasksOfDocument(Project p, Document d) throws Exception;
	
	public List<UUID> listDocuments();
	
	public List<Document> getDocumentsOfProject(Project project);
	public List<Document> getDocumentsOfShape(Shape shape);
	public Document getDocumentOfShape(Shape s, GeocodeSystem t);
//	public Document getDocumentOfShape(Shape s, GeocodeShape tts);
	public List<Document> getDocumentsOfWorkflowTask(WorkflowTask wt);
	
	public List<Document> searchDocuments(List<String> terms);
	public List<Document> searchDocumentsOfProject(List<String> terms, Project project);	
	public List<Document> searchDocumentsOfCreator(List<String> terms, Principal principal);
}

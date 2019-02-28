package gr.cite.gaap.servicelayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import gr.cite.gaap.servicelayer.exception.DocumentNotFoundException;
import gr.cite.gaap.servicelayer.exception.UnauthorizedOperationException;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.document.dao.DocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerDao;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.MimeType;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.dao.MimeTypeDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeShape;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.GeocodeShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDao;
import gr.cite.geoanalytics.dataaccess.entities.user.dao.UserDaoOld;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentManager {
	private GeocodeManager taxonomyManager;
	private ConfigurationManager configurationManager;
	//private SecurityContextAccessor securityContextAccessor;
	
	private PrincipalDao principalDao;
	private TenantDao tenantDao;
	private DocumentDao documentDao;
	private ProjectDocumentDao projectDocumentDao;
	private ShapeDocumentDao shapeDocumentDao;
	private ShapeDao shapeDao;
//	private GeocodeShapeDao geocodeShapeDao;
	private ProjectDao projectDao;
	private MimeTypeDao mimeTypeDao;
	private LayerDao layerDao;
	
	private static Logger log = LoggerFactory.getLogger(DocumentManager.class);
	
	@Inject
	public void setDataRepository(GeocodeManager taxonomyManager, ConfigurationManager configurationManager)
	{
		this.taxonomyManager = taxonomyManager;
		this.configurationManager = configurationManager;
		//this.securityContextAccessor = securityContextAccessor;
	}

	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}

	@Inject
	public void setCustomerDao(TenantDao tenantDao) {
		this.tenantDao = tenantDao;
	}

	@Inject
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	
	@Inject
	public void setLayerDao(LayerDao layerDao) {
		this.layerDao = layerDao;
	}

	@Inject
	public void setProjectDocumentDao(ProjectDocumentDao projectDocumentDao) {
		this.projectDocumentDao = projectDocumentDao;
	}

	@Inject
	public void setShapeDocumentDao(ShapeDocumentDao shapeDocumentDao) {
		this.shapeDocumentDao = shapeDocumentDao;
	}

//	@Inject
//	public void setGeocodeShapeDao(GeocodeShapeDao geocodeShapeDao) {
//		this.geocodeShapeDao = geocodeShapeDao;
//	}

	@Inject
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	@Inject
	public void setMimeTypeDao(MimeTypeDao mimeTypeDao) {
		this.mimeTypeDao = mimeTypeDao;
	}
	
	public class DocumentInfo {
		private Document document = null;
		private Project project = null;
		private List<Shape> shapes = null;
		private List<WorkflowTask> workflowTasks = null;
		
		public Document getDocument() {
			return document;
		}
		
		public void setDocument(Document document) {
			this.document = document;
		}
		
		public List<Shape> getShapes() {
			return shapes;
		}
		
		public void setShapes(List<Shape> shapes) {
			this.shapes = shapes;
		}
		
		public Project getProject() {
			return project;
		}
		
		public void setProject(Project project) {
			this.project = project;
		}
		
		public List<WorkflowTask> getWorkflowTasks() {
			return this.workflowTasks;
		}
		
		public void setWorkflowTasks(List<WorkflowTask> workflowTasks) {
			this.workflowTasks = workflowTasks;
		}
	}
	
	private void getDocumentDetails(Document d) {
		d.getCreator().getName();
		d.getCreator().getTenant().getName();
		if(d.getTenant() != null) d.getTenant().getName();
	}
	
	private void getProjectDetails(Project p)
	{
		p.getCreator().getName();
		p.getCreator().getTenant().getName();
		if(p.getTenant() != null) p.getTenant().getName();
	}
	
	private List<DocumentInfo> getInfo(List<Document> docs) throws Exception
	{
		List<DocumentInfo> res = new ArrayList<DocumentInfo>();
		for(Document d : docs)
		{
			DocumentInfo di = new DocumentInfo();
			di.setDocument(d);
			Project p = documentDao.findProjectOfDocument(d);
			if(p != null)
			{
				getProjectDetails(p);
				di.setProject(p);
			}
//			List<GeocodeShape> ttss = documentDao.findShapesOfDocument(d);
//			if(ttss != null)
//			{
//				List<Shape> docS = new ArrayList<Shape>();
//				for(GeocodeShape tts : ttss)
//					docS.add(tts.getShape());
//				di.setShapes(docS);
//			}
			res.add(di);
		}
		return res;
	}
	
	private Document findById(String id, boolean loadDetails, boolean secured) throws  UnauthorizedOperationException, Exception
	{
		Document d = documentDao.read(UUID.fromString(id));
		if(secured) checkAccessDocument(d);
		if(d != null && loadDetails)
			getDocumentDetails(d);
		return d;
	}
	
	@Transactional(readOnly = true)
	public Document findById(String id, boolean loadDetails) throws Exception
	{
		return findById(id, loadDetails, false);
	}
	
	@Transactional(readOnly = true)
	public Document findByIdSecure(String id, boolean loadDetails) throws UnauthorizedOperationException, Exception
	{
		return findById(id, loadDetails, true);
	}
	
	
	private void checkAccessDocument(Document d) throws UnauthorizedOperationException, Exception
	{
		//TODO NO SECURITY DANGER DANGER!!!!
		/*if(!securityContextAccessor.isAdministrator() && securityContextAccessor.isFullyAuthenticated())
		{
			if(!securityContextAccessor.canAccessDocument(d))
			{
				User u = securityContextAccessor.getPrincipal();
				log.error("User " + u.getSystemName() + " is not authorized to retrieve document " + d.getId());
				throw new UnauthorizedOperationException("User " + u.getSystemName() + " is not authorized to retrieve document " + d.getId());
			}
			
		}*/
	}
	@Transactional(readOnly = true)
	public Document findByIdSecured(String id, boolean loadDetails) throws UnauthorizedOperationException, Exception
	{
		Document d = documentDao.read(UUID.fromString(id));
		checkAccessDocument(d);
		if(d != null && loadDetails)
			getDocumentDetails(d);
		return d;
	}
	
//	@Transactional(readOnly = true)
//	public RepositoryFile getContentById(String id) throws DocumentNotFoundException, UnauthorizedOperationException, Exception
//	{
//		Document d = findById(id, false);
//		checkAccessDocument(d);
//		if(d == null)
//		{
//			log.error("Document " + id + " not found");
//			throw new DocumentNotFoundException("Document " + id + " not found", id);
//		}
//		RepositoryFile rf = repository.retrieve(d.getId().toString());
//		if(rf == null)
//		{
//			log.error("Retrieval of document " + id + " from repository was unsuccessful");
//			throw new Exception("Retrieval of document " + id + " from repository was unsuccessful");
//		}
//		return rf;
//	}
	
	@Transactional(readOnly = true)
	public List<Document> allDocuments() throws Exception
	{
		return documentDao.getAll();
	}
	
	@Transactional(readOnly = true)
	public List<DocumentInfo> allDocumentsInfo() throws Exception
	{
		return getInfo(allDocuments());
	}
	
	@Transactional(readOnly = true)
	public DocumentInfo findByIdInfo(String id) throws Exception
	{
		return getInfo(Collections.singletonList(findById(id, false))).get(0);
	}
	
	@Transactional(readOnly = true)
	public List<Document> findByCreatorAndCustomer(String name, String customerName) throws Exception {
		Principal principal =  principalDao.findActivePrincipalByName(name);
		if(principal != null) principal.getCreator().getName();
		if(customerName == null || (principal != null && principal.getTenant() != null && principal.getTenant().getName().equals(customerName)))
			return documentDao.findByCreatorAndCustomer(principal, principal.getTenant());
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<DocumentInfo> findByCreatorAndCustomerInfo(String name, String customerName) throws Exception {
		return getInfo(findByCreatorAndCustomer(name, customerName));
	}
	
	@Transactional(readOnly = true)
	public List<Document> findByCreator(String name) throws Exception {
		Principal u =  principalDao.findActivePrincipalByName(name);
		if(u == null) {
			log.error("User " + name + " not found");
			throw new Exception("User " + name + " not found");
		}
		return documentDao.findByCreator(u);
	}
	
	@Transactional(readOnly = true)
	public List<DocumentInfo> findByCreatorInfo(String name) throws Exception {
		return getInfo(findByCreator(name));
	}
	
	@Transactional(readOnly = true)
	public List<Document> findByCustomer(String customerName) throws Exception {
		List<Tenant> c = tenantDao.findByName(customerName);
		if(c == null || c.isEmpty()) {
			log.error("Customer " + customerName + " not found");
			throw new Exception("Customer " + customerName + " not found");
		}
		if(c.size() > 1) {
			log.error("More than one customers with name " + customerName + " were found");
			throw new Exception("More than one customers with name " + customerName + " were found");
		}
		return documentDao.findByCustomer(c.get(0));
	}
	
	@Transactional(readOnly = true)
	public List<DocumentInfo> findByCustomerInfo(String customerName) throws Exception
	{
		return getInfo(findByCustomer(customerName));
	}
	
	@Transactional(readOnly = true)
	public List<Document> findByProject(UUID projectId) throws Exception
	{
		Project p = projectDao.read(projectId);
		if(p == null)
		{
			log.error("Project "+ projectId + " not found");
			throw new Exception("Project " + projectId + " not found");
		}
		return documentDao.getDocumentsOfProject(p);
	}
	
	@Transactional(readOnly = true)
	public List<DocumentInfo> findByProjectInfo(UUID projectId) throws Exception
	{
		return getInfo(findByProject(projectId));
	}
	
//	@Transactional(rollbackFor={Exception.class})
//	public void create(Document d, RepositoryFile rf) throws Exception
//	{
//		
//		if(d.getName() != null) d.setName(rf.getOriginalName());
//		d.setSize((int)rf.getSize()); //TODO perhaps size should be long?
//		d.setDescription(d.getDescription());
//		
//		String ext = FilenameUtils.getExtension(rf.getOriginalName());
//	
//		List<MimeType> mts = null;
//		
//		if(ext != null && !ext.isEmpty())
//			mts = mimeTypeDao.findByExtension(ext);
//		
//		if(mts != null && !mts.isEmpty())
//		{
//			MimeType mt = mts.get(0); //TODO multiple mappings?
//			d.setMimeType(mt.getMimeType());
//			d.setMimeSubType(mt.getMimeSubType());
//			rf.setDataType(mt.getMimeType()+"/"+mt.getMimeSubType());
//		}
//		else
//		{
//			javax.activation.MimeType mt = new javax.activation.MimeType(rf.getDataType());
//			d.setMimeType(mt.getPrimaryType());
//			d.setMimeSubType(mt.getSubType());
//		}
//		
//		String fileId = repository.persist(rf);
//		d.setId(UUID.fromString(fileId)); //TODO depends on repository uuid generator
//		d.setUrl(repository.retrieve(fileId).getLocalImage().toString());
//		
//		documentDao.create(d);
//	}
	
	@Transactional
	public void update(Document d) throws Exception
	{
		Document ex = documentDao.read(d.getId());
		if(ex == null)
		{
			log.error("Document" + d.getId() + " not found");
			throw new Exception("Document" + d.getId() + " not found");
		}
		ex.setDescription(d.getDescription());
		ex.setName(d.getName());
		documentDao.update(ex);
	}
	
//	@Transactional(rollbackFor={Exception.class})
//	public boolean update(Document d, RepositoryFile rf, Principal principal) throws Exception
//	{
//		Document ex = documentDao.read(d.getId());
//		if(principal != null && !ex.getCreator().getId().equals(principal.getId())) return false;
//		if(ex == null)
//		{
//			log.error("Document" + d.getId() + " not found");
//			throw new Exception("Document" + d.getId() + " not found");
//		}
//		if(d.getName() != null) ex.setName(d.getName());
//		
//		if(d.getDescription() != null) ex.setDescription(d.getDescription());
//		if(rf != null)
//		{
//			rf.setId(ex.getId().toString());
//			ex.setSize((int)rf.getSize()); //TODO perhaps size should be long?
//			
//			List<MimeType> mts = null;
//					
//			String ext = FilenameUtils.getExtension(rf.getOriginalName());
//			if(ext != null && !ext.isEmpty())
//				mts = mimeTypeDao.findByExtension(ext); 
//				
//			if(mts != null && !mts.isEmpty())
//			{
//				MimeType mt = mts.get(0); //TODO multiple mappings?
//				ex.setMimeType(mt.getMimeType());
//				ex.setMimeSubType(mt.getMimeSubType());
//				rf.setDataType(mt.getMimeType()+"/"+mt.getMimeSubType());
//			}else
//			{
//				javax.activation.MimeType mt = new javax.activation.MimeType(rf.getDataType());
//				ex.setMimeType(mt.getPrimaryType());
//				ex.setMimeSubType(mt.getSubType());
//			}
//			
//			repository.update(rf);
//		}
//		documentDao.update(ex);
//		return true;
//	}
	
	@Transactional(readOnly = true)
	public List<UUID> listDocuments() throws Exception
	{
		return documentDao.listDocuments();
	}

	
	@Transactional(readOnly = true)
	public List<Document> getDocumentsOfProject(UUID projectId) throws Exception
	{
		Project p = projectDao.read(projectId);
		if(p == null)
		{
			log.error("Project " + projectId + " not found");
			throw new Exception("Project " + projectId + " not found");
		}
		return documentDao.getDocumentsOfProject(p);
	}
	
	@Transactional(readOnly = true)
	public List<Document> getDocumentsOfShape(UUID shapeId) throws Exception
	{
		Shape s = shapeDao.read(shapeId);
		if(s == null)
		{
			log.error("Shape " + shapeId + " not found");
			throw new Exception("Shape " + shapeId + " not found");
		}
		return documentDao.getDocumentsOfShape(s);
	}
	
	@Transactional(readOnly = true)
	public List<Document> searchDocuments(List<String> terms) throws Exception
	{
		return documentDao.searchDocuments(terms);
	}
	
	@Transactional(readOnly = true)
	public List<DocumentInfo> searchDocumentsInfo(List<String> terms) throws Exception
	{
		return getInfo(searchDocuments(terms));
	}
	
	@Transactional(readOnly = true)
	public List<Document> searchDocumentsOfProject(List<String> terms, UUID projectId) throws Exception
	{
		Project p = projectDao.read(projectId);
		if(p == null)
		{
			log.error("Project " + projectId + " not found");
			throw new Exception("Project " + projectId + " not found");
		}
		return documentDao.searchDocumentsOfProject(terms, p);
	}
	
//	@Transactional(readOnly = true)
//	public List<DocumentInfo> searchDocumentsOfProjectInfo(List<String> terms, UUID projectId) throws Exception
//	{
//		return getInfo(searchDocumentsOfProject(terms, projectId));
//	}
//	
//	@Transactional(rollbackFor={Exception.class})
//	public void delete(List<String> documents, Principal principal) throws UnauthorizedOperationException, Exception {
//		for(String doc : documents) {
//			Document d = documentDao.read(UUID.fromString(doc));
//			if(principal != null) {	//TODO NO SECURITY DANGER DANGER!!!!
//				/*if(!securityContextAccessor.isAdministrator() && !u.getId().equals(d.getCreator().getId()))
//				{
//					log.error("User " + u.getSystemName() + " is not authorized to delete document " + doc);
//					throw new UnauthorizedOperationException("User " + u.getSystemName() + " is not authorized to delete document " + doc);
//				}*/
//			}else
//				//TODO NO SECURITY DANGER DANGER!!!!
//				/*if(!securityContextAccessor.isAdministrator())
//				{
//					log.error("User " + u.getSystemName() + " is not authorized to delete document " + doc);
//					throw new UnauthorizedOperationException("User " + u.getSystemName() + " is not authorized to delete document " + doc);
//				}*/
//			if(d == null) {
//				log.error("Document " + doc + " not found");
//				throw new Exception("Document " + doc + " not found");
//			}
//			projectDocumentDao.deleteByDocument(d);
//			shapeDocumentDao.deleteByDocument(d);
//			documentDao.delete(d);
//			repository.delete(d.getId().toString());
//		}
//	}
	
	@Transactional(readOnly = true)
	public Map<String, String> attributeDocuments(Map<String, String> values) throws UnauthorizedOperationException, Exception
	{
		Map<String, String> attrDocs = new HashMap<String, String>();
		
		for(Map.Entry<String, String> val : values.entrySet())
		{
			GeocodeSystem t = taxonomyManager.findGeocodeSystemByName(val.getKey(), false);
			if(t == null)
			{
				log.error("Taxonomy " + val.getKey() + " not found");
				throw new Exception("Taxonomy " + val.getKey() + " not found");
			}
			List<AttributeMappingConfig> taxonMcfgs = configurationManager.getAttributeMappingsForTermId(t.getId().toString());
			String attrName = null;
			for(AttributeMappingConfig taxonMcfg : taxonMcfgs)
			{
				if(taxonMcfg.getAttributeValue() == null)
				{
					attrName = taxonMcfg.getAttributeName();
					break;
				}
			}
			if(attrName == null)
			{
				log.error("Could not find mapping for taxonomy " + t.getId() + " (" + t.getName() + ")");
				throw new Exception("Could not find mapping for taxonomy " + t.getId() + "(" + t.getName() + ")");
			}
			
			List<AttributeMappingConfig> valCfgs = configurationManager.getAttributeMappings(attrName, val.getValue());
			if(valCfgs == null)
				continue;
			
			List<String> processedTerms = new ArrayList<String>();
			Document d = null;
			for(AttributeMappingConfig valCfg : valCfgs)
			{
				if(processedTerms.contains(valCfg.getTermId()))
					continue;
				processedTerms.add(valCfg.getTermId());
				
				Layer layer = layerDao.read(UUID.fromString(valCfg.getTermId()));
				if(layer == null)
					continue;
				
				
//				List<TaxonomyTermShape> ttss = taxonomyTermShapeDao.findByTerm(tt);
//				for(TaxonomyTermShape tts : ttss)
//				{
//					//TODO NO SECURITY DANGER DANGER!!!!  <- what kind of comment is this? are you serious?
//					/*if(!securityContextAccessor.canAccessShape(tts.getShape()))
//					{
//						log.error("Unauthorized access of " + securityContextAccessor.getPrincipal().getSystemName() + " to shape " + tts.getShape().getId());
//						throw new UnauthorizedOperationException("Unauthorized access of " + securityContextAccessor.getPrincipal().getSystemName() + " to shape " + tts.getShape().getId());
//					}*/
//					d = shapeDocumentDao.findUniqueByTaxonomyTermShape(tts);
//					if(d != null)
//						break;
//				}
//				if(d != null)
//					break;
			}
//			if(d != null)
//				attrDocs.put(val.getKey(), d.getId().toString());
		}
		
		//now it always return empty... because of the above deactivation
		return attrDocs;
	}
}

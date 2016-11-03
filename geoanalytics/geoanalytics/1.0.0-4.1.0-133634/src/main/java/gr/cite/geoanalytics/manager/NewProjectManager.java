package gr.cite.geoanalytics.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.DocumentMessenger;
import gr.cite.gaap.datatransferobjects.GenericResponse;
import gr.cite.gaap.datatransferobjects.ProjectInfoMessenger;
import gr.cite.gaap.datatransferobjects.ProjectSummary;
import gr.cite.gaap.datatransferobjects.WorkflowTaskMessenger;
import gr.cite.gaap.datatransferobjects.GenericResponse.Status;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.DocumentManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.gaap.utilities.StringUtils;
import gr.cite.gaap.utilities.TaxonomyTermUtils;
import gr.cite.gaap.utilities.TaxonomyUtils;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.document.dao.DocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectDocument;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectDocumentPK;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectTerm;
import gr.cite.geoanalytics.dataaccess.entities.project.Project.ProjectStatus;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectTermDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocument;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocumentPK;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow.WorkflowStatus;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.Criticality;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.WorkflowTaskStatus;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowDao;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDao;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDocumentDao;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class NewProjectManager {
	
private static final Logger log = LoggerFactory.getLogger(ProjectManager.class);
	
	private PrincipalManager principalManager;
	private TaxonomyManager taxonomyManager;
	private ConfigurationManager configurationManager;
	private GeospatialBackend shapeManager;
	private SecurityContextAccessor securityContextAccessor;
	private DocumentManager documentManager;
	
	private ShapeDao shapeDao;
	private ProjectDao projectDao;
	private ProjectTermDao projectTermDao;
	private WorkflowDao workflowDao;
	private ProjectDocumentDao projectDocumentDao;
	private WorkflowTaskDao workflowTaskDao;
	private WorkflowTaskDocumentDao workflowTaskDocumentDao;
	private DocumentDao documentDao;
	
	@Inject
	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}
	
	@Inject
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	
	@Inject
	public void setProjectDocumentDao(ProjectDocumentDao projectDocumentDao) {
		this.projectDocumentDao = projectDocumentDao;
	}
	
	@Inject
	public void setShapeDao(ShapeDao shapeDao) {
		this.shapeDao = shapeDao;
	}
	
	@Inject
	public void setWorkflowDao(WorkflowDao workflowDao) {
		this.workflowDao = workflowDao;
	}
	
	@Inject
	public void setWorkflowTaskDao(WorkflowTaskDao workflowTaskDao) {
		this.workflowTaskDao = workflowTaskDao;
	}
	
	@Inject
	public void setWorkflowTaskDocumentDao(
			WorkflowTaskDocumentDao workflowTaskDocumentDao) {
		this.workflowTaskDocumentDao = workflowTaskDocumentDao;
	}
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setShapeManager(GeospatialBackend shapeManager) {
		this.shapeManager = shapeManager;
	}
	
	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	@Inject
	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Inject
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}
	
	@Inject
	public void setProjectTermDao(ProjectTermDao projectTermDao) {
		this.projectTermDao = projectTermDao;
	}
	
	public static class ProjectInfo {
		private Project project = null;
		private Shape shape = null;
				
		public Project getProject() {
			return project;
		}
		
		public void setProject(Project project) {
			this.project = project;
		}
		
		public Shape getShape() {
			return shape;
		}
		
		public void setShape(Shape shape) {
			this.shape = shape;
		}

	}
	
	private void getProjectDetails(Project project) {
		project.getCreator().getName();
		if(project.getCreator().getTenant() != null) project.getCreator().getTenant().getName();
		if(project.getTenant() != null) project.getTenant().getName();
	}
	
	private void getShapeDetails(Shape s) {
		s.getCreator().getName();
		if(s.getCreator().getTenant() != null) s.getCreator().getTenant().getName();
	}
	
	private void getWorkflowDetails(Workflow workflow) {
		workflow.getCreator().getName();
		workflow.getProject().getName();
		if(workflow.getTemplate() != null) workflow.getTemplate().getName();
	}
	
	private void getWorkflowTaskDetails(WorkflowTask workflowTask) {
		workflowTask.getCreator().getName();
		if(workflowTask.getPrincipal() != null) workflowTask.getPrincipal().getName();
		workflowTask.getWorkflow().getName();
		workflowTask.getWorkflow().getProject().getName();
	}
	
	private List<ProjectInfo> getInfo(List<Project> projects) throws Exception {
		
		List<ProjectInfo> projectsInfo = new ArrayList<ProjectInfo>();
		for(Project project : projects) {
			ProjectInfo projectInfo = new ProjectInfo();
			getProjectDetails(project);
			projectInfo.setProject(project);
			Shape shape = project.getShape() != null ? shapeDao.read(project.getShape()) : null;
			if(shape != null) {
				getShapeDetails(shape);
				projectInfo.setShape(shape);
			}
			
			projectsInfo.add(projectInfo);
		}
		return projectsInfo;
	}
	
	@Transactional(readOnly = true)
	public List<Project> allProjects() throws Exception {
		return projectDao.getAll();
	}
	
	@Transactional(readOnly = true)
	public List<ProjectInfo> allProjectsInfo() throws Exception {
		return getInfo(allProjects());
	}
	

	@Transactional(readOnly = true)
	public List<Map<String, String>> listIdAndNameOfAllProjects() throws Exception {
		
		List<Project> allPorjects = allProjects();
		List<Map<String, String>> projectIdAndName = new ArrayList<Map<String, String>>();
		for(Project project : allPorjects) {
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("id", project.getId().toString());
			attributes.put("name", project.getName());
			projectIdAndName.add(attributes);
		}
		
		return projectIdAndName;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectSummary> retrieveProjectSummariesForUser(Principal principal) throws Exception {
		
		List<ProjectSummary> summaries = new ArrayList<ProjectSummary>();
		
		List<Project> projects = projectDao.findActiveByCreator(principal);
		for(Project project : projects) {
			List<Workflow> workflows = projectDao.getWorkflowsOfProject(project);
			if(workflows == null || workflows.isEmpty()) {
				log.warn("Project " + project.getId() + " does not contain any workflows");
				continue;
			}
			if(workflows.size() > 1) {
				log.error("Multiple workflows were found in project " + project.getId() + 
						". This feature is not supported yet");

				throw new Exception("Multiple workflows were found in project " + project.getId() + 
						". This feature is not supported yet");
			}
			
			ProjectSummary projectSummary = new ProjectSummary();
			projectSummary.setClient(project.getClient());
			projectSummary.setId(project.getId().toString());
			projectSummary.setName(project.getName());
			projectSummary.setStartDate(workflows.get(0).getStartDate().getTime());
			projectSummary.setStatus(workflows.get(0).getStatus());
			
			if(project.getShape() != null){
				projectSummary.setShape(shapeManager.getGeometry(project.getShape()));
			}
			
			summaries.add(projectSummary);
		}
		
		return summaries;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateWorkflows(Principal principal) throws Exception {
		
		long now = new Date().getTime();
		List<Project> projects = projectDao.findActiveByCreator(principal);
		for(Project project : projects) {
			
			List<Workflow> workflows = projectDao.getWorkflowsOfProject(project);
			for(Workflow workflow : workflows) {
				
				List<WorkflowTask> workflowTasks = workflowDao.getWorkflowTasks(workflow);
				boolean updated = false;
				for(WorkflowTask workflowTask : workflowTasks) {
					
					if(workflowTask.getStatus() == WorkflowTaskStatus.INACTIVE && workflowTask.getStartDate().getTime() <= now
							&& (workflowTask.getEndDate() != null ? workflowTask.getEndDate().getTime() >= now : true)) {
						
						workflowTask.setStatus(WorkflowTaskStatus.ACTIVE);
						updateTask(workflowTask.getWorkflow().getProject(), workflowTask, false);
						updated = true;
					}else if(workflowTask.getStatus() == WorkflowTaskStatus.ACTIVE && (workflowTask.getStartDate().getTime() > now ||
							(workflowTask.getEndDate() != null ? workflowTask.getEndDate().getTime() < now : false)))
					{
						workflowTask.setStatus(WorkflowTaskStatus.INACTIVE);
						updateTask(workflowTask.getWorkflow().getProject(), workflowTask, false);
						updated = true;
					}
				}
				if(updated && checkWorkflowCompletion(workflow).isEmpty()) {
					
					workflow.setStatus(WorkflowStatus.COMPLETED);
					workflow.setStatusDate(new Date());
					workflowDao.update(workflow);
				}
			}
			
		}
	}
	
	@Transactional(readOnly = true)
	public Map<String, AttributeInfo> retrieveProjectInfo(Project project) throws Exception {
		
		project = findById(project.getId().toString(), true, false);
		if(project.getShape() == null) {
			return new HashMap<String, AttributeInfo>();
		}
		Shape shape = shapeManager.findShapeById(project.getShape());
		
		Map<String, AttributeInfo> attribute = shapeManager.retrieveShapeAttributes(shape);
		attribute.putAll(retrieveProjectUserAttributes(project, shape, attribute));
		return attribute;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void addProjectAttribute(Project project, Principal principal, AttributeInfo attributeInfo, String attributeClassType) throws Exception {
		
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		
		if(attributeInfo.getTaxonomy() == null){
			throw new Exception("Attribute lacks taxonomy");
		}
		if(attributeInfo.getTerm() == null){
			throw new Exception("Attribute lacks term");
		}
		if(attributeInfo.getName() == null){
			throw new Exception("Attribute lacks name");
		}
		if(attributeInfo.getValue() == null){
			throw new Exception("Attribute lacks value");
		}
		if(attributeClassType == null){
			throw new Exception("No attribute class");
		}
		
		Taxonomy taxonomy = taxonomyManager.findTaxonomyByName(attributeInfo.getTaxonomy(), false);
		if(taxonomy == null){
			taxonomy = taxonomyManager.findTaxonomyByName(StringUtils.normalizeEntityName(attributeInfo.getTaxonomy()), false);
		}
		
		String locale = LocaleContextHolder.getLocale().getLanguage();
		if(taxonomy == null) {
			
			String normalized = StringUtils.normalizeEntityName(attributeInfo.getTaxonomy());
			
			taxonomy = new Taxonomy();
			taxonomy.setIsUserTaxonomy(true);
			taxonomy.setName(normalized);
			taxonomy.setIsActive(true);
			taxonomy.setCreator(principal);
			taxonomy.setExtraData("<extraData userCreated=\"true\"><name locale=\"" + locale + "\">" + attributeInfo.getName() + "</name></extraData>");
			
			Taxonomy atttributeClassType = taxonomyManager.findTaxonomyByName(attributeClassType, false); //taxonomy config class is assumed to always exist
			if(atttributeClassType == null) {
				log.error("Provided taxonomy class does not exist: " + attributeClassType);
				throw new Exception("Provided taxonomy class does not exist " + attributeClassType);
			}
			taxonomy.setTaxonomyClass(atttributeClassType);
			taxonomyManager.updateTaxonomy(taxonomy, null, true);
			
		}else {
			
			if(taxonomy.getExtraData() == null || taxonomy.getExtraData().isEmpty()) {
				taxonomy.setExtraData("<extraData userCreated=\"true\"><name locale=\"" + locale + "\">" + attributeInfo.getName() + "</name></extraData>");
			}else {
				if(documentBuilderFactory == null) documentBuilderFactory = DocumentBuilderFactory.newInstance();
				if(documentBuilder == null) documentBuilder = documentBuilderFactory.newDocumentBuilder();
				String newData = TaxonomyUtils.storeLocalizedName(taxonomy, locale, attributeInfo.getName(), documentBuilder);
				if(newData != null) {
					taxonomy.setExtraData(newData);
					taxonomyManager.updateTaxonomy(taxonomy, taxonomy.getName(), false);
				}
			}
		}
		
		TaxonomyTerm taxonomyTerm = taxonomyManager.findTermByNameAndTaxonomy(StringUtils.normalizeEntityName(attributeInfo.getTerm()), taxonomy.getName(), false);
		if(taxonomyTerm == null) {
			String normalized = StringUtils.normalizeEntityName(attributeInfo.getTerm());
			
			taxonomyTerm = new TaxonomyTerm();
			taxonomyTerm.setName(normalized);
			taxonomyTerm.setIsActive(true);
			taxonomyTerm.setTaxonomy(taxonomy);
			taxonomyTerm.setCreator(principal);
			
			taxonomyTerm.setExtraData("<extraData userCreated=\"true\"><value locale=\"" + locale + "\">" + attributeInfo.getValue() + "</value></extraData>");
			
			taxonomyManager.updateTerm(taxonomyTerm, null, null, true);
		}else {
			
			if(taxonomyTerm.getExtraData() == null || taxonomyTerm.getExtraData().isEmpty()) {
				taxonomyTerm.setExtraData("<extraData userCreated=\"true\"><value locale=\"" + locale + "\">" + attributeInfo.getName() + "</value></extraData>");
			}else {
				if(documentBuilderFactory == null) documentBuilderFactory = DocumentBuilderFactory.newInstance();
				if(documentBuilder == null) documentBuilder = documentBuilderFactory.newDocumentBuilder();
				String newData = TaxonomyTermUtils.storeLocalizedValue(taxonomyTerm, locale, attributeInfo.getValue(), documentBuilder);
				if(newData != null) {
					taxonomyTerm.setExtraData(newData);
					taxonomyManager.updateTerm(taxonomyTerm, taxonomyTerm.getName(), taxonomyTerm.getTaxonomy().getName(), false);
				}
			}
		}
		
		ProjectTerm projectTerm = projectTermDao.find(project, taxonomyTerm);
		if(projectTerm == null) {
			projectTerm = new ProjectTerm();
			projectTerm.setProject(project);
			projectTerm.setTerm(taxonomyTerm);
			projectTerm.setCreator(principal);
			projectTermDao.create(projectTerm);
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void removeProjectAttribute(Project project, Principal principal, AttributeInfo attributeInfo) throws Exception {
		
		Taxonomy taxonomy = taxonomyManager.findTaxonomyByName(attributeInfo.getTaxonomy(), false);
		if(taxonomy == null){
			taxonomy = taxonomyManager.findTaxonomyByName(StringUtils.normalizeEntityName(attributeInfo.getTaxonomy()), false);
		}
		
		if(taxonomy == null) {
			log.error("Attribute " + attributeInfo.getTaxonomy() + " not found for project " + project.getId());
			throw new Exception("Attribute not found");
		}
		
		
		if(taxonomy.getTaxonomyClass() == null) {
			log.error("Taxonomy " + taxonomy.getName() + " referenced for project attribute update belongs to no class");
			throw new Exception("Attribute taxonomy belongs to no class");
		}
		
		TaxonomyConfig classTcfg = configurationManager.retrieveTaxonomyConfigById(taxonomy.getTaxonomyClass().getId().toString());
		if(classTcfg == null) {
			log.error("Class of attribute taxonomy " + taxonomy.getName() + " is not registered in taxonomy config");
			throw new Exception("Class of attribute taxonomy is not registered in taxonomy config");
		}
		
		
		if(!taxonomy.getIsUserTaxonomy() && !TaxonomyUtils.isEditable(taxonomy)) {
			log.error("Attempt to update non-user attribute");
			throw new Exception("Not a user attribute");
		}
		
		TaxonomyTerm attrT = projectTermDao.findByProjectAndTaxonomy(project, taxonomy);
		
		if(attrT == null) {
			log.error("Could not find taxonomy term for attribute " + attributeInfo.getName() + " for taxonomy "+ attributeInfo.getTaxonomy());
			throw new Exception("Could not find attribute");
		}
		
		ProjectTerm projectTerm = projectTermDao.find(project, attrT);
		if(projectTerm == null) {
			log.error("Unexpected error: could not find project term for project " + project.getId() + " and term " + attrT.getId() + "(" + attrT.getName() + ")");
			throw new Exception("Could not find attribute");
		}
			
		projectTermDao.delete(projectTerm);
		List<TaxonomyTerm> rest = taxonomyManager.getTermsOfTaxonomy(taxonomy.getId().toString(),false, false);
		if(rest.isEmpty()){
			taxonomyManager.deleteTaxonomies(Collections.singletonList(taxonomy.getId().toString()));
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateProjectAttribute(Project project, Principal principal, AttributeInfo attributeInfo) throws Exception {
		
		Taxonomy taxonomy = taxonomyManager.findTaxonomyByName(attributeInfo.getTaxonomy(), false);
		if(taxonomy == null){
			taxonomy = taxonomyManager.findTaxonomyByName(StringUtils.normalizeEntityName(attributeInfo.getTaxonomy()), false);
		}
		
		if(taxonomy == null) {
			log.error("Attribute " + attributeInfo.getTaxonomy() + " not found for project " + project.getId());
			throw new Exception("Attribute not found");
		}
		
		if(taxonomy.getTaxonomyClass() == null) {
			log.error("Taxonomy " + taxonomy.getName() + " referenced for project attribute update belongs to no class");
			throw new Exception("Attribute taxonomy belongs to no class");
		}
		
		TaxonomyConfig classTcfg = configurationManager.retrieveTaxonomyConfigById(taxonomy.getTaxonomyClass().getId().toString());
		if(classTcfg == null) {
			log.error("Class of attribute taxonomy " + taxonomy.getName() + " is not registered in taxonomy config");
			throw new Exception("Class of attribute taxonomy is not registered in taxonomy config");
		}
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		
		if(!taxonomy.getIsUserTaxonomy() && !TaxonomyUtils.isEditable(taxonomy, documentBuilder)) {
			log.error("Attempt to update non-user attribute");
			throw new Exception("Not a user attribute");
		}
		
		String locale = LocaleContextHolder.getLocale().getLanguage();
		TaxonomyTerm projectTaxonomyTerm = projectTermDao.findByProjectAndTaxonomy(project, taxonomy);
		
		boolean shouldCreate = false;
		if(projectTaxonomyTerm == null) {//for updates, allow term auto-creation only for configured taxonomies
			TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(taxonomy.getId().toString());
			if(tcfg == null) {
				log.error("Non configured taxonomy " + taxonomy.getId() + " (" + taxonomy.getName() + ")" + " with non-existing term specified for attribute update");
				throw new Exception("Non-configured taxonomy");
			}
			
			shouldCreate = true;
		}		
		
		String attributeValue = null;
		if(projectTaxonomyTerm != null){
			attributeValue = TaxonomyTermUtils.retrieveLocalizedValue(projectTaxonomyTerm, locale, documentBuilder);
		}
		
		if(!attributeInfo.getValue().equals(attributeValue)) {
			ProjectTerm oldProjectTerm = projectTermDao.find(project, projectTaxonomyTerm);
			if(!shouldCreate && oldProjectTerm == null) {
				log.error("Unexpected error: could not find project term for project " + project.getId() + " and term " + projectTaxonomyTerm.getId());
				throw new Exception("Could not find project term");
			}
			
			projectTermDao.delete(oldProjectTerm);
			
			String normalizedValue = StringUtils.normalizeEntityName(attributeInfo.getValue());
			TaxonomyTerm newTaxonomyTerm = taxonomyManager.findTermByNameAndTaxonomy(normalizedValue, taxonomy.getName(), false);
			
			if(newTaxonomyTerm == null) {
				newTaxonomyTerm = new TaxonomyTerm();
				newTaxonomyTerm.setCreator(principal);
				newTaxonomyTerm.setExtraData("<extraData userCreated=\"true\"><value locale=\"" + locale + "\">"+attributeInfo.getValue()+"</value>"+"</extraData>");
				newTaxonomyTerm.setIsActive(true);
				newTaxonomyTerm.setTaxonomy(taxonomy);
				newTaxonomyTerm.setName(normalizedValue);
				taxonomyManager.updateTerm(newTaxonomyTerm, null, null, true);
			}
			
			ProjectTerm newProjectTerm = new ProjectTerm();
			newProjectTerm.setProject(project);
			newProjectTerm.setTerm(newTaxonomyTerm);
			newProjectTerm.setCreator(principal);
			projectTermDao.create(newProjectTerm);
			
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateAllProjectAttributes() throws Exception {
		
		List<Project> projects = projectDao.getAll();
		for(Project project : projects) {
			if(project.getShape() == null) continue;
			Shape shape = shapeManager.findShapeById(project.getShape());
			createProjectAttributes(project, shape);
			shapeManager.update(shape);
		}
	}
	
	@Transactional(readOnly = true)
	public List<WorkflowTaskMessenger> retrieveProjectTasks(Project project) throws Exception {
		
		List<WorkflowTaskMessenger> workflowTaskMessengers = new ArrayList<WorkflowTaskMessenger>();
		
		List<Workflow> workflows = projectDao.getWorkflowsOfProject(project);
		if(workflows == null || workflows.isEmpty()) {
			log.warn("Project " + project.getId() + " does not contain any workflows");
			return workflowTaskMessengers;
		}
		
		if(workflows.size() > 1) {
			log.error("Multiple workflows were found in project " + project.getId() + 
					". This feature is not supported yet");

			throw new Exception("Multiple workflows were found in project " + project.getId() + 
					". This feature is not supported yet");
		}
		
		List<WorkflowTask> workflowTasks = workflowDao.getWorkflowTasks(workflows.get(0));
		
		for(WorkflowTask workflowTask : workflowTasks) {
			
			WorkflowTaskMessenger workflowTaskMessenger = new WorkflowTaskMessenger();
			
			workflowTaskMessenger.setName(workflowTask.getName());
			workflowTaskMessenger.setId(workflowTask.getId().toString());
			workflowTaskMessenger.setStartDate(workflowTask.getStartDate().getTime());
			if(workflowTask.getEndDate() != null) workflowTaskMessenger.setEndDate(workflowTask.getEndDate().getTime());
			if(workflowTask.getReminderDate() != null) workflowTaskMessenger.setReminderDate(workflowTask.getReminderDate().getTime());
			if(workflowTask.getExtraData() != null) workflowTaskMessenger.setExtraData(workflowTask.getExtraData());
			workflowTaskMessenger.setStatus(workflowTask.getStatus());
			workflowTaskMessenger.setStatusDate(workflowTask.getStatusDate().getTime());
			workflowTaskMessenger.setCritical(workflowTask.getCritical());
			workflowTaskMessenger.setNumDocuments(workflowTaskDao.countDocuments(workflowTask));
			
			workflowTaskMessengers.add(workflowTaskMessenger);
		}
		
		return workflowTaskMessengers;
	}
	
	@Transactional(readOnly = true)
	public List<WorkflowTaskMessenger> retrieveDocumentTasks(Project project, Document document) throws Exception {
		
		List<WorkflowTaskMessenger> workflowTaskMessengers = new ArrayList<WorkflowTaskMessenger>();
		
		List<WorkflowTask> workflowTasks = documentDao.findWorkflowTasksOfDocument(project, document);
		for(WorkflowTask workflowTask : workflowTasks) {
			
			WorkflowTaskMessenger workflowTaskMessenger = new WorkflowTaskMessenger();
			workflowTaskMessenger.setName(workflowTask.getName());
			workflowTaskMessenger.setId(workflowTask.getId().toString());
			workflowTaskMessenger.setStartDate(workflowTask.getStartDate().getTime());
			workflowTaskMessenger.setEndDate(workflowTask.getEndDate().getTime());
			if(workflowTask.getReminderDate() != null) workflowTaskMessenger.setReminderDate(workflowTask.getReminderDate().getTime());
			if(workflowTask.getExtraData() != null) workflowTaskMessenger.setExtraData(workflowTask.getExtraData());
			workflowTaskMessenger.setStatus(workflowTask.getStatus());
			workflowTaskMessenger.setStatusDate(workflowTask.getStatusDate().getTime());
			workflowTaskMessenger.setCritical(workflowTask.getCritical());
			workflowTaskMessenger.setNumDocuments(workflowTaskDao.countDocuments(workflowTask));
			
			workflowTaskMessengers.add(workflowTaskMessenger);
		}
		
		return workflowTaskMessengers;
	}
	
	@Transactional(readOnly = true)
	public List<DocumentMessenger> retrieveProjectDocuments(Project project) throws Exception {
		
		List<DocumentMessenger> documentMessengers = new ArrayList<DocumentMessenger>();
		List<Document> documents = documentDao.getDocumentsOfProject(project);
		
		if(documents == null || documents.isEmpty()) {
			return documentMessengers;
		}
				
		for(Document document : documents) {
			
			DocumentMessenger documentMessenger = new DocumentMessenger();
			
			documentMessenger.setId(document.getId().toString());
			documentMessenger.setName(document.getName());
			documentMessenger.setCreationDate(document.getCreationDate().getTime());
			documentMessenger.setDescription(document.getDescription());
			documentMessenger.setMimeType(document.getMimeType());
			documentMessenger.setMimeSubType(document.getMimeSubType());
			documentMessenger.setSize(document.getSize());
			documentMessenger.setNumOfWorkflowTasks(documentDao.countWorkflowTasksOfDocument(project, document));
			
			documentMessengers.add(documentMessenger);
		}
		
		return documentMessengers;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void addProjectDocument(String projectId, String documentId) throws Exception {
		
		Principal principal = securityContextAccessor.getPrincipal();
		Project project = findById(projectId, true, false);
		if(project == null) throw new Exception("Project " + projectId + " not found");
		Document document = documentManager.findById(documentId, false);
		if(document == null) throw new Exception("Document " + documentId + " not found");
		
		ProjectDocument projectDocument = new ProjectDocument();
		projectDocument.setCreator(principal);
		projectDocument.setProject(project);
		projectDocument.setDocument(document);
		projectDocumentDao.create(projectDocument);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void removeProjectDocument(String projectId, String documentId) throws Exception {
		
		Project project = findById(projectId, true, false);
		if(project == null) throw new Exception("Project " + projectId + " not found");
		Document document = documentManager.findById(documentId, false);
		if(document == null) throw new Exception("Document " + documentId + " not found");
		
		Workflow workflow = getWorkflowsOfProject(project).get(0); //TODO single workflow
		List<WorkflowTask> workflowTasks = workflowDao.getWorkflowTasks(workflow);
		
		for(WorkflowTask workflowTask : workflowTasks) {
			WorkflowTaskDocument workflowTaskDocument = workflowTaskDocumentDao.find(workflowTask, document);
			if(workflowTaskDocument != null) workflowTaskDocumentDao.delete(workflowTaskDocument);
		}
		
		ProjectDocumentPK projectDocumentPK = new ProjectDocumentPK(UUID.fromString(projectId), UUID.fromString(documentId));
		ProjectDocument projectDocument = projectDocumentDao.read(projectDocumentPK);
		
		if(projectDocument == null) throw new Exception("Document " + documentId + " is not part of project " + projectId);
		
		projectDocumentDao.delete(projectDocument);
	}
	
	@Transactional(readOnly = true)
	public List<DocumentMessenger> retrieveWorkflowTaskDocuments(WorkflowTask t) throws Exception {
		
		List<DocumentMessenger> documentMessengers = new ArrayList<DocumentMessenger>();
		
		List<Document> documents = documentDao.getDocumentsOfWorkflowTask(t);
		
		if(documents == null || documents.isEmpty()) {
			return documentMessengers;		
		}
		
		for(Document document : documents) {
			DocumentMessenger documentMessenger = new DocumentMessenger();
			
			documentMessenger.setId(document.getId().toString());
			documentMessenger.setName(document.getName());
			documentMessenger.setCreationDate(document.getCreationDate().getTime());
			documentMessenger.setDescription(document.getDescription());
			documentMessenger.setMimeType(document.getMimeType());
			documentMessenger.setMimeSubType(document.getMimeSubType());
			documentMessenger.setSize(document.getSize());
			documentMessenger.setNumOfWorkflowTasks(documentDao.countWorkflowTasksOfDocument(t.getWorkflow().getProject(), document));
			
			documentMessengers.add(documentMessenger);
		}
		
		return documentMessengers;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public GenericResponse.Status addWorkflowTaskDocument(String taskId, String documentId) throws Exception {

		Principal principal = securityContextAccessor.getPrincipal();
		WorkflowTask workflowTask = workflowTaskDao.read(UUID.fromString(taskId));
		if(workflowTask == null) throw new Exception("Workflow task " + taskId + " not found");
		Document document = documentManager.findById(documentId, false);
		if(document == null) throw new Exception("Document " + documentId + " not found");
		
		WorkflowTaskDocumentPK workflowTaskDocumentPK = new WorkflowTaskDocumentPK(UUID.fromString(taskId), UUID.fromString(documentId));
		WorkflowTaskDocument existingWorkflowTaskDocument = workflowTaskDocumentDao.read(workflowTaskDocumentPK);
		if(existingWorkflowTaskDocument != null) return GenericResponse.Status.Existing;
		
		WorkflowTaskDocument workflowTaskDocument = new WorkflowTaskDocument();
		workflowTaskDocument.setCreator(principal);
		workflowTaskDocument.setWorkflowTask(workflowTask);
		workflowTaskDocument.setDocument(document);
		workflowTaskDocumentDao.create(workflowTaskDocument);
		
		return GenericResponse.Status.Success;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void removeWorkflowTaskDocument(String taskId, String documentId) throws Exception {
		
		WorkflowTask workflowTask = workflowTaskDao.read(UUID.fromString(taskId));
		if(workflowTask == null) throw new Exception("Workflow task " + taskId + " not found");
		Document document = documentManager.findById(documentId, false);
		if(document == null) throw new Exception("Document " + documentId + " not found");
		
		WorkflowTaskDocumentPK workflowTaskDocumentPK = new WorkflowTaskDocumentPK(UUID.fromString(taskId), UUID.fromString(documentId));
		WorkflowTaskDocument workflowTaskDocument = workflowTaskDocumentDao.read(workflowTaskDocumentPK);
		
		if(workflowTaskDocument == null) throw new Exception("Document " + documentId + " is not part of workflow task " + taskId);
		
		workflowTaskDocumentDao.delete(workflowTaskDocument);
	}
	
	@Transactional(readOnly = true)
	public ProjectInfo findByIdInfo(String projectId, boolean active) throws Exception {
		return getInfo(Collections.singletonList(findById(projectId, active, true))).get(0);
	}
	
	@Transactional(readOnly = true)
	public List<Project> findByCreator(Principal principal, boolean active) throws Exception {
		
		List<Project> projects = new ArrayList<Project>();
		if(active) {
			projects = projectDao.findActiveByCreator(principal);
		}else {
			projects = projectDao.findByCreator(principal);
		}
		return projects;
	}
	
	@Transactional(readOnly = true)
	public Project findByNameAndCreator(String name, Principal principal, boolean active) throws Exception {
		
		List<Project> projects = null;
		if(active){ 
			projects =  projectDao.findActiveByNameAndCreator(name, principal);
		}else {
			projects = projectDao.findByNameAndCreator(name, principal);
		}
		
		if(projects != null && projects.size() > 1){
			throw new Exception("Multiple projects with name " + name);
		}
		
		return projects != null && !projects.isEmpty() ? projects.get(0) : null;
	}
	
	@Transactional(readOnly = true)
	public Project findById(String projectId, boolean active, boolean loadDetails) throws Exception {
		
		Project project = projectDao.read(UUID.fromString(projectId));
		
		if(active && project != null && project.getStatus() != ProjectStatus.ACTIVE) {
			return null;
		}
		if(project != null && loadDetails){
			getProjectDetails(project);
		}
		
		return project;
	}
	
	@Transactional(readOnly = true)
	public Workflow findWorkflowById(String workflowId, boolean loadDetails) throws Exception {
		
		Workflow workflow = workflowDao.read(UUID.fromString(workflowId));
		
		if(workflow != null && loadDetails){
			getWorkflowDetails(workflow);
		}
		return workflow;
	}
	
	@Transactional(readOnly = true)
	public WorkflowTask findTaskById(String taskId, boolean loadDetails) throws Exception {
		
		WorkflowTask workflowTask = workflowTaskDao.read(UUID.fromString(taskId));
		if(workflowTask != null && loadDetails){
			getWorkflowTaskDetails(workflowTask);
		}
		return workflowTask;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectInfo> findByCreatorInfo(Principal principal, boolean active) throws Exception {
		return getInfo(findByCreator(principal, active));
	}
	
	@Transactional(readOnly = true)
	public List<Project> findByCustomer(Tenant tenant, boolean active) throws Exception {
		
		List<Project> projects = new ArrayList<Project>();
		
		if(active){ 
			projects =  projectDao.findByActiveTenant(tenant);}
		else{ 
			projects =  projectDao.findByTenant(tenant);
		}
		
		return projects;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectInfo> findByCustomerInfo(Tenant tenant, boolean active) throws Exception {
		return getInfo(findByCustomer(tenant, active));
	}
	
	@Transactional(readOnly = true)
	public Set<Taxonomy> findProjectUserTaxonomies(Project project, Principal principal, Map<String, String> customNames) throws Exception {
		
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		if(project == null) {
			log.error("No project provided");
			throw new Exception("No project provided");
		}
		if(principal == null) {
			log.error("No user provided");
			throw new Exception("No user provided");
		}
		if(customNames != null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}
		
		Set<Taxonomy> userTaxonomies = new HashSet<Taxonomy>();
		List<TaxonomyTerm> taxonomyTerms = projectTermDao.findByProject(project);
		for(TaxonomyTerm taxonomyTerm : taxonomyTerms) {
			userTaxonomies.add(taxonomyTerm.getTaxonomy());
			if(customNames != null && taxonomyTerm.getTaxonomy().getExtraData() != null){
				customNames.put(taxonomyTerm.getTaxonomy().getName(), TaxonomyUtils.retrieveLocalizedName(taxonomyTerm.getTaxonomy(), LocaleContextHolder.getLocale().getLanguage(), documentBuilder));
			}
		}
			
		return userTaxonomies;
	}
	

	@Transactional(readOnly = true)
	public Set<Taxonomy> findProjectUserTaxonomies(Project project, Principal principal) throws Exception {
		return findProjectUserTaxonomies(project, principal, null);
	}
	
	@Transactional(readOnly = true)
	public Set<Taxonomy> findProjectUserTaxonomies(Principal principal, boolean active, Map<String, String> customNames) throws Exception {
		
		Set<Taxonomy> userTaxonomies = new HashSet<Taxonomy>();
		List<Project> projects = findByCreator(principal, active);
		
		for(Project project : projects){
			userTaxonomies.addAll(findProjectUserTaxonomies(project, principal, customNames));
		}
		return userTaxonomies;
			
	}
	
	@Transactional(readOnly = true)
	public Set<Taxonomy> findProjectUserTaxonomies(Principal principal, boolean active) throws Exception {
		return findProjectUserTaxonomies(principal, active, null);
	}
	
	private Map<String, AttributeInfo> retrieveProjectUserAttributes(Project project, Shape shape, Map<String, AttributeInfo> attributes) throws Exception {
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		
		Set<Taxonomy> userTaxonomies = new HashSet<Taxonomy>();
		Map<String, AttributeInfo> userAttributes = new HashMap<String, AttributeInfo>();
		List<TaxonomyConfig> infoCategories = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.PROJECTINFOCATEGORYTAXONOMY);
		for(TaxonomyConfig infoCategory : infoCategories) {
			
			List<TaxonomyConfig> configurations = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.valueOf(infoCategory.getType())); 
			for(TaxonomyConfig configuration : configurations) {
				
				Taxonomy taxonomy = taxonomyManager.findTaxonomyById(configuration.getId(), false);
				if(taxonomy.getIsUserTaxonomy()) {
					userTaxonomies.add(taxonomy);
				}
			}
		}
		
		List<TaxonomyTerm> projectTaxonomyTerms = projectTermDao.findByProject(project);
		Set<TaxonomyTerm> foundTaxonomyTerms = new HashSet<TaxonomyTerm>();
		for(Taxonomy userTaxonomy : userTaxonomies) {
			
			if(attributes.containsKey(userTaxonomy.getId().toString())){
				continue;
			}
			
			String attributeValue = "";
			for(TaxonomyTerm projectTaxonomyTerm : projectTaxonomyTerms) {
				if(projectTaxonomyTerm.getTaxonomy().getId().equals(userTaxonomy.getId())) {
					attributeValue = TaxonomyTermUtils.getUserCreatedTermValue(projectTaxonomyTerm, documentBuilder);
					foundTaxonomyTerms.add(projectTaxonomyTerm);
					break;
				}
					
			}
			AttributeInfo attributeInfo = new AttributeInfo();
			attributeInfo.setName(userTaxonomy.getName().toString());
			attributeInfo.setTaxonomy(userTaxonomy.getName().toString());
			attributeInfo.setType(userTaxonomy.getName().toString());
			attributeInfo.setValue(attributeValue);
			attributeInfo.setPresentable(true);
			
			userAttributes.put(userTaxonomy.getId().toString(), attributeInfo);
		}
		
		for(TaxonomyTerm projectTaxonomyTerm : projectTaxonomyTerms) {
			
			if(foundTaxonomyTerms.contains(projectTaxonomyTerm)) {
				continue;
			}
			
			String attributeValue = TaxonomyTermUtils.getUserCreatedTermValue(projectTaxonomyTerm, documentBuilder);
			AttributeInfo attributeInfo = new AttributeInfo();
			attributeInfo.setName(projectTaxonomyTerm.getTaxonomy().getName().toString());
			attributeInfo.setTaxonomy(projectTaxonomyTerm.getTaxonomy().getName().toString());
			attributeInfo.setType(projectTaxonomyTerm.getTaxonomy().getName().toString());
			attributeInfo.setValue(attributeValue);
			attributeInfo.setPresentable(true);
			
			userAttributes.put(projectTaxonomyTerm.getTaxonomy().getId().toString(), attributeInfo);		
		}
		
		return userAttributes;
	}
	
	@Transactional(readOnly = true)
	public List<AttributeInfo> retrieveCommonUserAttributes(Principal principal) throws Exception {
		
		principal = principalManager.getPrincipal(principal.getId()); //retrieve again in transactional context so that we can fetch customer
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		
		Set<Taxonomy> userTaxonomies = new HashSet<Taxonomy>();
		List<TaxonomyConfig> infoCategories = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.PROJECTINFOCATEGORYTAXONOMY);
		for(TaxonomyConfig infoCategory : infoCategories) {
			List<TaxonomyConfig> configurations = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.valueOf(infoCategory.getType())); 
			for(TaxonomyConfig configuration : configurations) {
				Taxonomy taxonomy = taxonomyManager.findTaxonomyById(configuration.getId(), false);
				if(taxonomy.getIsUserTaxonomy()) {
					userTaxonomies.add(taxonomy);
				}
			}
		}
		
		List<Taxonomy> projectTaxonomies = null;
		if(principal.getTenant() != null){
			projectTaxonomies = projectTermDao.findByTenant(principal.getTenant());
		}else {
			return new ArrayList<AttributeInfo>();
		}
		
		List<AttributeInfo> attributeInfos = new ArrayList<AttributeInfo>();
		
		for(Taxonomy projectTaxonomy : projectTaxonomies) {
			if(userTaxonomies.contains(projectTaxonomy)){
				continue;
			}
			
			AttributeInfo attributeInfo = new AttributeInfo();
			attributeInfo.setName(TaxonomyUtils.retrieveLocalizedName(projectTaxonomy, LocaleContextHolder.getLocale().getLanguage(), documentBuilder));
			attributeInfo.setTaxonomy(projectTaxonomy.getName().toString());
			attributeInfo.setType(projectTaxonomy.getName().toString());
			
			attributeInfos.add(attributeInfo);		
		}
		
		return attributeInfos;
	}
	
	private void createProjectAttributes(Project project, Shape shape) throws Exception {
		Map<String, AttributeInfo> attributes = new HashMap<String, AttributeInfo>();
		attributes.putAll(shapeManager.consolidateAttributes(shape));
		attributes.putAll(shapeManager.computeAttributes(shape));
		shapeManager.setShapeAttributes(shape, attributes);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void create(Project project, Workflow wokrflow, String shapeGeometry) throws Exception {
		
		project.setStatus(ProjectStatus.ACTIVE);
		if(project.getCreator() == null){ 
			throw new Exception("No creator assigned to project");
		}
		project.setCreator(principalManager.getPrincipal(project.getCreator().getId())); //reload just to use the same session
		
		if(project.getCreator().getTenant() == null && !securityContextAccessor.getRoles().contains("ROLE_admin")){ 
			throw new Exception("Project creator has no assigned customer");
		}
		
		project.setTenant(project.getCreator().getTenant());
		
		Shape shape = null;
		if(shapeGeometry != null) {
			shape = shapeManager.createFromGeometry(project, shapeGeometry);
			createProjectAttributes(project, shape);
			project.setShape(shape.getId());
		}
		projectDao.create(project);
		
		if(wokrflow.getStartDate() != null && wokrflow.getStartDate().getTime() <= new Date().getTime()){
			wokrflow.setStatus(WorkflowStatus.ACTIVE);
		}else {
			wokrflow.setStatus(WorkflowStatus.INACTIVE);
		}
		wokrflow.setStatusDate(new Date());
		wokrflow.setProject(project);
		
		if(wokrflow.getStartDate() == null){
			wokrflow.setStartDate(new Date());
		}
		
		workflowDao.create(wokrflow);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public GenericResponse create(ProjectInfoMessenger projectInfoMessenger) throws Exception {
		
		Principal creator = securityContextAccessor.getPrincipal();
		if(projectInfoMessenger == null || projectInfoMessenger.getProjectMessenger() == null || projectInfoMessenger.getProjectMessenger().getName() == null) {
			log.error("Project " + (projectInfoMessenger != null && projectInfoMessenger.getProjectMessenger() != null ? "name " : "") + "not provided");
			return new GenericResponse(GenericResponse.Status.Failure, null, 
					"Project " + (projectInfoMessenger != null && projectInfoMessenger.getProjectMessenger() != null ? "name " : "") + "not provided");
		}
		
		if(projectInfoMessenger.getProjectMessenger() == null){
			return new GenericResponse(GenericResponse.Status.Failure, null, "Illegal argument");
		}
		
		Project oldPorject = this.findByNameAndCreator(projectInfoMessenger.getProjectMessenger().getName(), creator, false);
		if(oldPorject != null) {
			log.error("Project " + projectInfoMessenger.getProjectMessenger().getName() + " already exists");
			return new GenericResponse(GenericResponse.Status.Existing, null, "Project " + projectInfoMessenger.getProjectMessenger().getName() + " already exists");
		}
		
		Project project = new Project();
		project.setCreator(creator);
		if(projectInfoMessenger.getProjectMessenger().getDescription() != null){
			project.setDescription(HtmlUtils.htmlEscape(projectInfoMessenger.getProjectMessenger().getDescription().trim()));
		}
		project.setIsTemplate(projectInfoMessenger.getProjectMessenger().isTemplate());
		if(projectInfoMessenger.getProjectMessenger().getClient() != null){
			project.setClient(HtmlUtils.htmlEscape(projectInfoMessenger.getProjectMessenger().getClient().trim()));
		}
		if(projectInfoMessenger.getProjectMessenger().getName() != null){
			project.setName(HtmlUtils.htmlEscape(projectInfoMessenger.getProjectMessenger().getName().trim()));
		}
		
		Workflow workflow = new Workflow();
		workflow.setCreator(creator);
		if(projectInfoMessenger.getWorkflowMessenger() != null)
		{
			if(projectInfoMessenger.getWorkflowMessenger().getDescription() != null)
				workflow.setDescription(HtmlUtils.htmlEscape(projectInfoMessenger.getWorkflowMessenger().getDescription().trim()));
		}
		if(projectInfoMessenger.getWorkflowMessenger() != null && projectInfoMessenger.getWorkflowMessenger().getName() != null) 
			workflow.setName(HtmlUtils.htmlEscape(projectInfoMessenger.getWorkflowMessenger().getName().trim()));
		else 
			workflow.setName(project.getName());
		if(projectInfoMessenger.getWorkflowMessenger() != null) {
			if(projectInfoMessenger.getWorkflowMessenger().getStartDate() != null) {
				workflow.setStartDate(new Date(projectInfoMessenger.getWorkflowMessenger().getStartDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getEndDate() != null) {
				if(projectInfoMessenger.getWorkflowMessenger().getStartDate() != null) {
					if(projectInfoMessenger.getWorkflowMessenger().getEndDate() <= projectInfoMessenger.getWorkflowMessenger().getStartDate()){
						return new GenericResponse(Status.InvalidDate, null, "Invalid start/end date");
					}
				}
				workflow.setEndDate(new Date(projectInfoMessenger.getWorkflowMessenger().getEndDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getReminderDate() != null) {
				if(projectInfoMessenger.getWorkflowMessenger().getStartDate() != null) {
					if(projectInfoMessenger.getWorkflowMessenger().getReminderDate() < projectInfoMessenger.getWorkflowMessenger().getStartDate()){
						return new GenericResponse(Status.InvalidDate, null, "Reminder date earlier than start date");
					}
				}
				
				if(projectInfoMessenger.getWorkflowMessenger().getEndDate() != null) {
					if(projectInfoMessenger.getWorkflowMessenger().getReminderDate() > projectInfoMessenger.getWorkflowMessenger().getEndDate())
						return new GenericResponse(Status.InvalidDate, null, "Reminder date later than end date");
				}
				
				workflow.setReminderDate(new Date(projectInfoMessenger.getWorkflowMessenger().getReminderDate()));
			}
		}
		
		this.create(project, workflow, projectInfoMessenger.getProjectMessenger().getShape());
		return new GenericResponse(Status.Success, null, "Ok");
	}
	
	@Transactional(readOnly = true)
	public Set<String> listProjectClients(Principal principal) throws Exception {
		return projectDao.findClientOfPrincipal(principal);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void update(Project project, Workflow workflow, boolean projectStatusUpdate, boolean workflowStatusUpdate, String shapeGeometry) throws Exception {
		
		Project oldProject = projectDao.read(project.getId());
		if(oldProject == null) throw new Exception("Project" + project.getId() + " not found");
		if(project.getDescription() != null) oldProject.setDescription(project.getDescription());
		if(project.getName() != null) oldProject.setName(project.getName());
		if(project.getClient() != null) oldProject.setClient(project.getClient());
		if(projectStatusUpdate == true) oldProject.setStatus(project.getStatus());
		
		List<Workflow> oldProjectWorkflows = workflowDao.getByProject(project); 
		if(oldProjectWorkflows == null || oldProjectWorkflows.isEmpty()) throw new Exception("No associated workflow were found for project " + project.getId());
		
		Workflow oldProjectSingleWorkflow = oldProjectWorkflows.get(0); //TODO only a single workflow is currently supported
		if(workflow.getDescription() != null) oldProjectSingleWorkflow.setDescription(workflow.getDescription());
		if(workflow.getName() != null) oldProjectSingleWorkflow.setName(workflow.getName());
		if(workflow.getStartDate() != null) oldProjectSingleWorkflow.setStartDate(workflow.getStartDate());
		if(workflow.getEndDate() != null) oldProjectSingleWorkflow.setEndDate(workflow.getEndDate());
		if(workflow.getReminderDate() != null) oldProjectSingleWorkflow.setReminderDate(workflow.getReminderDate());
		
		if(workflowStatusUpdate == true) { //TODO disallow manual workflow status update
			oldProjectSingleWorkflow.setStatus(workflow.getStatus());
			oldProjectSingleWorkflow.setStatusDate(new Date());
		}
		
		if(shapeGeometry != null) {
			Shape shape = null;
			if(project.getShape() == null) {
				shape = shapeManager.createFromGeometry(oldProject, shapeGeometry);
				oldProject.setShape(shape.getId());
			}else {
				shapeManager.updateGeometry(project.getShape(), shapeGeometry);
				shape = shapeManager.findShapeById(project.getShape());
			}
			createProjectAttributes(project, shape);
		}
		if(project.getStatus() != null && project.getStatus() != ProjectStatus.DELETED) oldProject.setStatus(project.getStatus()); //special status deleted assigned only upon deletion
		
		workflowDao.update(oldProjectSingleWorkflow);
		projectDao.update(oldProject);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public GenericResponse update(ProjectInfoMessenger projectInfoMessenger) throws Exception {
		
		if(projectInfoMessenger == null || projectInfoMessenger.getProjectMessenger() == null || projectInfoMessenger.getProjectMessenger().getId() == null) {
			log.error("Project " + (projectInfoMessenger != null && projectInfoMessenger.getProjectMessenger() != null? "id " : "") + "not provided");
			return new GenericResponse(GenericResponse.Status.Failure, null,
					"Project " + (projectInfoMessenger != null && projectInfoMessenger.getProjectMessenger() != null? "id " : "") + "not provided");
		}
		
		Project previousProject = this.findById(projectInfoMessenger.getProjectMessenger().getId(), true, true);
		if(previousProject == null) {
			log.error("Project " + projectInfoMessenger.getProjectMessenger().getId() + " does not exist");
			return new GenericResponse(GenericResponse.Status.NotFound, null, "Project " + projectInfoMessenger.getProjectMessenger().getId() + " does not exist");
		}
		
		Workflow previousWorkflow = this.getWorkflowsOfProject(previousProject).get(0); //TODO single workflow
		
		Project project = new Project();
		boolean pStatusUpdate = false;
		project.setId(UUID.fromString(projectInfoMessenger.getProjectMessenger().getId()));
		if(projectInfoMessenger.getProjectMessenger().getName() != null) project.setName(HtmlUtils.htmlEscape(projectInfoMessenger.getProjectMessenger().getName().trim()));
		if(projectInfoMessenger.getProjectMessenger().getDescription() != null) project.setDescription(HtmlUtils.htmlEscape(projectInfoMessenger.getProjectMessenger().getDescription().trim()));
		if(projectInfoMessenger.getProjectMessenger().getStatus() != null) {
			pStatusUpdate = true;
			project.setStatus(projectInfoMessenger.getProjectMessenger().getStatus());
		}
		if(projectInfoMessenger.getProjectMessenger().getClient() != null) {
			project.setClient(HtmlUtils.htmlEscape(projectInfoMessenger.getProjectMessenger().getClient().trim()));
		}
		
		Workflow workflow = null;
		boolean wStatusUpdate = false;
		if(projectInfoMessenger.getWorkflowMessenger() != null) {
			workflow = new Workflow();
			if(projectInfoMessenger.getWorkflowMessenger().getDescription() != null)
				workflow.setDescription(HtmlUtils.htmlEscape(projectInfoMessenger.getWorkflowMessenger().getDescription().trim()));
			if(projectInfoMessenger.getWorkflowMessenger().getName() != null)
			workflow.setName(HtmlUtils.htmlEscape(projectInfoMessenger.getWorkflowMessenger().getName().trim()));

			if(projectInfoMessenger.getWorkflowMessenger().getStartDate() != null){
				workflow.setStartDate(new Date(projectInfoMessenger.getWorkflowMessenger().getStartDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getEndDate() != null) {
				if(projectInfoMessenger.getWorkflowMessenger().getStartDate() != null) {
					if(projectInfoMessenger.getWorkflowMessenger().getEndDate() <= projectInfoMessenger.getWorkflowMessenger().getStartDate()){
						return new GenericResponse(Status.InvalidDate, null, "Invalid start/end date");
					}
				}else if(previousWorkflow.getStartDate() != null && projectInfoMessenger.getWorkflowMessenger().getEndDate() < previousWorkflow.getEndDate().getTime()){
					return new GenericResponse(Status.InvalidDate, null, "Invalid start/end date");
				}
				workflow.setEndDate(new Date(projectInfoMessenger.getWorkflowMessenger().getEndDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getReminderDate() != null) {
				if(projectInfoMessenger.getWorkflowMessenger().getStartDate() != null) {
					if(projectInfoMessenger.getWorkflowMessenger().getReminderDate() < projectInfoMessenger.getWorkflowMessenger().getStartDate()){
						return new GenericResponse(Status.InvalidDate, null, "Reminder date earlier than start date");
					}
				}else if(previousWorkflow.getStartDate() != null && projectInfoMessenger.getWorkflowMessenger().getReminderDate() < previousWorkflow.getStartDate().getTime())
					return new GenericResponse(Status.InvalidDate, null, "Reminder date earlier than start date");
				
				if(projectInfoMessenger.getWorkflowMessenger().getEndDate() != null)
				{
					if(projectInfoMessenger.getWorkflowMessenger().getReminderDate() > projectInfoMessenger.getWorkflowMessenger().getEndDate())
						return new GenericResponse(Status.InvalidDate, null, "Reminder date later than end date");
				}else if(previousWorkflow.getEndDate() != null && projectInfoMessenger.getWorkflowMessenger().getReminderDate() > previousWorkflow.getEndDate().getTime())
					return new GenericResponse(Status.InvalidDate, null, "Reminder date later than end date");
				
					
				workflow.setReminderDate(new Date(projectInfoMessenger.getWorkflowMessenger().getReminderDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getStartDate()!= null){
				workflow.setStartDate(new Date(projectInfoMessenger.getWorkflowMessenger().getStartDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getEndDate()!= null){
				workflow.setEndDate(new Date(projectInfoMessenger.getWorkflowMessenger().getEndDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getReminderDate()!= null){
				workflow.setReminderDate(new Date(projectInfoMessenger.getWorkflowMessenger().getReminderDate()));
			}
			if(projectInfoMessenger.getWorkflowMessenger().getStatus() != null) {
				workflow.setStatus(projectInfoMessenger.getWorkflowMessenger().getStatus());
				wStatusUpdate = true;
			}
		}
		this.update(project, workflow, pStatusUpdate, wStatusUpdate, projectInfoMessenger.getProjectMessenger().getShape());
		return new GenericResponse(Status.Success, null, "Ok");
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void delete(String projectId, boolean purge) throws Exception {
		
		if(projectId == null) throw new Exception("Missing project id");
		Project oldProject = projectDao.read(UUID.fromString(projectId));
		if(oldProject == null) throw new Exception("Project " + projectId + " does not exist");
		
		if(purge){
			shapeManager.delete(Collections.singletonList(oldProject.getShape().toString()));
		}
		
		if(!purge) {
			oldProject.setStatus(ProjectStatus.DELETED);
			projectDao.update(oldProject);
			return;
		}
		
		List<Document> oldProjectDocuments = documentDao.getDocumentsOfProject(oldProject);
		List<Workflow> workflows = getWorkflowsOfProject(oldProject);
		for(Workflow workflow : workflows) {
			List<WorkflowTask> workflowTasks = workflowDao.getWorkflowTasks(workflow);
			for(WorkflowTask workflowTask : workflowTasks) {
				workflowTaskDocumentDao.deleteByWorkflowTask(workflowTask);
			}
		}
		projectDocumentDao.deleteByProject(oldProject);
		
		//TODO we need to see what are we goind to do with the data repository
		for(Document oldProjectDocument : oldProjectDocuments) {
			long tasks = documentDao.countWorkflowTasksOfDocument(oldProject, oldProjectDocument);
/*			if(tasks == 0)
			{
				repository.delete(d.getId().toString());
				documentDao.delete(d);
			}*/
			//TODO needed if multi-project document support is added
//			List<Project> ps = documentDao.findProjectsOfDocument(d);
//			if(ps == null || ps.isEmpty())
//			{
//				repository.delete(d.getId().toString());
//				documentDao.delete(d);
//			}
		}
		
		projectTermDao.deleteByProject(oldProject);
		projectDao.delete(oldProject);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void createTask(Project project, WorkflowTask workflowTask) throws Exception {
		
		if(workflowTask.getStartDate() != null && workflowTask.getStartDate().getTime() <= new Date().getTime()){
			workflowTask.setStatus(WorkflowTaskStatus.ACTIVE);
		}else {
			workflowTask.setStatus(WorkflowTaskStatus.INACTIVE);
		}
		
		workflowTask.setStatusDate(new Date());
		if(project.getCreator() == null) throw new Exception("No creator assigned to workflow task");
		if(workflowTask.getCritical() == null){
			workflowTask.setCritical(Criticality.NONBLOCKING);
		}
		
		if(workflowTask.getWorkflow() != null) {
			
			List<Workflow> workflows = getWorkflowsOfProject(project);
			boolean found = false;
			for(Workflow workflow : workflows) {
				if(workflow.getId().equals(workflowTask.getWorkflow().getId())) {
					found = true;
					break;
				}
			}
			if(found == false) {
				throw new Exception("Specified workflow does not belong to the project");
			}
		}else {
			workflowTask.setWorkflow(getWorkflowsOfProject(project).get(0)); //TODO single workflow
		}
		
		workflowTaskDao.create(workflowTask);
	}
	
	private List<WorkflowTask> checkWorkflowCompletion(Workflow workflow) throws Exception {
		
		List<WorkflowTask> workflowTasksBlockers = new ArrayList<WorkflowTask>();
		if(workflow.getStatus() == WorkflowStatus.ACTIVE || workflow.getStatus() == WorkflowStatus.INACTIVE) {
			
			List<WorkflowTask> workflowTaskPotentialBlockers = workflowDao.getWorkflowTasks(workflow, Criticality.BLOCKING, WorkflowTaskStatus.ACTIVE);
			workflowTaskPotentialBlockers.addAll(workflowDao.getWorkflowTasks(workflow, Criticality.BLOCKING, WorkflowTaskStatus.INACTIVE));
			workflowTaskPotentialBlockers.addAll(workflowDao.getWorkflowTasks(workflow, Criticality.CRITICAL));

			for(WorkflowTask workflowTaskPotentialBlocker : workflowTaskPotentialBlockers) {
				if(workflowTaskPotentialBlocker.getStatus() != WorkflowTaskStatus.CANCELLED && workflowTaskPotentialBlocker.getStatus() != WorkflowTaskStatus.COMPLETED){
					workflowTasksBlockers.add(workflowTaskPotentialBlocker);
				}
			}
		}
		return workflowTasksBlockers;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<WorkflowTask> updateTask(Project project, WorkflowTask workflowTask) throws Exception {
		return updateTask(project, workflowTask, true);
	}
	
	private List<WorkflowTask> updateTask(Project project, WorkflowTask workflowTask, boolean checkWorkflowCompletion) throws Exception {
		
		List<WorkflowTask> workflowTasksBlockers = null;
		WorkflowTask oldWorkflowTask = workflowTaskDao.read(workflowTask.getId());
		if(oldWorkflowTask == null) throw new Exception("Workflow task" + workflowTask.getId() + " not found");
		
		if(workflowTask.getName() != null) oldWorkflowTask.setName(workflowTask.getName());
		if(workflowTask.getStartDate() != null) oldWorkflowTask.setStartDate(workflowTask.getStartDate());
		if(workflowTask.getEndDate() != null) oldWorkflowTask.setEndDate(workflowTask.getEndDate());
		if(workflowTask.getReminderDate() != null) oldWorkflowTask.setReminderDate(workflowTask.getReminderDate());
		
		if(workflowTask.getCritical() != null && workflowTask.getCritical() != oldWorkflowTask.getCritical()) {
			if(workflowTask.getCritical() != Criticality.CRITICAL && oldWorkflowTask.getCritical() == Criticality.CRITICAL) {
				
				Workflow oldWorkflow = findWorkflowById(oldWorkflowTask.getWorkflow().getId().toString(), false);
				oldWorkflow.setStatus(WorkflowStatus.ACTIVE);
				oldWorkflow.setStatusDate(new Date());
				workflowDao.update(oldWorkflow);
			}
			oldWorkflowTask.setCritical(workflowTask.getCritical());
		}
		
		if(workflowTask.getStatus() != null && workflowTask.getStatus() != oldWorkflowTask.getStatus()) {
			
			long now = new Date().getTime();
			if(workflowTask.getStatus() == WorkflowTaskStatus.INACTIVE && oldWorkflowTask.getStartDate().getTime() <= now && 
					(oldWorkflowTask.getEndDate() != null ? oldWorkflowTask.getEndDate().getTime() >= now : true)){
				
				workflowTask.setStatus(WorkflowTaskStatus.ACTIVE); //TODO issue warning
			}
			else if(workflowTask.getStatus() == WorkflowTaskStatus.ACTIVE && (oldWorkflowTask.getStartDate().getTime() > now || 
					(oldWorkflowTask.getEndDate() != null ? oldWorkflowTask.getEndDate().getTime() < now : false))) {
				
				workflowTask.setStatus(WorkflowTaskStatus.INACTIVE); //TODO issue warning
			}
			
			oldWorkflowTask.setStatus(workflowTask.getStatus());
			oldWorkflowTask.setStatusDate(new Date());
			
			if(workflowTask.getStatus() == WorkflowTaskStatus.ACTIVE) {
				
				Workflow oldWorkflow = findWorkflowById(oldWorkflowTask.getWorkflow().getId().toString(), false);
				oldWorkflow.setStatus(WorkflowStatus.ACTIVE);
				oldWorkflow.setStatusDate(new Date());
				workflowDao.update(oldWorkflow);
			}else if(workflowTask.getStatus() == WorkflowTaskStatus.COMPLETED && checkWorkflowCompletion) {
				
				Workflow oldWorkflow = findWorkflowById(oldWorkflowTask.getWorkflow().getId().toString(), false);
				workflowTasksBlockers = checkWorkflowCompletion(oldWorkflow);
				if(workflowTasksBlockers.isEmpty()) {
					
					oldWorkflow.setStatus(WorkflowStatus.COMPLETED);
					oldWorkflow.setStatusDate(new Date());
					workflowDao.update(oldWorkflow);
				}
			}
			if(workflowTask.getCritical() == Criticality.CRITICAL) {
				
				Workflow oldWorkflow = this.findWorkflowById(oldWorkflowTask.getWorkflow().getId().toString(), false);
				switch(workflowTask.getStatus()) {
					case CANCELLED:
						oldWorkflow.setStatus(WorkflowStatus.CANCELLED);
						break;
					case INACTIVE:
						oldWorkflow.setStatus(WorkflowStatus.INACTIVE); //TODO should workflow become inactive even if there are active non-critical tasks?
						break;
					default:
						break;
				}
				oldWorkflow.setStatusDate(new Date());
				workflowDao.update(oldWorkflow);
			}
		}
		
		workflowTaskDao.update(oldWorkflowTask);
		return workflowTasksBlockers;
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteTask(String taskId) throws Exception {
		
		if(taskId == null) throw new Exception("Missing task id");
		WorkflowTask oldWorkflowTask = workflowTaskDao.read(UUID.fromString(taskId));
		if(oldWorkflowTask == null) throw new Exception("Task " + taskId + " not found");
		
		workflowTaskDocumentDao.deleteByWorkflowTask(oldWorkflowTask);
		List<Document> oldWorkflowDocuments = documentDao.getDocumentsOfWorkflowTask(oldWorkflowTask);
		for(Document oldWorkflowDocument : oldWorkflowDocuments) {
			long tasks = documentDao.countWorkflowTasksOfDocument(oldWorkflowTask.getWorkflow().getProject(), oldWorkflowDocument);
			/*if(tasks == 0)
			{
				repository.delete(d.getId().toString());
				documentDao.delete(d);
			}*/
			//TODO needed if multi-project document support is added
//			List<Project> ps = documentDao.findProjectsOfDocument(d);
//			if(ps == null || ps.isEmpty())
//			{
//				repository.delete(d.getId().toString());
//				documentDao.delete(d);
//			}
		}
		
		if(oldWorkflowTask.getCritical() == Criticality.BLOCKING || oldWorkflowTask.getCritical() == Criticality.CRITICAL) {
			
			if(checkWorkflowCompletion(oldWorkflowTask.getWorkflow()).isEmpty()) {
				
				Workflow oldWorkflow = findWorkflowById(oldWorkflowTask.getWorkflow().getId().toString(), false);
				oldWorkflow.setStatus(WorkflowStatus.COMPLETED);
				oldWorkflow.setStatusDate(new Date());
				workflowDao.update(oldWorkflow);
			}else if(oldWorkflowTask.getCritical() == Criticality.CRITICAL && 
					oldWorkflowTask.getStatus() != WorkflowTaskStatus.ACTIVE && oldWorkflowTask.getStatus() != WorkflowTaskStatus.COMPLETED) {
				
				Workflow oldWorkflow = findWorkflowById(oldWorkflowTask.getWorkflow().getId().toString(), false);
				oldWorkflow.setStatus(WorkflowStatus.ACTIVE);
				oldWorkflow.setStatusDate(new Date());
				workflowDao.update(oldWorkflow);
			}
		}
		workflowTaskDao.delete(oldWorkflowTask);
	}
	
	@Transactional(readOnly = true)
	public List<String> listProjects() throws Exception {
		return projectDao.listProjects();
	}
	
	@Transactional(readOnly = true)
	public List<String> listProjectsOfCreator(Principal principal) throws Exception {
		return projectDao.listProjectsOfCreator(principal);
	}
	
	@Transactional(readOnly = true)
	public List<String> listProjectsOfCustomer(Tenant tenant) throws Exception {
		return projectDao.listProjectsOfTenant(tenant);
	}
	
	@Transactional(readOnly = true)
	public List<Project> searchProjects(List<String> searchTerms) throws Exception {
		return projectDao.searchProjects(searchTerms);
	}
	
	@Transactional(readOnly = true)
	public List<ProjectInfo> searchProjectsInfo(List<String> searchTerms) throws Exception {
		return getInfo(searchProjects(searchTerms));
	}
	
	@Transactional(readOnly = true)
	public List<Project> searchProjectsOfCreator(List<String> searchTerms, Principal principal) throws Exception {
		return projectDao.searchProjectsOfCreator(searchTerms, principal);
	}
	
	@Transactional(readOnly = true)
	public List<ProjectInfo> searchProjectsOfCreatorInfo(List<String> searchTerms, Principal principal) throws Exception {
		return getInfo(searchProjectsOfCreator(searchTerms, principal));
	}
	
	@Transactional(readOnly = true)
	public List<Project> searchProjectsOfCustomer(List<String> searchTerms, Tenant tenant) throws Exception {
		return projectDao.searchProjectsOfTenant(searchTerms, tenant);
	}
	
	@Transactional(readOnly = true)
	public List<ProjectInfo> searchProjectsOfCustomerInfo(List<String> searchTerms, Tenant tenant) throws Exception {
		return getInfo(searchProjectsOfCustomer(searchTerms, tenant));
	}
	
	@Transactional(readOnly = true)
	public List<Workflow> getWorkflowsOfProject(Project project) throws Exception {
		return projectDao.getWorkflowsOfProject(project);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void delete(List<UUID> projectsUUID) throws Exception {
		
		for(UUID projectUUID : projectsUUID) {
			Project project = projectDao.read(projectUUID);
			if(project == null) throw new Exception("Project " + project + " not found");
			
			//List<UUID> documentsToDelete = new ArrayList<UUID>();
			
			//List<ProjectDocument> pds = documentDao.getDocumentsOfProject(p);
			
			//delete all documents of the project
			//for(ProjectDocument pd : pds)
			//	documentManager.delete(Collections.singletonList(pd.getDocument().getId()));
			//projectDocumentDao.deleteByProject(p);
			
			project.setStatus(ProjectStatus.DELETED);
			projectDao.update(project);
			
			//delete all workflows of the project
//			List<Workflow> workflows = getWorkflowsOfProject(p);
//			for(Workflow w : workflow)
//			{
//				workflowTaskDao.deleteByWorkflow(w);
//				workflowDao.delete(w);
//			}
		}
	}
	
}

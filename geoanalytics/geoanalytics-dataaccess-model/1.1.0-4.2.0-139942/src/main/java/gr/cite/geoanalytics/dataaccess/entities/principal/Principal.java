package gr.cite.geoanalytics.dataaccess.entities.principal;

import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.annotation.Annotation;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.MimeType;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectDocument;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeTerm;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocument;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name="\"Principal\"")
public class Principal implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable{

	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"PRCP_ID\"", nullable = false)
	private UUID id = null;
	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"PRCP_Class\"", nullable = false)
	private UUID classId = null;
	
	@Column(name="\"PRCP_Name\"", nullable = false, length = 250)
	private String name = null;
	
	@Column(name="\"PRCP_URI\"", nullable = true, length = 250)
	private String uri = null;
	
	@Column(name="\"PRCP_DataURL\"", nullable = true, length = 500)
	private String dataUrl = null;
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name="\"PRCP_Metadata\"", columnDefinition = "xml") //DEPWARN possible db portability issue
	private String metadata = null;
	
	@Column(name="\"PRCP_IsActive\"", nullable = false)
	private short isActive = 0;
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name="\"PRCP_ProviderDefinition\"", columnDefinition = "xml") //DEPWARN possible db portability issue
	private String providerDefinition = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PRCP_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"PRCP_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name="\"PRCP_Tenant\"")
	private Tenant tenant = null;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name="\"PRCP_Creator\"")
	private Principal creator = null;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name="\"PRCP_PrincipalData\"")
	private PrincipalData principalData = null;
	
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<PrincipalMembership> membersprincipal= new HashSet<PrincipalMembership>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<PrincipalMembership> groupsPrincipal = new HashSet<PrincipalMembership>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "principal", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Accounting> accountingsPrincipal = new HashSet<Accounting>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Accounting> accountingsCreator = new HashSet<Accounting>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Annotation> annotationsCreator = new HashSet<Annotation>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "principal", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Auditing> auditingsPrincipal = new HashSet<Auditing>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Auditing> auditingsCreator = new HashSet<Auditing>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Document> documentsCreator = new HashSet<Document>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MimeType> mimeTypesCreator = new HashSet<MimeType>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Project> projectsCreator = new HashSet<Project>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<ProjectDocument> projectDocumentsCreator = new HashSet<ProjectDocument>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<ProjectDocument> projectTermsCreator = new HashSet<ProjectDocument>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Shape> shapesCreator = new HashSet<Shape>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<ShapeDocument> shapeDocumentsCreator = new HashSet<ShapeDocument>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<ShapeImport> shapeImportsCreator = new HashSet<ShapeImport>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<ShapeTerm> shapeTermsCreator = new HashSet<ShapeTerm>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<SysConfig> sysConfigsCreator = new HashSet<SysConfig>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<TaxonomyTerm> taxonomyTermsCreator = new HashSet<TaxonomyTerm>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<TaxonomyTermLink> taxonomyTermLinksCreator = new HashSet<TaxonomyTermLink>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<TaxonomyTermShape> taxonomyTermShapesCreator = new HashSet<TaxonomyTermShape>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Tenant> tenantsCreator = new HashSet<Tenant>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<TenantActivation> tenantActivationsCreator = new HashSet<TenantActivation>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Workflow> workflowsCreator = new HashSet<Workflow>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<WorkflowTask> workflowTaskCreator = new HashSet<WorkflowTask>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "principal", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<WorkflowTask> workflowTaskPrincipal = new HashSet<WorkflowTask>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<WorkflowTaskDocument> workflowTaskDocumentCreator = new HashSet<WorkflowTaskDocument>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Principal> creators = new HashSet<Principal>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<PrincipalProject> projectsParticipant = new HashSet<PrincipalProject>(0);

	public Set<PrincipalProject> getProjectsParticipant() {
		return projectsParticipant;
	}

	public void setProjectsParticipant(Set<PrincipalProject> projectsParticipant) {
		this.projectsParticipant = projectsParticipant;
	}

	public void setIsActive(short isActive) {
		this.isActive = isActive;
	}

	public Set<PrincipalMembership> getMembersprincipal() {
		return membersprincipal;
	}

	public void setMembersprincipal(Set<PrincipalMembership> membersprincipal) {
		this.membersprincipal = membersprincipal;
	}

	public Set<PrincipalMembership> getGroupsPrincipal() {
		return groupsPrincipal;
	}

	public void setGroupsPrincipal(Set<PrincipalMembership> groupsPrincipal) {
		this.groupsPrincipal = groupsPrincipal;
	}

	public Set<Accounting> getAccountingsPrincipal() {
		return accountingsPrincipal;
	}

	public void setAccountingsPrincipal(Set<Accounting> accountingsPrincipal) {
		this.accountingsPrincipal = accountingsPrincipal;
	}

	public Set<Accounting> getAccountingsCreator() {
		return accountingsCreator;
	}

	public void setAccountingsCreator(Set<Accounting> accountingsCreator) {
		this.accountingsCreator = accountingsCreator;
	}

	public Set<Annotation> getAnnotationsCreator() {
		return annotationsCreator;
	}

	public void setAnnotationsCreator(Set<Annotation> annotationsCreator) {
		this.annotationsCreator = annotationsCreator;
	}

	public Set<Auditing> getAuditingsPrincipal() {
		return auditingsPrincipal;
	}

	public void setAuditingsPrincipal(Set<Auditing> auditingsPrincipal) {
		this.auditingsPrincipal = auditingsPrincipal;
	}

	public Set<Auditing> getAuditingsCreator() {
		return auditingsCreator;
	}

	public void setAuditingsCreator(Set<Auditing> auditingsCreator) {
		this.auditingsCreator = auditingsCreator;
	}

	public Set<Document> getDocumentsCreator() {
		return documentsCreator;
	}

	public void setDocumentsCreator(Set<Document> documentsCreator) {
		this.documentsCreator = documentsCreator;
	}

	public Set<MimeType> getMimeTypesCreator() {
		return mimeTypesCreator;
	}

	public void setMimeTypesCreator(Set<MimeType> mimeTypesCreator) {
		this.mimeTypesCreator = mimeTypesCreator;
	}

	public Set<Project> getProjectsCreator() {
		return projectsCreator;
	}

	public void setProjectsCreator(Set<Project> projectsCreator) {
		this.projectsCreator = projectsCreator;
	}

	public Set<ProjectDocument> getProjectDocumentsCreator() {
		return projectDocumentsCreator;
	}

	public void setProjectDocumentsCreator(Set<ProjectDocument> projectDocumentsCreator) {
		this.projectDocumentsCreator = projectDocumentsCreator;
	}

	public Set<ProjectDocument> getProjectTermsCreator() {
		return projectTermsCreator;
	}

	public void setProjectTermsCreator(Set<ProjectDocument> projectTermsCreator) {
		this.projectTermsCreator = projectTermsCreator;
	}

	public Set<Shape> getShapesCreator() {
		return shapesCreator;
	}

	public void setShapesCreator(Set<Shape> shapesCreator) {
		this.shapesCreator = shapesCreator;
	}

	public Set<ShapeDocument> getShapeDocumentsCreator() {
		return shapeDocumentsCreator;
	}

	public void setShapeDocumentsCreator(Set<ShapeDocument> shapeDocumentsCreator) {
		this.shapeDocumentsCreator = shapeDocumentsCreator;
	}

	public Set<ShapeImport> getShapeImportsCreator() {
		return shapeImportsCreator;
	}

	public void setShapeImportsCreator(Set<ShapeImport> shapeImportsCreator) {
		this.shapeImportsCreator = shapeImportsCreator;
	}

	public Set<ShapeTerm> getShapeTermsCreator() {
		return shapeTermsCreator;
	}

	public void setShapeTermsCreator(Set<ShapeTerm> shapeTermsCreator) {
		this.shapeTermsCreator = shapeTermsCreator;
	}

	public Set<SysConfig> getSysConfigsCreator() {
		return sysConfigsCreator;
	}

	public void setSysConfigsCreator(Set<SysConfig> sysConfigsCreator) {
		this.sysConfigsCreator = sysConfigsCreator;
	}

	public Set<TaxonomyTerm> getTaxonomyTermsCreator() {
		return taxonomyTermsCreator;
	}

	public void setTaxonomyTermsCreator(Set<TaxonomyTerm> taxonomyTermsCreator) {
		this.taxonomyTermsCreator = taxonomyTermsCreator;
	}

	public Set<TaxonomyTermLink> getTaxonomyTermLinksCreator() {
		return taxonomyTermLinksCreator;
	}

	public void setTaxonomyTermLinksCreator(Set<TaxonomyTermLink> taxonomyTermLinksCreator) {
		this.taxonomyTermLinksCreator = taxonomyTermLinksCreator;
	}

	public Set<TaxonomyTermShape> getTaxonomyTermShapesCreator() {
		return taxonomyTermShapesCreator;
	}

	public void setTaxonomyTermShapesCreator(Set<TaxonomyTermShape> taxonomyTermShapesCreator) {
		this.taxonomyTermShapesCreator = taxonomyTermShapesCreator;
	}

	public Set<Tenant> getTenantsCreator() {
		return tenantsCreator;
	}

	public void setTenantsCreator(Set<Tenant> tenantsCreator) {
		this.tenantsCreator = tenantsCreator;
	}

	public Set<TenantActivation> getTenantActivationsCreator() {
		return tenantActivationsCreator;
	}

	public void setTenantActivationsCreator(Set<TenantActivation> tenantActivationsCreator) {
		this.tenantActivationsCreator = tenantActivationsCreator;
	}

	public Set<Workflow> getWorkflowsCreator() {
		return workflowsCreator;
	}

	public void setWorkflowsCreator(Set<Workflow> workflowsCreator) {
		this.workflowsCreator = workflowsCreator;
	}

	public Set<WorkflowTask> getWorkflowTaskCreator() {
		return workflowTaskCreator;
	}

	public void setWorkflowTaskCreator(Set<WorkflowTask> workflowTaskCreator) {
		this.workflowTaskCreator = workflowTaskCreator;
	}

	public Set<WorkflowTask> getWorkflowTaskPrincipal() {
		return workflowTaskPrincipal;
	}

	public void setWorkflowTaskPrincipal(Set<WorkflowTask> workflowTaskPrincipal) {
		this.workflowTaskPrincipal = workflowTaskPrincipal;
	}

	public Set<WorkflowTaskDocument> getWorkflowTaskDocumentCreator() {
		return workflowTaskDocumentCreator;
	}

	public void setWorkflowTaskDocumentCreator(Set<WorkflowTaskDocument> workflowTaskDocumentCreator) {
		this.workflowTaskDocumentCreator = workflowTaskDocumentCreator;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public PrincipalData getPrincipalData() {
		return principalData;
	}

	public void setPrincipalData(PrincipalData principalData) {
		this.principalData = principalData;
	}

	public Set<Principal> getCreators() {
		return creators;
	}

	public void setCreators(Set<Principal> creators) {
		this.creators = creators;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getClassId() {
		return classId;
	}

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	public void setClassId(UUID classId) {
		this.classId = classId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public boolean isActive() {
		return isActive == ActiveStatus.ACTIVE.code();
	}
	
	public ActiveStatus getIsActive() {
		return ActiveStatus.fromCode(this.isActive);
	}
	
	public void setIsActive(ActiveStatus isActive)  {
		this.isActive = isActive.code();
	}

	public String getProviderDefinition() {
		return providerDefinition;
	}

	public void setProviderDefinition(String providerDefinition) {
		this.providerDefinition = providerDefinition;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Principal other = (Principal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

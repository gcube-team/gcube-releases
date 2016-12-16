package gr.cite.geoanalytics.dataaccess.entities.tenant;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.annotation.Annotation;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

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
@Table(name="\"Tenant\"")
public class Tenant implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"TEN_ID\"", nullable = false)
	private UUID id = null;
	
	@Column(name="\"TEN_Name\"", nullable = false, length = 100)
	private String name = null;
	
	@Column(name="\"TEN_Code\"", nullable = false, length = 20)
	private String code = null;
	
	@Column(name="\"TEN_EmailAddress\"", length = 250)
	private String email = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"TEN_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"TEN_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="\"TEN_Creator\"", nullable = false)
	private Principal creator = null;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Principal> principals = new HashSet<Principal>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Annotation> annotations = new HashSet<Annotation>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Auditing> auditings = new HashSet<Auditing>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Document> documents = new HashSet<Document>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Project> projects = new HashSet<Project>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<TenantActivation> tenantActivations = new HashSet<TenantActivation>(0);
	
	public Set<Principal> getPrincipals() {
		return principals;
	}

	public void setPrincipals(Set<Principal> principals) {
		this.principals = principals;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	public Set<Auditing> getAuditings() {
		return auditings;
	}

	public void setAuditings(Set<Auditing> auditings) {
		this.auditings = auditings;
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	public Set<TenantActivation> getTenantActivations() {
		return tenantActivations;
	}

	public void setTenantActivations(Set<TenantActivation> tanantActivations) {
		this.tenantActivations = tanantActivations;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String eMail)
	{
		this.email = eMail;
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	public Date getLastUpdate()
	{
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate)
	{
		this.lastUpdate = lastUpdate;
	}

	public Principal getCreator()
	{
		return creator;
	}

	public void setCreator(Principal creator)
	{
		this.creator = creator;
	}
	
	@Override
	public String toString()
	{
		return "Customer(" + "id=" + getId() + " name=" + getName() + 
				" code=" + getCode() + " eMailAddress=" + getEmail() +
				" creation=" + getCreationDate() + "lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null);
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
		Tenant other = (Tenant) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}

package gr.cite.geoanalytics.dataaccess.entities.taxonomy;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Index;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"Taxonomy\"")
public class Taxonomy implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable
{

	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"TAX_ID\"", nullable = false)
	private UUID id = null;

	@Column(name = "\"TAX_Name\"", nullable = false, length = 250)
	private String name = null;

	@Column(name = "\"TAX_IsUserTaxonomy\"", nullable = false)
	private short isUserTaxonomy = 0;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAX_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAX_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@Column(name = "\"TAX_IsActive\"", nullable = false)
	private Short isActive = 1;

	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "\"TAX_ExtraData\"", columnDefinition = "xml", nullable=true) //DEPWARN possible db portability issue
	private String extraData = null;
	
	@ManyToOne(fetch = FetchType.LAZY) //TODO nullable?
	@JoinColumn(name = "\"TAX_Class\"")
	private Taxonomy taxonomyClass;
	
	@ManyToOne
	@JoinColumn(name = "\"TAX_Creator\"", nullable = false)
	private Principal creator = null;
	
	public Taxonomy() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsUserTaxonomy() {
		return isUserTaxonomy == 0 ? false : true;
	}

	public void setIsUserTaxonomy(boolean isUserTaxonomy) {
		this.isUserTaxonomy = (short)(isUserTaxonomy == true ? 1 : 0);
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
	
	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
	
	public String getExtraData() {
		return extraData;
	}
	
	public boolean getIsActive() {
		if(isActive == null) return true;
		return isActive == 0 ? false : true;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = (short)(isActive == true ? 1 : 0);
	}
	
	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	public Taxonomy getTaxonomyClass() {
		return taxonomyClass;
	}

	public void setTaxonomyClass(Taxonomy taxonomy) {
		this.taxonomyClass = taxonomy;
	}

	@Override
	public String toString()
	{
		return "Taxonomy(" + "id=" + getId() + " name=" + getName() + 
				" isUserTaxonomy=" + getIsUserTaxonomy() +
				" taxonomyClass=" + getTaxonomyClass() + " isActive=" + getIsActive() +
				" creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null);
	}
	
	@Override
	public boolean equals(Object other) 
	{
		if (other == this) return true;
		if (other == null || other.getClass() != this.getClass()) return false;

		if(!id.equals(((Taxonomy)other).getId())) return false;
		return true;
	}
 
	@Override
	public int hashCode() 
	{
		return id.hashCode();
	}
}

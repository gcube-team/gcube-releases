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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"TaxonomyTerm\"")
public class TaxonomyTerm implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {

	public static class FieldName {
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String TAXONOMY = "taxonomy";
		public static final String PARENT = "parent";
		public static final String TAXONOMY_TERM_CLASS = "taxonomyTermClass";
		public static final String CREATOR = "creator";
		public static final String ORDER = "order";
		public static final String IS_ACTIVE = "isActive";
		public static final String LAST_UPDATE = "lastUpdate";
		public static final String EXTRA_DATA = "extraData";
		public static final String REF_CLASS_SCHEMA = "refClassSchema";
	}
	
	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"TAXT_ID\"", nullable = false)
	private UUID id = null;

	@Column(name = "\"TAXT_Name\"", nullable = false, length = 250)
	private String name = null;

	/**
	 * Each taxonomy is associated to exactly one term
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"TAXT_Taxonomy\"", nullable = false)
	private Taxonomy taxonomy = null;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "\"TAXT_Parent\"")
	private TaxonomyTerm parent = null;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "\"TAXT_Class\"")
	private TaxonomyTerm taxonomyTermClass = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"TAXT_Creator\"", nullable=false)
	private Principal creator = null;
	
	@Column(name = "\"TAXT_Order\"", nullable = false)
	private Integer order = 0;

	@Column(name = "\"TAXT_IsActive\"", nullable = false)
	private short isActive = 1;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAXT_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAXT_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@Lob
	@Type(type = "org.hibernate.type.TextType") //DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "\"TAXT_ExtraData\"", nullable = true)
	private String extraData = null;

	/** points to SysConfig for the schema (not necessarily XSD) of the terms that 
     *  point to this term as being their class.
     */
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"TAXT_RefClassSchema\"", nullable = true)
	private UUID refClassSchema = null;  //TODO should have direct ref to sysconfig?

	public TaxonomyTerm() {
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

	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	public TaxonomyTerm getParent() {
		return parent;
	}

	public void setParent(TaxonomyTerm parent) {
		this.parent = parent;
	}

	public TaxonomyTerm getTaxonomyTermClass() {
		return taxonomyTermClass;
	}

	public void setTaxonomyTermClass(TaxonomyTerm taxonomyTermClass) {
		this.taxonomyTermClass = taxonomyTermClass;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean getIsActive() {
		return isActive == 0 ? false : true;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = (short) (isActive == true ? 1 : 0);
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

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public UUID getRefClassSchema() {
		return refClassSchema;
	}

	public void setRefClassSchema(UUID refClassSchema) {
		this.refClassSchema = refClassSchema;
	}

	@Override
	public String toString() {
		return "TaxonomyTerm(" + "id=" + getId() + " name=" + getName() + " taxonomy="
				+ (taxonomy != null ? taxonomy.getId() : null) + " parent=" + (parent != null ? parent.getId() : null)
				+ " taxonomyTermClass=" + (taxonomyTermClass != null ? taxonomyTermClass.getId() : null) + " order="
				+ getOrder() + " isActive=" + getIsActive() + " creation=" + getCreationDate() + " lastUpdate="
				+ getLastUpdate() + " creator=" + (creator != null ? creator.getId() : null) + " extraData="
				+ getExtraData();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null || other.getClass() != this.getClass())
			return false;

		if (!id.equals(((TaxonomyTerm) other).getId()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}

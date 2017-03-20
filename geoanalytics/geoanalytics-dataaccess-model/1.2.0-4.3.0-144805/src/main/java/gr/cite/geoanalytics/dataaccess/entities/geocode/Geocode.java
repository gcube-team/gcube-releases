package gr.cite.geoanalytics.dataaccess.entities.geocode;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

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
@Table(name = "\"Geocode\"")
public class Geocode implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {

	public static class FieldName {
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String GEOCODESYSTEM = "geocodeSystem";
		public static final String PARENT = "parent";
		public static final String GEOCODE_CLASS = "geocodeClass";
		public static final String CREATOR = "creator";
		public static final String ORDER = "order";
		public static final String IS_ACTIVE = "isActive";
		public static final String LAST_UPDATE = "lastUpdate";
		public static final String EXTRA_DATA = "extraData";
		public static final String REF_CLASS_SCHEMA = "refClassSchema";
	}
	
	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"GC_ID\"", nullable = false)
	private UUID id = null;

	@Column(name = "\"GC_Name\"", nullable = false, length = 250)
	private String name = null;

	/**
	 * Each geocodeSystem is associated to exactly one geocode
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"GC_GeocodeSystem\"", nullable = false)
	private GeocodeSystem geocodeSystem = null;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "\"GC_Parent\"")
	private Geocode parent = null;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "\"GC_Class\"")
	private Geocode geocodeClass = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"GC_Creator\"", nullable=false)
	private Principal creator = null;
	
	@Column(name = "\"GC_Order\"", nullable = false)
	private Integer order = 0;

	@Column(name = "\"GC_IsActive\"", nullable = false)
	private short isActive = 1;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"GC_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"GC_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@Lob
	@Type(type = "org.hibernate.type.TextType") //DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "\"GC_ExtraData\"", nullable = true)
	private String extraData = null;

	/** points to SysConfig for the schema (not necessarily XSD) of the terms that 
     *  point to this term as being their class.
     */
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"GC_RefClassSchema\"", nullable = true)
	private UUID refClassSchema = null;  //TODO should have direct ref to sysconfig?
	
	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "\"GC_Shape\"")
	private Shape shape = null;  
	
	public Geocode() {
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

	public GeocodeSystem getGeocodeSystem() {
		return geocodeSystem;
	}

	public void setGeocodeSystem(GeocodeSystem geocodeSystem) {
		this.geocodeSystem = geocodeSystem;
	}

	public Geocode getParent() {
		return parent;
	}

	public void setParent(Geocode parent) {
		this.parent = parent;
	}

	public Geocode getGeocodeClass() {
		return geocodeClass;
	}

	public void setGeocodeClass(Geocode geocodeClass) {
		this.geocodeClass = geocodeClass;
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
	
	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}
	
	@Override
	public String toString() {
		return "Geocode(" + "id=" + getId() + " name=" + getName() + " taxonomy="
				+ (geocodeSystem != null ? geocodeSystem.getId() : null) + " parent=" + (parent != null ? parent.getId() : null)
				+ " geocodeClass=" + (geocodeClass != null ? geocodeClass.getId() : null) + " order="
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

		if (!id.equals(((Geocode) other).getId()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}

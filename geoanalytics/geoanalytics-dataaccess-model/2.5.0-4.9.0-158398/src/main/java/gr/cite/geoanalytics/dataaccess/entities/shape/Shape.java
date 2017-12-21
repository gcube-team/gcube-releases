package gr.cite.geoanalytics.dataaccess.entities.shape;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.exception.SRSException;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Index;

import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Geometry;

@XmlRootElement
@Entity
@Table(name="\"Shape\"")
public class Shape implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	public static class Attribute {
		private String name = null;
		private String type = null;
		private String taxonomy = null;
		private String value = null;

		public Attribute(String name, String type, String taxonomy, String value) {
			this.name = name;
			this.type = type;
			this.taxonomy = taxonomy;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTaxonomy() {
			return taxonomy;
		}

		public void setTaxonomy(String taxonomy) {
			this.taxonomy = taxonomy;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public Shape(){
	}
	
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"SHP_ID\"", nullable = false)
	private UUID id = null;
	
	@Column(name="\"SHP_Code\"", length = 20, nullable = true) //nullable, urban planning code
	private String code = null;
	
	@Column(name="\"SHP_Name\"", length = 100) //TODO nullable?
	private String name = null;
	
	/**
	 * Internal enumerator of shapes.
	 * Several classes of shapes joined comprise a layer.
	 * Layers are described in SysConfig entries
	 */
	@Column(name="\"SHP_Class\"", nullable = false)
	private int shapeClass = -1;

	//@Type(type = "org.hibernate.spatial.GeometryType") //DEPWARN dependency to Hibernate spatial
	@Column(name="\"SHP_Geography\"", nullable = false, columnDefinition = "Geography") //DEPWARN dependency to PostGIS column def
	private Geometry geography = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SHP_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SHP_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"SHP_Creator\"", nullable = false)
	private UUID creatorID = null;
	
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "\"SHP_ExtraData\"", columnDefinition = "xml") //DEPWARN possible db portability issue
	private String extraData;
	
	
//	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
//	@Column(name="\"SHP_ShapeImport\"")
//	private UUID shapeImport = null;
	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"SHP_LayerID\"", nullable = false)
	private UUID layerID = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getShapeClass() {
		return shapeClass;
	}

	public void setShapeClass(int shapeClass) {
		this.shapeClass = shapeClass;
	}

	public Geometry getGeography() {
		return geography;
	}

	public void setGeography(Geometry geography) throws Exception {
		this.geography = geography;
		if (this.geography.getSRID() == 8307)
			this.geography.setSRID(4326); // replace 8307 with 4326 (both
											// representing WGS84)
//		if (this.geography.getSRID() != 4326)
//			throw new SRSException("", Integer.toString(geography.getSRID()), null);
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

	public UUID getCreatorID() {
		return creatorID;
	}

	public void setCreatorID(UUID creatorID) {
		this.creatorID = creatorID;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

//	public UUID getShapeImport() {
//		return shapeImport;
//	}
//	
//	public void setShapeImport(UUID shapeImport) {
//		this.shapeImport = shapeImport;
//	}
	
	public UUID getLayerID() {
		return layerID;
	}

	public void setLayerID(UUID layerID) {
		this.layerID = layerID;
	}
	
	
	@Override
	public String toString() {
		return "Shape(" + "id=" + getId() + " name=" + getName() + " code=" + getCode() + " class=" + getClass()
				+ " geography=" + getGeography() + " creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate()
				+ " creator=" + (creatorID != null ? creatorID.toString() : null) + " extraData=" + getExtraData();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Shape))
			return false;
		Shape other = (Shape) obj;
		boolean eq = this.getId().equals(other.getId()) && this.getExtraData().equals(other.getExtraData())
				&& this.getGeography().equals(other.getGeography());
		return this.getName() != null ? (eq && this.getName().equals(other.getName())) : eq;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result += 37 * result + id.hashCode();
		if (getName() != null)
			result += 37 * result + name.hashCode();
		result += 37 * result + geography.hashCode();
		if(extraData != null)
			result += 37 * result + extraData.hashCode();
		return result;
	}
}

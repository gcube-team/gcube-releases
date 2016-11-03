package gr.cite.geoanalytics.dataaccess.entities.shape;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.exception.SRSException;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Index;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Geometry;

@Entity
@Table(name = "\"ShapeImport\"")
public class ShapeImport implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable
{

	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"SHPI_ID\"", nullable = false)
	private UUID id = null;
	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"SHPI_Import\"", nullable = false)
	private UUID shapeImport = null;
	
	@Type(type = "org.hibernate.spatial.GeometryType") //DEPWARN dependency to Hibernate spatial
	@Column(name="\"SHPI_Geography\"", nullable = false, columnDefinition = "Geography") //DEPWARN dependency to PostGIS column def
	private Geometry geography = null;
	
	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Column(name="\"SHPI_Data\"", columnDefinition = "xml") //DEPWARN possible db portability issue
	private String data = null;
	
	@Column(name="\"SHPI_ShapeIdentity\"", length = 250)
	private String shapeIdentity = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SHPI_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SHPI_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@ManyToOne
	@JoinColumn(name = "\"SHPI_Creator\"", nullable = false)
	private Principal creator = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getShapeImport() {
		return shapeImport;
	}

	public void setShapeImport(UUID shapeImport) {
		this.shapeImport = shapeImport;
	}

	public Geometry getGeography() {
		return geography;
	}

	public void setGeography(Geometry geography) throws Exception {
		this.geography = geography;
		if (this.geography.getSRID() == 8307)
			this.geography.setSRID(4326); // replace 8307 with 4326 (both
											// representing WGS84)
		if (this.geography.getSRID() != 4326)
			throw new SRSException("", Integer.toString(geography.getSRID()), null);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getShapeIdentity() {
		return shapeIdentity;
	}

	public void setShapeIdentity(String shapeIdentity) {
		this.shapeIdentity = shapeIdentity;
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

	@Override
	public String toString() {
		return "ShapeImport(" + "id=" + getId() + " shapeImport=" + getShapeImport() + " geography=" + getGeography()
				+ "data=" + getData() + " shapeIdentity=" + getShapeIdentity() + " creation=" + getCreationDate()
				+ " lastUpdate=" + getLastUpdate() + " creator=" + (creator != null ? creator.getId() : null);
	}
}

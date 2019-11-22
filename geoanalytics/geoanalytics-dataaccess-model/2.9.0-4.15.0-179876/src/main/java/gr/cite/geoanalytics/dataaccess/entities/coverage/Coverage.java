package gr.cite.geoanalytics.dataaccess.entities.coverage;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;

@Entity
@Table(name = "\"Coverage\"")
public class Coverage implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {

	@Id
	@Type(type = "org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"CVRG_ID\"", nullable = false)
	private UUID id = null;

	@Column(name = "\"CVRG_Name\"", length = 100, nullable = false)
	private String name = null;

	@Column(name = "\"CVRG_Image\"", nullable = false)
	private byte[] image = null;

	@Type(type = "org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"CVRG_LayerID\"", nullable = false)
	private UUID layerID = null;

	@Type(type = "gr.cite.geoanalytics.dataaccess.typedefinition.XMLType")
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "\"CVRG_ExtraData\"", columnDefinition = "xml")
	private String extraData;

	@Type(type = "org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"CVRG_Creator\"", nullable = false)
	private UUID creator = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"CVRG_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"CVRG_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	
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

	
	public byte[] getImage() {
		return image;
	}

	
	public void setImage(byte[] image) {
		this.image = image;
	}

	
	public UUID getLayerID() {
		return layerID;
	}

	
	public void setLayerID(UUID layerID) {
		this.layerID = layerID;
	}

	
	public String getExtraData() {
		return extraData;
	}

	
	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	
	public UUID getCreator() {
		return creator;
	}

	
	public void setCreator(UUID creator) {
		this.creator = creator;
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
	public String toString() {
		return "Coverage{" +
				"id=" + id +
				", name='" + name + '\'' +
				", layerID=" + layerID +
				", extraData='" + extraData + '\'' +
				", creator=" + creator +
				", creationDate=" + creationDate +
				", lastUpdate=" + lastUpdate +
				'}';
	}
}

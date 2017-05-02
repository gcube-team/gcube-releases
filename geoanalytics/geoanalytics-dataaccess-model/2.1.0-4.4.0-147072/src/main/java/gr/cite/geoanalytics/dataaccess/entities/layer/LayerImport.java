package gr.cite.geoanalytics.dataaccess.entities.layer;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import java.io.Serializable;

@Entity
@Table(name = "\"LayerImport\"")
public class LayerImport implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Serializable, Stampable {

	private static final long serialVersionUID = 612031446706140664L;

	public static final String TYPE_TSV = "TSV";
	public static final String TYPE_WFS = "WFS";
	public static final String TYPE_SHAPEFILE = "ShapeFile";

	@Id
	@Type(type = "org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"LI_ID\"", nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	@JoinColumn(name = "\"LI_Layer\"", nullable = true)
	private Layer layer = null;

	@Column(name = "\"LI_Status\"", nullable = false)
	private Short status = 0;

	@Column(name = "\"LI_Type\"", nullable = false, length = 250)
	private String type = null;

	@ManyToOne(fetch = FetchType.EAGER)
	
	@JoinColumn(name = "\"LI_Creator\"", nullable = false)
	@JsonIgnore
	private Principal creator = null;

	@Column(name = "\"LI_GeocodeSystem\"", nullable = true, length = 250)
	private String geocodeSystem = null;

	@Column(name = "\"LI_Name\"", nullable = false, length = 250)
	private String name = null;

	@Column(name = "\"LI_FileName\"", nullable = true, length = 250)
	private String fileName = null;

	@Column(name = "\"LI_Description\"", nullable = true)
	private String description = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LI_CreationDate\"", nullable = false)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm")
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LI_LastUpdate\"", nullable = false)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm")
	private Date lastUpdate = null;

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	public String getGeocodeSystem() {
		return geocodeSystem;
	}

	public void setGeocodeSystem(String geocodeSystem) {
		this.geocodeSystem = geocodeSystem;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "LayerImport [id=" + id + ", " + "layer=" + layer + ", " + "status=" + status + "," + "name=" + name + "," + "geocodeSystem=" + geocodeSystem + "," + "filename="
				+ fileName + "," + "creator=" + creator + "," + "creationDate=" + creationDate + "," + "lastUpdate=" + lastUpdate + "]";
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
}
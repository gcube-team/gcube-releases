package gr.cite.geoanalytics.dataaccess.entities.layer;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

	@Column(name = "\"LI_ImportType\"", nullable = false, length = 250)
	private String importType = null;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "\"LI_DataSource\"", nullable = true, length = 250)
	private DataSource dataSource = null;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "\"LI_Creator\"", nullable = false)
	@JsonIgnore
	private Principal creator = null;

	@Column(name = "\"LI_GeocodeSystem\"", nullable = true, length = 250)
	private String geocodeSystem = null;

	@Column(name = "\"LI_Name\"", nullable = false, length = 250)
	private String name = null;

	@Column(name = "\"LI_Source\"", nullable = true, length = 250)
	private String source = null;

	@Column(name = "\"LI_Description\"", nullable = true)
	private String description = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LI_CreationDate\"", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LI_LastUpdate\"", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
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

	public String getImportType() {
		return importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "LayerImport [id=" + id + ", " + "layer=" + layer + ", " + "status=" + status + "," + "name=" + name + "," + "geocodeSystem=" + geocodeSystem + "," + "filename="
				+ source + "," + "creator=" + creator + "," + "creationDate=" + creationDate + "," + "lastUpdate=" + lastUpdate + "]";
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
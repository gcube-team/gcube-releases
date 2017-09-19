package gr.cite.geoanalytics.dataaccess.entities.layer;

import java.io.Serializable;
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

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

/**
 * @author vfloros
 *
 */
@Entity
@Table(name = "\"LayerVisualization\"")
public class LayerVisualization implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Serializable, Stampable {

	private static final long serialVersionUID = 11L;
	
	@Id
	@Type(type = "org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"LV_ID\"")
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"LV_Layer\"", nullable = false)
	private Layer layer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"LV_Tenant\"", nullable = false)
	private Tenant tenant;
	
	@Type(type = "gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") 
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "\"LV_AttributeVisualization\"", columnDefinition = "xml")
	private String attributeVisualization;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LV_CreationDate\"", nullable = false)
	private Date creationDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LV_LastUpdate\"", nullable = false)
	private Date lastUpdate;
	
	public LayerVisualization() {}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getAttributeVisualization() {
		return attributeVisualization;
	}

	public void setAttributeVisualization(String attributeVisualization) {
		this.attributeVisualization = attributeVisualization;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((layer == null) ? 0 : layer.hashCode());
		result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
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
		LayerVisualization other = (LayerVisualization) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (layer == null) {
			if (other.layer != null)
				return false;
		} else if (!(layer.equals(other.layer) && tenant.equals(tenant) && id.equals(other.id)))
			return false;
		if (tenant == null) {
			if (other.tenant != null)
				return false;
		} else if (!(layer.equals(other.layer) && tenant.equals(tenant) && id.equals(other.id)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LayerVisualization [id=" + id + ", layer=" + layer + ", tenant=" + tenant + ", attributeVisualization="
				+ attributeVisualization + ", creationDate=" + creationDate + ", lastUpdate=" + lastUpdate + "]";
	}
}

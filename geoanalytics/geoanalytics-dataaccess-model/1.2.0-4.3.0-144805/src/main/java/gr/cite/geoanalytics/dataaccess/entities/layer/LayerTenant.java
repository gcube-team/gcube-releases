package gr.cite.geoanalytics.dataaccess.entities.layer;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import java.io.Serializable;

@Entity
@Table(name = "\"LayerTenant\"")
public class LayerTenant implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Serializable {
	
	private static final long serialVersionUID = 1362579047583240048L;

	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "lt_id", nullable = false)
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"TEN_ID\"", nullable = false)
	private Tenant tenant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "l_id", nullable = false)
	private Layer layer = null;	

	public Layer getLayer() {
		return this.layer;
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

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;		
	}
}

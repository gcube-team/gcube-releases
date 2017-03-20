package gr.cite.geoanalytics.dataaccess.entities.geocode;

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
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;

@Entity
@Table(name = "\"TaxonomyLayer\"")
public class TaxonomyLayer implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable{
	
	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="tl_id", nullable = false)
	private UUID tl_id = null;
	
	
//	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
//	@Column(name="l_id", nullable = false)
//	private UUID l_id = null;
//	
//	
//	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
//	@Column(name="\"TAX_ID\"", nullable = false)
//	private UUID tax_id = null;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="\"TAX_ID\"", nullable = false)
	private GeocodeSystem taxonomy = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "l_id", nullable = false)
	private Layer layer = null;
	

	public TaxonomyLayer(){
	}
	
	public TaxonomyLayer(UUID id, GeocodeSystem taxonomy, Layer layer){
		this.tl_id = id;
		this.taxonomy = taxonomy;
		this.layer = layer;
	}
	
	public UUID getId() {
		return tl_id;
	}


	public void setId(UUID tl_id) {
		this.tl_id = tl_id;
	}

	
	public Layer getLayer() {
		return layer;
	}


	public void setLayer(Layer layer) {
		this.layer = layer;
	}


	public GeocodeSystem getTaxonomy() {
		return taxonomy;
	}


	public void setTaxonomy(GeocodeSystem taxonomy) {
		this.taxonomy = taxonomy;
	}
}

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

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import java.io.Serializable;

@Entity
@Table(name = "\"LayerTag\"")
public class LayerTag implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Serializable, Stampable {

	private static final long serialVersionUID = -424508685277077232L;

	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"LTAG_ID\"", nullable = false)
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"LTAG_Layer\"", nullable = false)
	private Layer layer = null;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"LTAG_Tag\"", nullable = false)
	private Tag tag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LTAG_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"LTAG_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;		
	}
	
	@Override
	public String toString() {
		return "LayerTag[id=" 			+ getId() +
				", layer=" 			+ getLayer() + 
				", tag= " 	+ getTag() + "]";
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
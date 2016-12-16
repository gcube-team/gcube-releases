package gr.cite.geoanalytics.dataaccess.entities.shape;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;

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

@Entity
@Table(name = "\"ShapeDocument\"")
public class ShapeDocument implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable
{
	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"SD_ID\"", nullable = false)
	private UUID id = null;
	
	@ManyToOne
	@JoinColumn(name = "\"SD_TaxonomyTermShape\"", nullable = false)
	private TaxonomyTermShape taxonomyTermShape = null;
	
	@ManyToOne
	@JoinColumn(name = "\"SD_Document\"", nullable = false)
	private Document document = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SD_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SD_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"SD_Creator\"", nullable = false)
	private Principal creator = null;

	public ShapeDocument() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	public TaxonomyTermShape getTaxonomyTermShape() {
		return taxonomyTermShape;
	}

	public void setTaxonomyTermShape(TaxonomyTermShape taxonomyTermShape) {
		this.taxonomyTermShape = taxonomyTermShape;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creation) {
		this.creationDate = creation;
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
		return "ShapeDocument(" + "taxonomyTermShape=" + getTaxonomyTermShape().getId() + " name="
				+ getDocument().getId() + " creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate()
				+ " creator=" + (creator != null ? creator.getId() : null);
	}

}

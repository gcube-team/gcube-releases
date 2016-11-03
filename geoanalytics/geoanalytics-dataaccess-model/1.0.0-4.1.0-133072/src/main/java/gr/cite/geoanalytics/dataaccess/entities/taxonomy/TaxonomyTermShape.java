package gr.cite.geoanalytics.dataaccess.entities.taxonomy;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"TaxonomyTermShape\"")
public class TaxonomyTermShape implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable
{

	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType")
	@Column(name = "\"TAXTS_ID\"", nullable = false)
	private UUID id = null;

	/*@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE}) //TODO ManyToOne? */
	@OneToOne
	@JoinColumn(name = "\"TAXTS_Term\"", nullable = false)
	private TaxonomyTerm term = null;

	@OneToOne //TODO ManyToOne?
	@JoinColumn(name = "\"TAXTS_Shape\"", nullable = false)
	private Shape shape = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAXTS_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAXTS_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"TAXTS_Creator\"", nullable = false)
	private Principal creator = null;

	public TaxonomyTermShape() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public TaxonomyTerm getTerm() {
		return term;
	}

	public void setTerm(TaxonomyTerm term) {
		this.term = term;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
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
	public String toString()
	{
		return "TaxonomyTermShape(" + "id=" + getId() + 
				" term=" + (term != null ? term.getId() : null) + 
				" shape=" + (shape != null ? shape.getId() : null) + 
				" creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj)
			return true;
		if(!(obj instanceof TaxonomyTermShape))
			return false;
		
		TaxonomyTermShape other = (TaxonomyTermShape)obj;
		
		
		boolean eq = this.getId().equals(other.getId()) &&
				this.shape.equals(other.getShape()) &&
				this.term.getId().equals(other.getTerm().getId()) &&
				this.term.getName().equals(other.getTerm().getName()) &&
				this.term.getTaxonomy().getId().equals(other.getTerm().getTaxonomy().getId()) &&
				this.term.getTaxonomy().getName().equals(other.getTerm().getTaxonomy().getName());
		return eq;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result += 37 * result + id.hashCode();
		result += 37 * result + shape.hashCode();
		result += 37 * result + term.getId().hashCode();
		result += 37 * result + term.getName().hashCode();
		result += 37 * result + term.getTaxonomy().getId().hashCode();
		result += 37 * result + term.getTaxonomy().getName().hashCode();
		return result;
	}
}

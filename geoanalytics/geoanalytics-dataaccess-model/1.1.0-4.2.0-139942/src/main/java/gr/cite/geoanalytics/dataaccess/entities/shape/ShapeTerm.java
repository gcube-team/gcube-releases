package gr.cite.geoanalytics.dataaccess.entities.shape;

import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@IdClass(ShapeTermPK.class)
@Table(name="\"ShapeTerm\"")
public class ShapeTerm implements gr.cite.geoanalytics.dataaccess.entities.Entity, Stampable
{
	@Id
	@OneToOne
	@JoinColumn(name = "\"SHPT_Shape\"", nullable = false)
	private Shape shape = null;
	
	@Id
	@ManyToOne
	@JoinColumn(name = "\"SHPT_Term\"", nullable = false)
	private TaxonomyTerm term = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SHPT_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"SHPT_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@ManyToOne
	@JoinColumn(name = "\"SHPT_Creator\"", nullable = false)
	private Principal creator = null;
	

//	@AssociationOverrides({
//		@AssociationOverride(name="shape", 
//			joinColumns = @JoinColumn(name = "SHPT_Shape")),
//		@AssociationOverride(name="term", 
//			joinColumns = @JoinColumn(name = "SHPT_Term")) })
	
	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public TaxonomyTerm getTerm() {
		return term;
	}

	public void setTerm(TaxonomyTerm term) {
		this.term = term;
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
		return "ShapeTerm(" + " shape=" + (shape != null ? shape : null) + " term=" + (term != null ? term : null)
				+ " creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() + " creator="
				+ (creator != null ? creator.getId() : null);
	}
}

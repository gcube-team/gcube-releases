package gr.cite.geoanalytics.dataaccess.entities.taxonomy;

import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@IdClass(TaxonomyTermLinkPK.class)
@Table(name = "\"TaxonomyTermLink\"")
public class TaxonomyTermLink implements gr.cite.geoanalytics.dataaccess.entities.Entity, Stampable {
	public enum Verb
	{
		Equivalent((int)0), Contains((int)1), LayerFor((int)2), LandUseFor((int)3), POIFor((int)4), SiteFor((int)5), AttrFor((int)6);
		
		private final int verbCode;
		
		private static final Map<Integer,Verb> lookup  = new HashMap<Integer,Verb>();
		 
		static {
		      for(Verb s : EnumSet.allOf(Verb.class))
		           lookup.put(s.verbCode(), s);
		 }
		
		Verb(int verbCode)
		{
			this.verbCode = verbCode;
		}
		
		public int verbCode() { return verbCode; }
	
		public static Verb fromVerbCode(int verbCode)
		{
			return lookup.get(verbCode);
		}
	};
	
	@Id
	@ManyToOne
	@JoinColumn(name = "\"TAXTL_SourceTerm\"", nullable = false)
	private TaxonomyTerm sourceTerm = null;
	
	@Id
	@ManyToOne
	@JoinColumn(name = "\"TAXTL_DestinationTerm\"", nullable = false)
	private TaxonomyTerm destinationTerm = null;
	
	@Column(name = "\"TAXTL_Verb\"", nullable = false)
	private int verb = 0;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAXTL_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"TAXTL_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"TAXTL_Creator\"", nullable = false)
	private Principal creator;

	public TaxonomyTermLink() {
	}
	
	public TaxonomyTerm getDestinationTerm() {
		return destinationTerm;
	}

	public void setDestinationTerm(TaxonomyTerm destinationTerm) {
		this.destinationTerm = destinationTerm;
	}

	public TaxonomyTerm getSourceTerm() {
		return sourceTerm;
	}

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	public void setSourceTerm(TaxonomyTerm sourceTerm) {
		this.sourceTerm = sourceTerm;
	}

	public Verb getVerb() {
		return Verb.fromVerbCode(this.verb);
	}

	public void setVerb(Verb verb) {
		this.verb = verb.verbCode();
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
		return "TaxonomyTermLink(" + " sourceTerm=" + (sourceTerm != null ? sourceTerm.getId() : null) + " destinationTerm="
				+ (destinationTerm != null ? destinationTerm.getId() : null) + " creation=" + getCreationDate() + " lastUpdate="
				+ getLastUpdate() + " creator=" + (creator != null ? creator.getId() : null);
	}

}

